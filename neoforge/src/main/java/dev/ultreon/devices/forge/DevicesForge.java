package dev.ultreon.devices.forge;

import com.mojang.logging.LogUtils;
import dev.ultreon.devices.*;
import dev.ultreon.devices.api.print.IPrint;
import dev.ultreon.devices.api.print.PrintingManager;
import dev.ultreon.devices.init.RegistrationHandler;
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
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.i18n.MavenVersionTranslator;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import org.slf4j.Logger;

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

    public DevicesForge(IEventBus modEventBus, ModContainer container) throws LaunchException {
        super();

        this.modEventBus = modEventBus;

        Devices.preInit();
        IEventBus forgeEventBus = NeoForge.EVENT_BUS;

        // Common side stuff
        LOGGER.info("Initializing registration handler and mod config.");
        RegistrationHandler.register();
//        NeoForgeConfigRegistry.INSTANCE.register(container, ModConfig.Type.CLIENT, DeviceConfig.CONFIG);

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
            ((ReloadableResourceManager ) Minecraft.getInstance().getResourceManager()).registerReloadListener(new ClientModEvents.ReloaderListener());
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
