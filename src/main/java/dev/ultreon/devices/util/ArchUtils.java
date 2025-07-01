package dev.ultreon.devices.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.platform.Platform;

public class ArchUtils {
    @Deprecated
    public static boolean isProduction() {
        return Platform.isDevelopmentEnvironment();
    }
}
