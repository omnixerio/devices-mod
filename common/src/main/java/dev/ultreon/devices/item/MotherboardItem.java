package dev.ultreon.devices.item;

import dev.ultreon.devices.util.KeyboardHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

/**
 * @author MrCrayfish
 */
public class MotherboardItem extends ComponentItem {
    public MotherboardItem(Properties properties) {
        super(properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay display, Consumer<net.minecraft.network.chat.Component> builder, @NonNull TooltipFlag tooltipFlag) {
        MotherboardComponents tag = stack.get(DeviceDataComponents.MOTHERBOARD_COMPONENTS.get());
        builder.accept(net.minecraft.network.chat.Component.literal("Motherboard"));
        if (!KeyboardHelper.isShiftDown()) {
            builder.accept(net.minecraft.network.chat.Component.literal("CPU: " + getComponentStatus(tag, "cpu")));
            builder.accept(net.minecraft.network.chat.Component.literal("RAM: " + getComponentStatus(tag, "ram")));
            builder.accept(net.minecraft.network.chat.Component.literal("GPU: " + getComponentStatus(tag, "gpu")));
            builder.accept(net.minecraft.network.chat.Component.literal("WIFI: " + getComponentStatus(tag, "wifi")));
            builder.accept(net.minecraft.network.chat.Component.literal(ChatFormatting.YELLOW + "Hold shift for help"));
        } else {
            builder.accept(net.minecraft.network.chat.Component.literal("To add the required components"));
            builder.accept(net.minecraft.network.chat.Component.literal("place the motherboard and the"));
            builder.accept(net.minecraft.network.chat.Component.literal("corresponding component into a"));
            builder.accept(net.minecraft.network.chat.Component.literal("crafting table to combine them."));
        }
    }

    private String getComponentStatus(MotherboardComponents tag, String component) {
        return switch (component) {
            case "cpu" -> tag.cpu() ? "Installed" : "Missing";
            case "ram" -> tag.ram() ? "Installed" : "Missing";
            case "gpu" -> tag.gpu() ? "Installed" : "Missing";
            case "wifi" -> tag.wifi() ? "Installed" : "Missing";
            default -> "Unknown";
        };
    }

    public static class Component extends ComponentItem {
        public Component(Properties properties) {
            super(properties);
        }
    }
}
