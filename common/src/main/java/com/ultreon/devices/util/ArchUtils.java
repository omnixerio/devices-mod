package com.ultreon.devices.util;

import dev.ultreon.mods.xinexlib.platform.XinexPlatform;

public class ArchUtils {
    @Deprecated
    public static boolean isProduction() {
        return XinexPlatform.isDevelopmentEnvironment();
    }
}
