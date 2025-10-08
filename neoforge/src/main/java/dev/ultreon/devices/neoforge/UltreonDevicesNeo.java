package dev.ultreon.devices.neoforge;

import com.mojang.logging.LogUtils;
import dev.ultreon.devices.*;
import dev.ultreon.devices.api.print.IPrint;
import dev.ultreon.devices.api.print.PrintingManager;
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
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
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
public final class UltreonDevicesNeo extends Devices {
    public static final Logger LOGGER = LogUtils.getLogger();
    private final Devices instance = new Devices() {
        @Override
        protected void registerApplicationEvent() {
            UltreonDevicesNeo.this.modEventBus.post(new ApplicationRegistrationEvent());
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

    public UltreonDevicesNeo(IEventBus modEventBus, ModContainer container) throws LaunchException {
        super();

        NeoForgePlatform.getPlatform().registerMod(container.getModId(), modEventBus);

        this.modEventBus = modEventBus;

        Devices.preInit();
        IEventBus forgeEventBus = NeoForge.EVENT_BUS;

        // Common side stuff
        LOGGER.info("Initializing registration handler and mod config.");
        RegistrationHandler.register();
        container.registerConfig(ModConfig.Type.CLIENT, DeviceConfig.CONFIG);

        LOGGER.info("Registering common setup handler, and load complete handler.");
        if (XinexPlatform.getEnv() == Env.CLIENT)
            modEventBus.addListener(this::onClientSetup);
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::onLoadComplete);

        // Server side stuff
        LOGGER.info("Registering server setup handler.");
        modEventBus.addListener(this::onServerSetup);

        // Client side stuff
        if (!DatagenModLoader.isRunningDataGen()) {
            LOGGER.info("Registering the reload listener.");
            ((ReloadableResourceManager ) Minecraft.getInstance().getResourceManager()).registerReloadListener(new ClientModEvents.ReloaderListener());
        }
    }

    private void onClientSetup(FMLClientSetupEvent t) {
        t.enqueueWork(Devices::doClientInit);
    }

    private void onCommonSetup(FMLCommonSetupEvent t) {
        t.enqueueWork(this.instance::init);
    }

    private void onServerSetup(FMLDedicatedServerSetupEvent t) {
        t.enqueueWork(this.instance::serverSetup);
    }

    private void onLoadComplete(FMLLoadCompleteEvent t) {
        t.enqueueWork(this.instance::loadComplete);
    }

    @Override
    public int getBurnTime(ItemStack stack, RecipeType<?> type) {
        return 0;
    }

    @Override
    protected void registerApplicationEvent() {
        this.modEventBus.post(new ApplicationRegistrationEvent());
    }

    @Override
    public String getVersion() {
        return "";
    }

    @Override
    protected Map<String, IPrint.Renderer> getRegisteredRenders() {
        return Map.of();
    }

    @Override
    protected void setRegisteredRenders(Map<String, IPrint.Renderer> map) {

    }
}
