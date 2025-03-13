package dev.ultreon.devices.util;

import dev.ultreon.mods.xinexlib.platform.Services;

public class ArchUtils {
    @Deprecated
    public static boolean isProduction() {
        return Services.isDevelopmentEnvironment();
    }
}
