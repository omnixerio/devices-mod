package dev.ultreon.devices;

import dev.ultreon.devices.init.DeviceItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

public class DeviceTab {
    public static CreativeModeTab create() {
        UltreonDevicesCommon.LOGGER.info("Creating Creative Tab...");

        return creativeModeTabBuilder.build();
    }
}
