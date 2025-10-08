package dev.ultreon.devices.neoforge;

import dev.ultreon.devices.DevicesModPlatform;
import net.neoforged.fml.ModList;
import net.neoforged.fml.i18n.MavenVersionTranslator;

public final class UltreonDevicesNeoPlatform implements DevicesModPlatform {
    @Override
    public String getVersion() {
        return MavenVersionTranslator.artifactVersionToString(ModList.get().getModContainerById("devices").orElseThrow().getModInfo().getVersion());
    }
}
