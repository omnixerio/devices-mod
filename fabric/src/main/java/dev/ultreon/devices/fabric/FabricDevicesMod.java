package dev.ultreon.devices.fabric;

import dev.ultreon.devices.DeviceConfig;
import dev.ultreon.devices.OmnixerioDevicesCommon;
import dev.ultreon.devices.client.OmnixerioDevicesClient;
import dev.ultreon.devices.event.WorldDataHandler;
import dev.ultreon.devices.platform.Services;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.neoforged.fml.config.ModConfig;

public class FabricDevicesMod extends OmnixerioDevicesCommon implements ModInitializer {
    @Override
    public void onInitialize() {
        ConfigRegistry.INSTANCE.register(MOD_ID, ModConfig.Type.COMMON, DeviceConfig.CONFIG);

        ServerLifecycleEvents.SERVER_STARTING.register(WorldDataHandler::load);
        ServerLevelEvents.UNLOAD.register(WorldDataHandler::save);

        ServerPlayerEvents.JOIN.register(this::onServerPlayerJoin);
        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStarting);
        ServerLifecycleEvents.SERVER_STOPPED.register(this::onServerStopped);

        super.onInitialize();

        if (Services.PLATFORM.isClient())
            clientLoaders.forEach(Runnable::run);
    }
}
