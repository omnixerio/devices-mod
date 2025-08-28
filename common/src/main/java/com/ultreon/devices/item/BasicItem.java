package com.ultreon.devices.item;

import com.ultreon.devices.Devices;
import net.minecraft.world.item.Item;

/**
 * @author MrCrayfish
 */
@SuppressWarnings("UnstableApiUsage")
public class BasicItem extends Item {
    public BasicItem(Properties properties) {
        super(properties.arch$tab(Devices.TAB_DEVICE));
    }
}
