package dev.ultreon.devices.client;

import dev.ultreon.devices.ClientModEvents;
import dev.ultreon.devices.OmnixerioDevicesCommon;
import dev.ultreon.devices.client.network.DevicesClientNetworker;

public class OmnixerioDevicesClient {
    private static final OmnixerioDevicesClient instance = new OmnixerioDevicesClient();

    public static OmnixerioDevicesClient getInstance() {
        return instance;
    }

    protected OmnixerioDevicesClient() {

    }

    public void onInitializeClient() {
        ClientModEvents.registerRenderers();
        DevicesClientNetworker.init();
    }
}
