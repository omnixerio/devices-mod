package com.ultreon.devices.forge;

import com.mojang.logging.LogUtils;
import com.ultreon.devices.DeviceConfig;
import com.ultreon.devices.Devices;
import com.ultreon.devices.LaunchException;
import com.ultreon.devices.Reference;
import com.ultreon.devices.api.app.Application;
import com.ultreon.devices.api.print.IPrint;
import com.ultreon.devices.api.print.PrintingManager;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.event.WorldDataHandler;
import com.ultreon.devices.init.RegistrationHandler;
import dev.architectury.platform.forge.EventBuses;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.MavenVersionStringHelper;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.forgespi.language.IModInfo;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Reference.MOD_ID)
public final class DevicesForge {
    public static final Logger LOGGER = LogUtils.getLogger();
    private final Devices instance = new Devices() {
        @Override
        protected void registerApplicationEvent() {
            DevicesForge.this.modEventBus.post(new ForgeApplicationRegistration());
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
        @SuppressWarnings("DataFlowIssue")
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

    public DevicesForge() throws LaunchException {
        super();

        EventBuses.registerModEventBus(Devices.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        this.modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        this.modEventBus.register(BuiltinAppsRegistration.class);

        Devices.preInit();

        ModLoadingContext context = ModLoadingContext.get();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        // Common side stuff
        LOGGER.info("Initializing registration handler and mod config.");
        RegistrationHandler.register();
        context.registerConfig(ModConfig.Type.CLIENT, DeviceConfig.CONFIG);

        forgeEventBus.register(this);

        LOGGER.info("Registering common setup handler, and load complete handler.");
        this.modEventBus.addListener(this::fmlCommonSetup);
        this.modEventBus.addListener(this::fmlLoadComplete);

        // Server side stuff
        LOGGER.info("Registering server setup handler.");
        this.modEventBus.addListener(this::fmlServerSetup);

        // Client side stuff
        if (!DatagenModLoader.isRunningDataGen()) {
            LOGGER.info("Registering the reload listener.");
//            ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(this);
        }

        // Register ourselves for server and other game events we are interested in
        LOGGER.info("Registering mod class to forge events.");
        forgeEventBus.register(this);
    }

    private void fmlCommonSetup(FMLCommonSetupEvent t) {
        this.instance.init();
    }

    private void fmlLoadComplete(FMLLoadCompleteEvent t) {
        this.instance.loadComplete();
    }

    private void fmlServerSetup(FMLDedicatedServerSetupEvent t) {
        this.instance.serverSetup();
    }
}
