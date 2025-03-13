package dev.ultreon.devices.fabric;

import dev.ultreon.devices.BuiltinApps;

public class BuiltinAppsRegistration implements FabricApplicationRegistration {
    @Override
    public void registerApplications() {
        BuiltinApps.registerBuiltinApps();
    }
}
