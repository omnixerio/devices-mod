package dev.ultreon.devices;

import dev.ultreon.devices.init.DeviceItems;
import dev.ultreon.quantum.item.ItemStack;
import dev.ultreon.quantum.item.group.ItemGroup;
import dev.ultreon.quantum.item.group.ItemGroups;
import dev.ultreon.quantum.text.TextObject;

public class DeviceTab {
    private DeviceTab() {
        throw new AssertionError("Utility class");
    }

    public static ItemGroup create() {
        Devices.LOGGER.info("Creating Creative Tab...");
        return ItemGroups.register(new ItemGroup(TextObject.literal("Ultreon Devices"), () -> new ItemStack(DeviceItems.LAPTOPS.of(DyeColor.RED).get())));
    }
}
