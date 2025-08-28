package dev.ultreon.devices.item;

import dev.ultreon.devices.DeviceConfig;
import dev.ultreon.devices.block.entity.NetworkDeviceBlockEntity;
import dev.ultreon.devices.block.entity.RouterBlockEntity;
import dev.ultreon.devices.core.network.Router;
import dev.ultreon.devices.init.CableData;
import dev.ultreon.devices.init.DeviceDataComponents;
import dev.ultreon.devices.util.KeyboardHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
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

/// @author MrCrayfish
public class EthernetCableItem extends Item {
    public EthernetCableItem() {
        super(new Properties().stacksTo(1));
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

        if (!level.isClientSide && player != null) {
            ItemStack heldItem = player.getItemInHand(hand);
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (blockEntity instanceof RouterBlockEntity routerBE) {
                if (!heldItem.has(DeviceDataComponents.CABLE_DATA.get())) {
                    sendGameInfoMessage(player, "message.devices.invalid_cable");
                    return InteractionResult.SUCCESS;
                }

                Router router = routerBE.getRouter();

                CableData tag = heldItem.get(DeviceDataComponents.CABLE_DATA.get());
                assert tag != null;
                BlockPos devicePos = tag.pos();

                BlockEntity tileEntity1 = level.getBlockEntity(devicePos);
                if (tileEntity1 instanceof NetworkDeviceBlockEntity networkDeviceBlockEntity) {
                    if (!router.isDeviceRegistered(networkDeviceBlockEntity)) {
                        if (router.addDevice(networkDeviceBlockEntity)) {
                            networkDeviceBlockEntity.connect(router);
                            heldItem.shrink(1);
                            if (getDistance(tileEntity1.getBlockPos(), routerBE.getBlockPos()) > DeviceConfig.SIGNAL_RANGE.get()) {
                                sendGameInfoMessage(player, "message.devices.successful_registered");
                            } else {
                                sendGameInfoMessage(player, "message.devices.successful_connection");
                            }
                        } else {
                            sendGameInfoMessage(player, "message.devices.router_max_devices");
                        }
                    } else {
                        sendGameInfoMessage(player, "message.devices.device_already_connected");
                    }
                } else {
                    if (router.addDevice(tag.id(), tag.name())) {
                        heldItem.shrink(1);
                        sendGameInfoMessage(player, "message.devices.successful_registered");
                    } else {
                        sendGameInfoMessage(player, "message.devices.router_max_devices");
                    }
                }
                return InteractionResult.SUCCESS;
            }

            if (blockEntity instanceof NetworkDeviceBlockEntity networkDeviceBlockEntity) {
                CableData cableData = new CableData(
                        networkDeviceBlockEntity.getBlockPos(),
                        networkDeviceBlockEntity.getId(),
                        networkDeviceBlockEntity.getCustomName()
                );

                heldItem.set(DeviceDataComponents.CABLE_DATA.get(), cableData);
                sendGameInfoMessage(player, "message.devices.select_router");
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.SUCCESS;
    }

    private void sendGameInfoMessage(Player player, String message) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.translatable(message));
        }
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        if (!level.isClientSide) {
            ItemStack heldItem = player.getItemInHand(usedHand);
            if (player.isCrouching()) {
                heldItem.remove(DataComponents.CUSTOM_NAME);
                heldItem.remove(DeviceDataComponents.CABLE_DATA.get());
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, heldItem);
            }
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        if (stack.has(DeviceDataComponents.CABLE_DATA.get())) {
            CableData tag = stack.get(DeviceDataComponents.CABLE_DATA.get());

            if (tag != null) {
                tooltipComponents.add(Component.literal(ChatFormatting.GRAY + "Connected to: " + ChatFormatting.RESET + tag.name()));
                tooltipComponents.add(Component.literal(ChatFormatting.GRAY + "Located at: " + ChatFormatting.RESET + tag.pos().getX() + ", " + tag.pos().getY() + ", " + tag.pos().getZ()));

                if (tooltipFlag.isAdvanced()) {
                    tooltipComponents.add(Component.empty());
                    tooltipComponents.add(Component.literal(ChatFormatting.RED.toString() + ChatFormatting.BOLD + "ID: " + ChatFormatting.RESET + tag.id()));
                    tooltipComponents.add(Component.literal(ChatFormatting.RED.toString() + ChatFormatting.BOLD + "Device: " + ChatFormatting.RESET + tag.name()));

                    BlockPos devicePos = tag.pos();
                    String text = ChatFormatting.RED.toString() + ChatFormatting.BOLD + "X: " + ChatFormatting.RESET + devicePos.getX() + " " +
                                  ChatFormatting.RED + ChatFormatting.BOLD + "Y: " + ChatFormatting.RESET + devicePos.getY() + " " +
                                  ChatFormatting.RED + ChatFormatting.BOLD + "Z: " + ChatFormatting.RESET + devicePos.getZ();
                    tooltipComponents.add(Component.literal(text));
                }
            }
        } else {
            if (!KeyboardHelper.isShiftDown()) {
                tooltipComponents.add(Component.literal(ChatFormatting.GRAY + "Use this cable to connect"));
                tooltipComponents.add(Component.literal(ChatFormatting.GRAY + "a device to a router."));
                tooltipComponents.add(Component.literal(ChatFormatting.YELLOW + "Hold SHIFT for How-To"));
                return;
            }

            tooltipComponents.add(Component.literal(ChatFormatting.GRAY + "Start by right clicking a"));
            tooltipComponents.add(Component.literal(ChatFormatting.GRAY + "device with this cable"));
            tooltipComponents.add(Component.literal(ChatFormatting.GRAY + "then right click the "));
            tooltipComponents.add(Component.literal(ChatFormatting.GRAY + "router you want to"));
            tooltipComponents.add(Component.literal(ChatFormatting.GRAY + "connect this device to."));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.has(DeviceDataComponents.CABLE_DATA.get());
    }

    @NotNull
    @Override
    public Component getName(ItemStack stack) {
        if (stack.has(DeviceDataComponents.CABLE_DATA.get()))
            return super.getDescription().copy().withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD);

        return super.getName(stack);
    }
}
