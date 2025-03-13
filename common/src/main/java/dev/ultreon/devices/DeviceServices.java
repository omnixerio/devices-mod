package dev.ultreon.devices;

import java.util.ServiceLoader;

public interface DeviceServices {
    DevicesModPlatform PLATFORM = ServiceLoader.load(DevicesModPlatform.class).findFirst().orElseThrow();

    static String getVersion() {
        return PLATFORM.getVersion();
    }
}
