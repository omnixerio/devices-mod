package dev.ultreon.devices.neoforge;


import com.mojang.math.Constants;
import dev.ultreon.devices.DeviceConfig;
import dev.ultreon.devices.OmnixerioDevicesCommon;
import dev.ultreon.devices.event.WorldDataHandler;
import dev.ultreon.devices.neoforge.platform.NeoForgePlatformHelper;
import dev.ultreon.devices.platform.Services;
import dev.ultreon.devices.platform.services.IPlatformHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerLifecycleEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

@Mod(OmnixerioDevicesCommon.MOD_ID)
public class NeoForgeDevicesMod extends OmnixerioDevicesCommon {
    public NeoForgeDevicesMod(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, DeviceConfig.CONFIG);

        NeoForge.EVENT_BUS.addListener(ServerStartingEvent.class, serverStartingEvent -> {
            MinecraftServer server = serverStartingEvent.getServer();
            WorldDataHandler.load(server);
        });

        NeoForge.EVENT_BUS.addListener(LevelEvent.Unload.class, serverStoppingEvent -> {
            LevelAccessor server = serverStoppingEvent.getLevel();
            if (server instanceof ServerLevel serverLevel)
                WorldDataHandler.save(serverLevel.getServer(), serverLevel);
        });

        if (Services.PLATFORM.isClient()) {
            modEventBus.addListener(FMLClientSetupEvent.class, fmlClientSetupEvent -> {
                fmlClientSetupEvent.enqueueWork(() -> clientLoaders.forEach(Runnable::run));
            });
        }

        // This method is invoked by the NeoForge mod loader when it is ready
        // to load your mod. You can access NeoForge and Common code in this
        // project.

        // Use NeoForge to bootstrap the Common mod.
        LOGGER.info("Hello NeoForge world!");

        NeoForgePlatformHelper platform = (NeoForgePlatformHelper) Services.PLATFORM;
        platform.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerLoggedInEvent.class, playerLoggedInEvent -> {
            Player player = playerLoggedInEvent.getEntity();
            if (player instanceof ServerPlayer)
                onServerPlayerJoin((ServerPlayer) player);
        });

        NeoForge.EVENT_BUS.addListener(ServerStartingEvent.class, serverStartingEvent -> {
            onServerStarting(serverStartingEvent.getServer());
        });

        NeoForge.EVENT_BUS.addListener(ServerStoppedEvent.class, serverStoppingEvent -> {
            onServerStopped(serverStoppingEvent.getServer());
        });

        onInitialize();

        if (Services.PLATFORM.isClient()) {
            modEventBus.addListener(FMLClientSetupEvent.class, fmlClientSetupEvent -> {
                fmlClientSetupEvent.enqueueWork(() -> {
                    new NeoForgeClientDevicesMod(modEventBus, modContainer);
                });
            });
        }
    }
}