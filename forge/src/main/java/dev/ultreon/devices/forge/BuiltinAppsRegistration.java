<<<<<<<< HEAD:forge/src/main/java/com/ultreon/devices/forge/BuiltinAppsRegistration.java
package com.ultreon.devices.forge;
========
package dev.ultreon.devices.forge;
>>>>>>>> origin/wip/port-xinexlib:forge/src/main/java/dev/ultreon/devices/forge/BuiltinAppsRegistration.java

import dev.ultreon.devices.BuiltinApps;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BuiltinAppsRegistration {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void registerBuiltinApps(ForgeApplicationRegistration event) {
        BuiltinApps.registerBuiltinApps();
    }
}
