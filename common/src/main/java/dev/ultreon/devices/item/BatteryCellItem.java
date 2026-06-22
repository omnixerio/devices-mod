package dev.ultreon.devices.item;

import dev.ultreon.devices.ModDeviceTypes;
import dev.ultreon.devices.init.Battery;
import dev.ultreon.devices.init.ModDataComponents;
import net.minecraft.world.item.Item;

public class BatteryCellItem extends Item {
    public static final int BATTERY_CAPACITY = 10000;

    public BatteryCellItem(Properties properties) {
        super(properties.component(ModDataComponents.BATTERY.get(), new Battery(BATTERY_CAPACITY, 0)));
    }
}
