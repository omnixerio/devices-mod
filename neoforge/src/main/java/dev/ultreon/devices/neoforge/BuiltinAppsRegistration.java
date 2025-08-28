package dev.ultreon.devices.neoforge;

import dev.ultreon.devices.BuiltinApps;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class BuiltinAppsRegistration {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void registerBuiltinApps(NeoForgeApplicationRegistration event) {
        BuiltinApps.registerBuiltinApps();
    }
}
