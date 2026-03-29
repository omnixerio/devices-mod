package com.ultreon.devices.item;

import com.ultreon.devices.DeviceConfig;
import com.ultreon.devices.Devices;
import com.ultreon.devices.block.entity.NetworkDeviceBlockEntity;
import com.ultreon.devices.block.entity.RouterBlockEntity;
import com.ultreon.devices.core.network.Router;
import com.ultreon.devices.util.KeyboardHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author MrCrayfish
 */
public class EthernetCableItem extends Item {
    public EthernetCableItem() {
        super(new Properties().setId(ResourceKey.create(Registries.ITEM, Devices.id("ethernet_cable"))).stacksTo(1));
    }

    private static double getDistance(BlockPos source, BlockPos target) {
        return Math.sqrt(source.distToCenterSqr(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        InteractionHand hand = context.getHand();

        if (!level.isClientSide() && player != null) {
            ItemStack heldItem = player.getItemInHand(hand);
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (blockEntity instanceof RouterBlockEntity routerBE) {
                if (!heldItem.has(DeviceDataComponents.ETHERNET_CONNECTION.get())) {
                    sendGameInfoMessage(player, "message.devices.invalid_cable");
                    return InteractionResult.SUCCESS;
                }

                Router router = routerBE.getRouter();

                EthernetConnection tag = heldItem.get(DeviceDataComponents.ETHERNET_CONNECTION.get());
                assert tag != null;
                Optional<BlockPos> optionalDevicePos = tag.pos();
                if (optionalDevicePos.isEmpty()) {
                    sendGameInfoMessage(player, "message.devices.invalid_cable");
                    return InteractionResult.SUCCESS;
                }
                BlockPos devicePos = optionalDevicePos.get();

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
                    Optional<UUID> uuid = tag.connectedTo();
                    Optional<String> name = tag.name();
                    if (uuid.isPresent() && name.isPresent()) {
                        if (router.addDevice(uuid.orElseThrow(), name.orElseThrow())) {
                            heldItem.shrink(1);
                            sendGameInfoMessage(player, "message.devices.successful_registered");
                        } else {
                            sendGameInfoMessage(player, "message.devices.router_max_devices");
                        }
                    } else {
                        sendGameInfoMessage(player, "message.devices.invalid_cable");
                    }
                }
                return InteractionResult.SUCCESS;
            }

            if (blockEntity instanceof NetworkDeviceBlockEntity networkDeviceBlockEntity) {
                heldItem.set(DeviceDataComponents.ETHERNET_CONNECTION.get(), new EthernetConnection(Optional.of(networkDeviceBlockEntity.getBlockPos()), Optional.of(networkDeviceBlockEntity.getId()), Optional.of(networkDeviceBlockEntity.getCustomName())));
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

    @Override
    public InteractionResult use(Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        if (!level.isClientSide()) {
            ItemStack heldItem = player.getItemInHand(usedHand);
            if (player.isCrouching()) {
                DataComponentType<EthernetConnection> ethernetConnectionType = DeviceDataComponents.ETHERNET_CONNECTION.get();
                if (heldItem.has(ethernetConnectionType)) {
                    heldItem.remove(ethernetConnectionType);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        DataComponentType<EthernetConnection> type = DeviceDataComponents.ETHERNET_CONNECTION.get();
        if (stack.has(type)) {
            EthernetConnection ethernetConnection = stack.get(type);
            BlockPos devicePos = ethernetConnection.pos().orElse(null);
            UUID connectedTo = ethernetConnection.connectedTo().orElse(null);
            String name = ethernetConnection.name().orElse(null);
            if (connectedTo != null) {
                builder.accept(Component.literal(ChatFormatting.RED.toString() + ChatFormatting.BOLD + "ID: " + ChatFormatting.RESET + connectedTo));
            }
            if (name != null) {
                builder.accept(Component.literal(ChatFormatting.RED.toString() + ChatFormatting.BOLD + "Device: " + ChatFormatting.RESET + name));
            }

            if (devicePos != null) {
                String text = ChatFormatting.RED.toString() + ChatFormatting.BOLD + "X: " + ChatFormatting.RESET + devicePos.getX() + " " +
                        ChatFormatting.RED + ChatFormatting.BOLD + "Y: " + ChatFormatting.RESET + devicePos.getY() + " " +
                        ChatFormatting.RED + ChatFormatting.BOLD + "Z: " + ChatFormatting.RESET + devicePos.getZ();
                builder.accept(Component.literal(text));
            }
        } else {
            if (!KeyboardHelper.isShiftDown()) {
                builder.accept(Component.literal(ChatFormatting.GRAY + "Use this cable to connect"));
                builder.accept(Component.literal(ChatFormatting.GRAY + "a device to a router."));
                builder.accept(Component.literal(ChatFormatting.YELLOW + "Hold SHIFT for How-To"));
                return;
            }

            builder.accept(Component.literal(ChatFormatting.GRAY + "Start by right clicking a"));
            builder.accept(Component.literal(ChatFormatting.GRAY + "device with this cable"));
            builder.accept(Component.literal(ChatFormatting.GRAY + "then right click the "));
            builder.accept(Component.literal(ChatFormatting.GRAY + "router you want to"));
            builder.accept(Component.literal(ChatFormatting.GRAY + "connect this device to."));
        }
        super.appendHoverText(stack, context, display, builder, tooltipFlag);
    }

    public boolean hasEffect(ItemStack stack) {
        return stack.has(DeviceDataComponents.ETHERNET_CONNECTION.get());
    }

    @NotNull
    @Override
    public Component getName(ItemStack stack) {
        if (stack.has(DeviceDataComponents.ETHERNET_CONNECTION.get())) {
            return super.getName(stack).copy().withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD);
        }
        return super.getName(stack);
    }
}
