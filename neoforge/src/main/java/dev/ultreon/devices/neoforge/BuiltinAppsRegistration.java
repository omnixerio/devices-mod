package dev.ultreon.devices.neoforge;

import dev.ultreon.devices.BuiltinApps;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public final class BuiltinAppsRegistration {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void registerBuiltinApps(ApplicationRegistrationEvent event) {
        BuiltinApps.registerBuiltinApps();
    }
}
