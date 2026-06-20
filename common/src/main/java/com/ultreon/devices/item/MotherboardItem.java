package com.ultreon.devices.item;

import com.ultreon.devices.init.ModDataComponents;
import com.ultreon.devices.item.data.MotherboardComponents;
import com.ultreon.devices.util.KeyboardHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * @author MrCrayfish
 */
public class MotherboardItem extends ComponentItem {
    public MotherboardItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        MotherboardComponents components = stack.get(ModDataComponents.MOTHERBOARD_COMPONENTS.get());
        if (components == null) {
            components = new MotherboardComponents(false, false, false, false);
            stack.set(ModDataComponents.MOTHERBOARD_COMPONENTS.get(), components);
        }

        if (!KeyboardHelper.isShiftDown()) {
            tooltip.add(net.minecraft.network.chat.Component.literal("CPU: " + (components.hasCpu() ? "Added" : "Missing")));
            tooltip.add(net.minecraft.network.chat.Component.literal("RAM: " + (components.hasRam() ? "Added" : "Missing")));
            tooltip.add(net.minecraft.network.chat.Component.literal("GPU: " + (components.hasGpu() ? "Added" : "Missing")));
            tooltip.add(net.minecraft.network.chat.Component.literal("WIFI: " + (components.hasWifi() ? "Added" : "Missing")));
            tooltip.add(net.minecraft.network.chat.Component.literal(ChatFormatting.YELLOW + "Hold shift for help"));
        } else {
            tooltip.add(net.minecraft.network.chat.Component.literal("To add the required components"));
            tooltip.add(net.minecraft.network.chat.Component.literal("place the motherboard and the"));
            tooltip.add(net.minecraft.network.chat.Component.literal("corresponding component into a"));
            tooltip.add(net.minecraft.network.chat.Component.literal("crafting table to combine them."));
        }
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
