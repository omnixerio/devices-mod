package dev.ultreon.devices.init;

import dev.ultreon.devices.Devices;
import dev.ultreon.devices.object.TrayItem;
import dev.ultreon.quantum.registry.RegistryKey;

public class ModRegistryKeys {
    public static RegistryKey<TrayItem> TRAY_ITEM = RegistryKey.registry(Devices.id("tray_item"));
}
