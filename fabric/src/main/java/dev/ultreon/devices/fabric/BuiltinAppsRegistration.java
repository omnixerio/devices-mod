package dev.ultreon.devices.fabric;

import dev.ultreon.devices.BuiltinApps;

public class BuiltinAppsRegistration implements ApplicationRegistration {
    @Override
    public void registerApplications() {
        BuiltinApps.registerBuiltinApps();
    }
}
