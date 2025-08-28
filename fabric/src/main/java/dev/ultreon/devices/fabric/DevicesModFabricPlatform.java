package dev.ultreon.devices.fabric;

import dev.ultreon.devices.DevicesModPlatform;
import dev.ultreon.devices.object.AppInfo;
import net.fabricmc.loader.api.FabricLoader;

public class DevicesModFabricPlatform implements DevicesModPlatform {
    @Override
    public String getVersion() {
        return FabricLoader.getInstance().getModContainer("devices").orElseThrow().getMetadata().getVersion().getFriendlyString();
    }

    @Override
    public void updateIcon(AppInfo info, int iconU, int iconV) {

    }
}
