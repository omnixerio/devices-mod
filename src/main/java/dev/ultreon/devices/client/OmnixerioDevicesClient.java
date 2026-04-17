package dev.ultreon.devices.client;

import dev.ultreon.devices.ClientModEvents;
import dev.ultreon.devices.OmnixerioDevicesCommon;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public class OmnixerioDevicesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientModEvents.registerRenderers();
    }
}
