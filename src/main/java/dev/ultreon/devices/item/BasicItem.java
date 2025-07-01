package dev.ultreon.devices.item;

import dev.ultreon.devices.Devices;
import dev.ultreon.quantum.item.Item;

/**
 * @author MrCrayfish
 */
@SuppressWarnings("UnstableApiUsage")
public class BasicItem extends Item {
    public BasicItem(Properties properties) {
        super(properties.arch$tab(Devices.TAB_DEVICE));
    }
}
