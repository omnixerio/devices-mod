package dev.ultreon.devices.item;

import dev.ultreon.devices.Devices;
import dev.ultreon.quantum.item.Item;

public class ComponentItem extends Item {
    public ComponentItem(Properties pProperties) {
        super(pProperties.arch$tab(Devices.TAB_DEVICE));
    }
}
