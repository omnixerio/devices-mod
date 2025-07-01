package dev.ultreon.devices;

import dev.architectury.injectables.targets.ArchitecturyTarget;
import dev.architectury.platform.Platform;

public class LaunchException extends RuntimeException {
    @Override
    public String getMessage() {
        return "The developer version of the Device Mod has been detected and can only be run in a " + getPlatform() + " development " +
                "environment. If you are not a developer, download the normal version (https://www.curseforge.com/minecraft/mc-mods/devices-mod)";
    }

    private static String getPlatform() {
        var target = ArchitecturyTarget.getCurrentTarget();
        if (target.equals("forge")) return "Forge";
        if (target.equals("fabric")) return "Fabric";
        return "modded";
    }
}
