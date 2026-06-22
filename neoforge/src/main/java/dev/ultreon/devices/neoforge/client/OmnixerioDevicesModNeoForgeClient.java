package dev.ultreon.devices.neoforge.client;

import dev.ultreon.devices.ClientModEvents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class OmnixerioDevicesModNeoForgeClient {
    public static void init(IEventBus modEventBus) {
        ClientModEvents.clientSetup();
        modEventBus.addListener(OmnixerioDevicesModNeoForgeClient::onClientSetup);
    }

    private static void onClientSetup(FMLClientSetupEvent evt) {
        ClientModEvents.registerRenderers();
        ClientModEvents.registerItemProperties();
    }
}
