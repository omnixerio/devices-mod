package dev.ultreon.devices.neoforge;


import dev.ultreon.devices.DeviceConfig;
import dev.ultreon.devices.OmnixerioDevicesCommon;
import dev.ultreon.devices.client.OmnixerioDevicesClient;
import dev.ultreon.devices.event.WorldDataHandler;
import dev.ultreon.devices.neoforge.platform.NeoForgePlatformHelper;
import dev.ultreon.devices.platform.Services;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

import static dev.ultreon.devices.OmnixerioDevicesCommon.LOGGER;

public class NeoForgeClientDevicesMod extends OmnixerioDevicesClient {
    public NeoForgeClientDevicesMod(IEventBus modEventBus, ModContainer modContainer) {
        // This method is invoked by the NeoForge mod loader when it is ready
        // to load your mod. You can access NeoForge and Common code in this
        // project.

        // Use NeoForge to bootstrap the Common mod.
        LOGGER.info("Hello NeoForge world!");

        onInitializeClient();
    }
}