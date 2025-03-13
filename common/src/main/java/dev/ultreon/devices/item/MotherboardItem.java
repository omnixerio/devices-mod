package dev.ultreon.devices.item;

import dev.ultreon.devices.init.DeviceDataComponents;
import dev.ultreon.devices.init.HardwareComponents;
import dev.ultreon.devices.util.KeyboardHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/// @author MrCrayfish
public class MotherboardItem extends ComponentItem {
    public MotherboardItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @NotNull TooltipContext context, @NotNull List<net.minecraft.network.chat.Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        HardwareComponents tag = stack.get(DeviceDataComponents.HARDWARE_COMPONENTS.get());
        if (tag == null) {
            tooltipComponents.add(net.minecraft.network.chat.Component.literal("Invalid Motherboard").withStyle(ChatFormatting.RED));
            return;
        }
        if (!KeyboardHelper.isShiftDown()) {
            tooltipComponents.add(net.minecraft.network.chat.Component.literal(ChatFormatting.GRAY + "CPU: " + getComponentStatus(tag.cpu())));
            tooltipComponents.add(net.minecraft.network.chat.Component.literal(ChatFormatting.GRAY + "RAM: " + getComponentStatus(tag.ram())));
            tooltipComponents.add(net.minecraft.network.chat.Component.literal(ChatFormatting.GRAY + "GPU: " + getComponentStatus(tag.gpu())));
            tooltipComponents.add(net.minecraft.network.chat.Component.literal(ChatFormatting.GRAY + "WIFI: " + getComponentStatus(tag.wifi())));
            tooltipComponents.add(net.minecraft.network.chat.Component.literal(ChatFormatting.YELLOW + "Hold shift for help"));
        } else {
            tooltipComponents.add(net.minecraft.network.chat.Component.literal("To add the required components"));
            tooltipComponents.add(net.minecraft.network.chat.Component.literal("place the motherboard and the"));
            tooltipComponents.add(net.minecraft.network.chat.Component.literal("corresponding component into a"));
            tooltipComponents.add(net.minecraft.network.chat.Component.literal("crafting table to combine them."));
        }
    }

    private String getComponentStatus(ItemStack cpu) {
        if (cpu != null) return ChatFormatting.GREEN + "Added";
        return ChatFormatting.RED + "Missing";
    }

    private String getComponentStatus(CompoundTag tag, String component) {
        if (tag != null && tag.contains("components", Tag.TAG_COMPOUND)) {
            CompoundTag components = tag.getCompound("components");
            if (components.contains(component, Tag.TAG_BYTE)) {
                return ChatFormatting.GREEN + "Added";
            }
        }
        return ChatFormatting.RED + "Missing";
    }

    public static class Component extends ComponentItem {
        public Component(Properties properties) {
            super(properties);
        }
    }
}
