package dev.ultreon.devices.util;

import net.fabricmc.loader.api.FabricLoader;

public class ArchUtils {
    @Deprecated
    public static boolean isProduction() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
