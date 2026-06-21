package dev.ultreon.devices.item;

import dev.ultreon.devices.DeviceConfig;
import dev.ultreon.devices.OmnixerioDevicesMod;
import dev.ultreon.devices.block.entity.NetworkDeviceBlockEntity;
import dev.ultreon.devices.block.entity.RouterBlockEntity;
import dev.ultreon.devices.core.network.Router;
import dev.ultreon.devices.init.ModDataComponents;
import dev.ultreon.devices.item.data.EthernetConnection;
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

/**
 * @author MrCrayfish
 */
public class EthernetCableItem extends Item {
    public EthernetCableItem(Properties properties) {
        super(properties.arch$tab(OmnixerioDevicesMod.TAB_DEVICE).stacksTo(1));
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
                final EthernetConnection connection = heldItem.get(ModDataComponents.ETHERNET_CONNECTION.get());
                if (connection == null) {
                    sendGameInfoMessage(player, "message.devices.invalid_cable");
                    return InteractionResult.SUCCESS;
                }

                Router router = routerBE.getRouter();

                BlockPos devicePos = connection.devicePos();

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
                    if (router.addDevice(connection.deviceUUID(), connection.deviceName())) {
                        heldItem.shrink(1);
                        sendGameInfoMessage(player, "message.devices.successful_registered");
                    } else {
                        sendGameInfoMessage(player, "message.devices.router_max_devices");
                    }
                }
                return InteractionResult.SUCCESS;
            }

            if (blockEntity instanceof NetworkDeviceBlockEntity networkDeviceBlockEntity) {
                heldItem.set(ModDataComponents.ETHERNET_CONNECTION.get(), new EthernetConnection(
                        networkDeviceBlockEntity.getBlockPos(),
                        networkDeviceBlockEntity.getId(),
                        networkDeviceBlockEntity.getCustomName()
                ));

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
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, heldItem);
            }
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        EthernetConnection connection = stack.get(ModDataComponents.ETHERNET_CONNECTION.get());
        if (connection != null) {
            tooltip.add(net.minecraft.network.chat.Component.literal(ChatFormatting.RED.toString() + ChatFormatting.BOLD + "ID: " + ChatFormatting.RESET + connection.deviceUUID()));
            tooltip.add(net.minecraft.network.chat.Component.literal(ChatFormatting.RED.toString() + ChatFormatting.BOLD + "Device: " + ChatFormatting.RESET + connection.deviceName()));

            BlockPos devicePos = connection.devicePos();
            String text = ChatFormatting.RED.toString() + ChatFormatting.BOLD + "X: " + ChatFormatting.RESET + devicePos.getX() + " " +
                    ChatFormatting.RED + ChatFormatting.BOLD + "Y: " + ChatFormatting.RESET + devicePos.getY() + " " +
                    ChatFormatting.RED + ChatFormatting.BOLD + "Z: " + ChatFormatting.RESET + devicePos.getZ();
            tooltip.add(net.minecraft.network.chat.Component.literal(text));
        } else if (KeyboardHelper.isShiftDown()) {
            tooltip.add(Component.literal(ChatFormatting.GRAY + "Start by right clicking a"));
            tooltip.add(Component.literal(ChatFormatting.GRAY + "device with this cable"));
            tooltip.add(Component.literal(ChatFormatting.GRAY + "then right click the "));
            tooltip.add(Component.literal(ChatFormatting.GRAY + "router you want to"));
            tooltip.add(Component.literal(ChatFormatting.GRAY + "connect this device to."));
        } else {
            tooltip.add(Component.literal(ChatFormatting.GRAY + "Use this cable to connect"));
            tooltip.add(Component.literal(ChatFormatting.GRAY + "a device to a router."));
            tooltip.add(Component.literal(ChatFormatting.YELLOW + "Hold SHIFT for How-To"));
            return;
        }
        super.appendHoverText(stack, context, tooltip, tooltipFlag);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return itemStack.has(ModDataComponents.ETHERNET_CONNECTION.get());
    }

    @NotNull
    @Override
    public Component getName(ItemStack stack) {
        if (stack.has(ModDataComponents.ETHERNET_CONNECTION.get())) {
            return super.getDescription().copy().withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD);
        }
        return super.getName(stack);
    }
}
