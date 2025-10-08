package dev.ultreon.devices;

import java.util.ServiceLoader;

public interface UltreonDevicesServices {
    UltreonDevicesPlatform PLATFORM = ServiceLoader.load(UltreonDevicesPlatform.class).findFirst().orElseThrow();

    static String getVersion() {
        return PLATFORM.getVersion();
    }
}
