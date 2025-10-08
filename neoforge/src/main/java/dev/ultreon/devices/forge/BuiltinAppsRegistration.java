package dev.ultreon.devices.forge;

import dev.ultreon.devices.BuiltinApps;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class BuiltinAppsRegistration {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void registerBuiltinApps(ForgeApplicationRegistration event) {
        BuiltinApps.registerBuiltinApps();
    }
}
