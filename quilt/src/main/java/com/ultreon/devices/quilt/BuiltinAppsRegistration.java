package dev.ultreon.devices.quilt;

import dev.ultreon.devices.BuiltinApps;

public class BuiltinAppsRegistration implements QuiltApplicationRegistration {
    @Override
    public void registerApplications() {
        BuiltinApps.registerBuiltinApps();
    }
}
