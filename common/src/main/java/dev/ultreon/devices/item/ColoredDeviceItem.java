package dev.ultreon.devices.item;

import dev.ultreon.devices.ModDeviceTypes;
import dev.ultreon.devices.util.Colored;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ColoredDeviceItem extends DeviceItem implements Colored {
    private final DyeColor color;

    public ColoredDeviceItem(@NotNull Block block, Properties tab, DyeColor color, ModDeviceTypes deviceType) {
        super(block, tab, deviceType);
        this.color = color;
    }

    public DyeColor getColor() {
        return color;
    }
}
