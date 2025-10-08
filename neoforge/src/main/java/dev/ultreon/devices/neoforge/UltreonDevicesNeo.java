package dev.ultreon.devices.neoforge;

import com.mojang.logging.LogUtils;
import dev.ultreon.devices.*;
import dev.ultreon.devices.api.app.Application;
import dev.ultreon.devices.api.print.IPrint;
import dev.ultreon.devices.api.print.PrintingManager;
import dev.ultreon.devices.core.ComputerScreen;
import dev.ultreon.devices.init.RegistrationHandler;
import dev.ultreon.mods.xinexlib.Env;
import dev.ultreon.mods.xinexlib.platform.NeoForgePlatform;
import dev.ultreon.mods.xinexlib.platform.XinexPlatform;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.i18n.MavenVersionTranslator;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Reference.MOD_ID)
public final class UltreonDevicesNeo {
    public static final Logger LOGGER = LogUtils.getLogger();
    private final UltreonDevices instance = new UltreonDevices() {
        @Override
        protected List<Application> loadApps() {
            return ObfuscationReflectionHelper.getPrivateValue(ComputerScreen.class, null, "APPLICATIONS");
        }

        @Override
        public String getVersion() {
            return MavenVersionTranslator.artifactVersionToString(ModList.get().getModContainerById("devices").orElseThrow().getModInfo().getVersion());
        }

        @Override
        public int getBurnTime(ItemStack stack, RecipeType<?> type) {
            return stack.getBurnTime(type);
        }

        @OnlyIn(Dist.CLIENT)
        protected void setRegisteredRenders(Map<String, dev.ultreon.devices.api.print.IPrint.Renderer> map) {
            ObfuscationReflectionHelper.setPrivateValue(PrintingManager.class, null, map, "registeredRenders");
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        protected Map<String, IPrint.Renderer> getRegisteredRenders() {
            return ObfuscationReflectionHelper.getPrivateValue(PrintingManager.class, null, "registeredRenders");
        }
    };

    public IEventBus modEventBus;

    public UltreonDevicesNeo(IEventBus modEventBus, ModContainer container) {
        super();

        NeoForgePlatform.getPlatform().registerMod(container.getModId(), modEventBus);

        this.modEventBus = modEventBus;

        UltreonDevices.preInit();

        // Common side stuff
        LOGGER.info("Initializing registration handler and mod config.");
        RegistrationHandler.register();
        container.registerConfig(ModConfig.Type.CLIENT, DeviceConfig.CONFIG);

        LOGGER.info("Registering common setup handler, and load complete handler.");
        if (XinexPlatform.getEnv() == Env.CLIENT) {
            UltreonDevices.doClientInit();
        }
        modEventBus.addListener(this::onLoadComplete);

        // Server side stuff
        LOGGER.info("Registering server setup handler.");
        modEventBus.addListener(this::onServerSetup);

        instance.registerApplications();

        // Client side stuff
        if (!DatagenModLoader.isRunningDataGen()) {
            LOGGER.info("Registering the reload listener.");
            ((ReloadableResourceManager ) Minecraft.getInstance().getResourceManager()).registerReloadListener(new ClientModEvents.ReloaderListener());
        }

        instance.init();
    }

    private void onServerSetup(FMLDedicatedServerSetupEvent t) {
        t.enqueueWork(instance::serverSetup);
    }

    private void onLoadComplete(FMLLoadCompleteEvent t) {
        t.enqueueWork(instance::loadComplete);
    }
}
