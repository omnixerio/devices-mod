package com.jab125.testmod;

import com.ultreon.devices.api.ApplicationManager;
import com.ultreon.devices.fabric.BuiltinAppsRegistration;
import com.ultreon.devices.fabric.FabricApplicationRegistration;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.resources.ResourceLocation;

public class ClientInit implements FabricApplicationRegistration, ClientModInitializer {
    @Override
    public void registerApplications() {
        ApplicationManager.registerApplication(new ResourceLocation("devices-testmod", "test-app"), () -> TestApp::new, false);
    }

    @Override
    public void onInitializeClient() {

    }
}
