package dev.ultreon.devices.fabric;

import dev.ultreon.devices.DeviceConfig;
import dev.ultreon.devices.OmnixerioDevicesCommon;
import dev.ultreon.devices.client.OmnixerioDevicesClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;

public class FabricClientDevicesMod extends OmnixerioDevicesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OmnixerioDevicesCommon.getInstance().onInitialize();

        ClientPlayConnectionEvents.JOIN.register((_, _, _) -> OmnixerioDevicesCommon.onClientJoin());

        super.onInitializeClient();
    }
}
