package com.ultreon.devices;

import dev.ultreon.mods.xinexlib.platform.XinexPlatform;

public class Reference {
    public static final String MOD_ID = "devices";
    public static final String VERSION;
    private static String[] verInfo;
    static {
        VERSION = getVersion();
    }

    public static String getVersion() {
        return XinexPlatform.getMod(Devices.MOD_ID).getVersion();
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
