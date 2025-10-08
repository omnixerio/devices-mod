package dev.ultreon.devices.fabric;

import dev.ultreon.devices.UltreonDevicesPlatform;
import net.fabricmc.loader.api.FabricLoader;

public class UltreonDevicesFabricPlatform implements UltreonDevicesPlatform {
    @Override
    public String getVersion() {
        return FabricLoader.getInstance().getModContainer("devices").orElseThrow().getMetadata().getVersion().getFriendlyString();
    }

}
