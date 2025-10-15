package com.ultreon.devices.item;

import com.ultreon.devices.DeviceConfig;
import com.ultreon.devices.Devices;
import com.ultreon.devices.block.entity.NetworkDeviceBlockEntity;
import com.ultreon.devices.block.entity.RouterBlockEntity;
import com.ultreon.devices.core.network.Router;
import com.ultreon.devices.debug.DebugLog;
import com.ultreon.devices.util.KeyboardHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author MrCrayfish
 */
public class EthernetCableItem extends Item {
    @SuppressWarnings("UnstableApiUsage")
    public EthernetCableItem() {
        super(new Properties().arch$tab(Devices.TAB_DEVICE).stacksTo(1));
    }

    private static double getDistance(BlockPos source, BlockPos target) {
        return Math.sqrt(source.distToCenterSqr(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        InteractionHand hand = context.getHand();

        if (level.isClientSide || player == null)
            return InteractionResult.SUCCESS;

        ItemStack heldItem = player.getItemInHand(hand);
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (!(blockEntity instanceof RouterBlockEntity routerBlockEntity)) {
            return selectDevice(blockEntity, heldItem, player);
        }
        if (!heldItem.hasTag()) {
            sendGameInfoMessage(player, "message.devices.invalid_cable");
            return InteractionResult.FAIL;
        }

        Router router = routerBlockEntity.getRouter();

        CompoundTag tag = heldItem.getTag();
        assert tag != null;

        BlockPos receiverPos = BlockPos.of(tag.getLong("pos"));
        BlockEntity receiverBlockEntity = level.getBlockEntity(receiverPos);

        if (!(receiverBlockEntity instanceof NetworkDeviceBlockEntity networkDevice)) {
            return registerUnknown(router, tag, heldItem, player);
        }

        DebugLog.log("Connecting network device.");
        if (router.isDeviceRegistered(networkDevice)) {
            DebugLog.log("Already connected");
            sendGameInfoMessage(player, "message.devices.device_already_connected");
            return InteractionResult.FAIL;
        }

        return tryConnect(routerBlockEntity, networkDevice, router, heldItem, receiverBlockEntity, player);
    }

    @NotNull
    private InteractionResult selectDevice(BlockEntity blockEntity, ItemStack heldItem, Player player) {
        if (!(blockEntity instanceof NetworkDeviceBlockEntity networkDevice))
            return InteractionResult.SUCCESS;

        heldItem.setTag(new CompoundTag());
        CompoundTag tag = heldItem.getTag();
        assert tag != null;
        tag.putUUID("id", networkDevice.getId());
        tag.putString("name", networkDevice.getCustomName());
        tag.putLong("pos", networkDevice.getBlockPos().asLong());

        sendGameInfoMessage(player, "message.devices.select_router");
        return InteractionResult.FAIL;
    }

    @NotNull
    private InteractionResult registerUnknown(Router router, CompoundTag tag, ItemStack heldItem, Player player) {
        DebugLog.log("Not a network device!");
        if (router.addDevice(tag.getUUID("id"), tag.getString("name"))) {
            DebugLog.log("Registered, but it's not a network device!");
            heldItem.shrink(1);
            sendGameInfoMessage(player, "message.devices.successful_registered");
            return InteractionResult.SUCCESS;
        }

        DebugLog.log("Max device limit reached!");
        sendGameInfoMessage(player, "message.devices.router_max_devices");
        return InteractionResult.FAIL;
    }

    @NotNull
    private InteractionResult tryConnect(RouterBlockEntity routerBlockEntity, NetworkDeviceBlockEntity toConnect,
                                         Router router, ItemStack heldItem, BlockEntity receiverBlockEntity, Player player) {
        DebugLog.log("Unregistered device " + toConnect);
        if (!router.addDevice(toConnect)) {
            DebugLog.log("Max device limit reached!");
            sendGameInfoMessage(player, "message.devices.router_max_devices");
            return InteractionResult.SUCCESS;
        }

        DebugLog.log("Adding device");
        toConnect.connect(router);
        if (!player.isCreative()) heldItem.shrink(1);

        if (getDistance(receiverBlockEntity.getBlockPos(), routerBlockEntity.getBlockPos()) > DeviceConfig.SIGNAL_RANGE.get()) {
            DebugLog.log("Registered, but too low signal!");
            sendGameInfoMessage(player, "message.devices.successful_registered");
            return InteractionResult.CONSUME_PARTIAL;
        }

        DebugLog.log("Connected!");
        sendGameInfoMessage(player, "message.devices.successful_connection");
        return InteractionResult.CONSUME_PARTIAL;
    }

    private void sendGameInfoMessage(Player player, String message) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.translatable(message));
        }
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        if (level.isClientSide)
            return super.use(level, player, usedHand);

        ItemStack heldItem = player.getItemInHand(usedHand);
        if (player.isCrouching()) {
            heldItem.resetHoverName();
            heldItem.setTag(null);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, heldItem);
        }

        return super.use(level, player, usedHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag isAdvanced) {
        if (!stack.hasTag()) {
            if (!KeyboardHelper.isShiftDown()) {
                tooltip.add(Component.literal(ChatFormatting.GRAY + "Use this cable to connect"));
                tooltip.add(Component.literal(ChatFormatting.GRAY + "a device to a router."));
                tooltip.add(Component.literal(ChatFormatting.YELLOW + "Hold SHIFT for How-To"));
                return;
            }

            tooltip.add(Component.literal(ChatFormatting.GRAY + "Start by right clicking a"));
            tooltip.add(Component.literal(ChatFormatting.GRAY + "device with this cable"));
            tooltip.add(Component.literal(ChatFormatting.GRAY + "then right click the "));
            tooltip.add(Component.literal(ChatFormatting.GRAY + "router you want to"));
            tooltip.add(Component.literal(ChatFormatting.GRAY + "connect this device to."));
        } else {
            CompoundTag tag = stack.getTag();
            if (tag != null) {
                tooltip.add(Component.literal(ChatFormatting.RED.toString() + ChatFormatting.BOLD + "ID: " + ChatFormatting.RESET + tag.getUUID("id")));
                tooltip.add(Component.literal(ChatFormatting.RED.toString() + ChatFormatting.BOLD + "Device: " + ChatFormatting.RESET + tag.getString("name")));

                BlockPos devicePos = BlockPos.of(tag.getLong("pos"));
                String text = ChatFormatting.RED.toString() + ChatFormatting.BOLD + "X: " + ChatFormatting.RESET + devicePos.getX() + " " +
                        ChatFormatting.RED + ChatFormatting.BOLD + "Y: " + ChatFormatting.RESET + devicePos.getY() + " " +
                        ChatFormatting.RED + ChatFormatting.BOLD + "Z: " + ChatFormatting.RESET + devicePos.getZ();
                tooltip.add(Component.literal(text));
            }
        }
        super.appendHoverText(stack, level, tooltip, isAdvanced);
    }

    @Environment(EnvType.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return stack.hasTag();
    }

    @NotNull
    @Override
    public Component getName(ItemStack stack) {
        if (stack.hasTag())
            return super.getDescription().copy().withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD);

        return super.getName(stack);
    }
}
