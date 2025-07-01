package dev.ultreon.devices.util;

import dev.ultreon.devices.DyeColor;
import net.minecraft.world.item.DyeColor;

public interface Colorable extends Colored {
    DyeColor getColor();

    void setColor(DyeColor color);
}
