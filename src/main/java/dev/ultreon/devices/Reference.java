package dev.ultreon.devices;

import net.fabricmc.loader.api.FabricLoader;

public class Reference {
    public static final String MOD_ID = "devices";
    public static final String VERSION;
    private static String[] verInfo;
    static {
        VERSION = getVersion();
    }

    public static String getVersion() {
        return FabricLoader.getInstance().getModContainer(OmnixerioDevicesCommon.MOD_ID).orElseThrow().getMetadata().getVersion().getFriendlyString();
    }

    public static String[] getVerInfo() {
        if (verInfo == null) {
            if (getVersion().split("\\+").length == 1) {
                return verInfo = new String[]{getVersion(), "unknown"};
            }
            var version = getVersion().split("\\+")[0];
            var build = getVersion().split("\\+")[1];
            verInfo = new String[]{version, build};
        }
        return verInfo;
    }
}
