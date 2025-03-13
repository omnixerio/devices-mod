package dev.ultreon.devices;

import dev.ultreon.mods.xinexlib.ModPlatform;
import dev.ultreon.mods.xinexlib.platform.Services;

public class LaunchException extends RuntimeException {
    @Override
    public String getMessage() {
        return "The developer version of the Device Mod has been detected and can only be run in a " + getPlatform() + " development " +
                "environment. If you are not a developer, download the normal version (https://www.curseforge.com/minecraft/mc-mods/devices-mod)";
    }

    private static String getPlatform() {
        var target = Services.getPlatformName();
        if (target.equals(ModPlatform.Forge)) return "Minecraft Forge";
        if (target.equals(ModPlatform.Fabric)) return "FabricMC";
        if (target.equals(ModPlatform.NeoForge)) return "NeoForge";
        return "<insert-modloader>";
    }
}
