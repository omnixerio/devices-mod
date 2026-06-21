package dev.ultreon.devices.neoforge;

import com.mojang.logging.LogUtils;
import dev.ultreon.devices.DeviceConfig;
import dev.ultreon.devices.OmnixerioDevicesMod;
import dev.ultreon.devices.LaunchException;
import dev.ultreon.devices.Reference;
import dev.ultreon.devices.api.app.Application;
import dev.ultreon.devices.api.print.IPrint;
import dev.ultreon.devices.api.print.PrintingManager;
import dev.ultreon.devices.core.Laptop;
import dev.ultreon.devices.init.ModStats;
import dev.ultreon.devices.init.RegistrationHandler;
import dev.ultreon.devices.neoforge.client.OmnixerioDevicesModNeoForgeClient;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Reference.MOD_ID)
public final class OmnixerioDevicesModNeoForge {
    public static final Logger LOGGER = LogUtils.getLogger();
    private final OmnixerioDevicesMod instance = new OmnixerioDevicesMod() {
        @Override
        protected void registerApplicationEvent() {
            OmnixerioDevicesModNeoForge.this.modEventBus.post(new NeoForgeApplicationRegistration());
        }

        @Override
        public int getBurnTime(ItemStack stack, RecipeType<?> type) {
            return stack.getBurnTime(type);
        }

        @Override
        protected List<Application> getApplications() {
            return ObfuscationReflectionHelper.getPrivateValue(Laptop.class, null, "APPLICATIONS");
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        protected void setRegisteredRenders(Map<String, IPrint.Renderer> map) {
            ObfuscationReflectionHelper.setPrivateValue(PrintingManager.class, null, map, "registeredRenders");
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        protected Map<String, IPrint.Renderer> getRegisteredRenders() {
            return ObfuscationReflectionHelper.getPrivateValue(PrintingManager.class, null, "registeredRenders");
        }
    };

    public IEventBus modEventBus;

    public OmnixerioDevicesModNeoForge(IEventBus modEventBus, ModContainer container) throws LaunchException {
        super();

        this.modEventBus = modEventBus;
        this.modEventBus.register(BuiltinAppsRegistration.class);

        OmnixerioDevicesMod.preInit();

        // Common side stuff
        LOGGER.info("Initializing registration handler and mod config.");
        RegistrationHandler.register();
        container.registerConfig(ModConfig.Type.CLIENT, DeviceConfig.CONFIG);

        LOGGER.info("Registering common setup handler, and load complete handler.");
        this.modEventBus.addListener(this::fmlCommonSetup);
        this.modEventBus.addListener(this::fmlLoadComplete);

        // Server side stuff
        LOGGER.info("Registering server setup handler.");
        this.modEventBus.addListener(this::fmlServerSetup);

        // Client side stuff
        if (FMLEnvironment.dist.isClient()) {
            OmnixerioDevicesModNeoForgeClient.init(modEventBus);
            if (!DatagenModLoader.isRunningDataGen()) {
                LOGGER.info("Registering the reload listener.");
//            ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(this);
            }
        }

        // Register ourselves for server and other game events we are interested in
        LOGGER.info("Registering mod class to forge events.");
    }

    private void fmlCommonSetup(FMLCommonSetupEvent t) {
        this.instance.init();
    }

    private void fmlLoadComplete(FMLLoadCompleteEvent t) {
        this.instance.loadComplete();
        ModStats.init();
    }

    private void fmlServerSetup(FMLDedicatedServerSetupEvent t) {
        this.instance.serverSetup();
    }
}
