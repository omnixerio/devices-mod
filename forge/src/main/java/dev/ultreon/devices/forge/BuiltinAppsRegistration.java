package dev.ultreon.devices.forge;

import dev.ultreon.devices.BuiltinApps;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BuiltinAppsRegistration {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void registerBuiltinApps(ForgeApplicationRegistration event) {
        BuiltinApps.registerBuiltinApps();
    }
}
