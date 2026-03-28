package com.ultreon.devices.init;

import com.ultreon.devices.DeviceTab;
import com.ultreon.devices.Devices;
import dev.ultreon.mods.xinexlib.registrar.Registrar;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;

public class DeviceCreativeTabs {
    private static final Registrar<CreativeModeTab> REGISTER = Devices.REGISTRIES.get().getRegistrar(Registries.CREATIVE_MODE_TAB);

    public static void register() {
    }

    static {
        REGISTER.register("devices", DeviceTab::create);
    }
}
