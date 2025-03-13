package dev.ultreon.devices.fabric;

import dev.ultreon.devices.DeviceConfig;
import dev.ultreon.devices.Devices;
import dev.ultreon.devices.api.app.Application;
import dev.ultreon.devices.api.print.IPrint;
import dev.ultreon.devices.api.print.PrintingManager;
import dev.ultreon.devices.core.ComputerScreen;
import dev.ultreon.devices.init.RegistrationHandler;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.neoforged.fml.config.ModConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DevicesFabricMod extends Devices implements ModInitializer {
    @Override
    public void onInitialize() {
        NeoForgeConfigRegistry.INSTANCE.register(Devices.MOD_ID, ModConfig.Type.CLIENT, DeviceConfig.CONFIG);

        RegistrationHandler.register();

        this.init();
    }

    @Override
    public int getBurnTime(ItemStack stack, RecipeType<?> type) {
        var a = AbstractFurnaceBlockEntity.getFuel().get(stack.getItem());
        return a == null ? 1600 : a;
    }

    @Override
    protected void registerApplicationEvent() {
        var eve = FabricLoader.getInstance().getEntrypointContainers("devices:application_registration", FabricApplicationRegistration.class);
        EntrypointContainer<FabricApplicationRegistration> builtin = null;
        for (EntrypointContainer<FabricApplicationRegistration> fabricApplicationRegistrationEntrypointContainer : eve) {
            if (fabricApplicationRegistrationEntrypointContainer.getProvider().getMetadata().getId().equals("devices")) {
                builtin = fabricApplicationRegistrationEntrypointContainer;
            }
        }
        assert builtin != null;
        builtin.getEntrypoint().registerApplications();
        eve = new ArrayList<>(eve);
        eve.remove(builtin);
        for (EntrypointContainer<FabricApplicationRegistration> fabricApplicationRegistrationEntrypointContainer : eve) {
            fabricApplicationRegistrationEntrypointContainer.getEntrypoint().registerApplications();
        }
    }

    @Override
    protected List<Application> loadApps() {
        return ComputerScreen.getApplicationsForFabric();
    }

    @Override
    public String getVersion() {
        return FabricLoader.getInstance().getModContainer("devices").orElseThrow().getMetadata().getVersion().getFriendlyString();
    }

    @Override
    protected Map<String, IPrint.Renderer> getRegisteredRenders() {
        return PrintingManager.getRegisteredRenders();
    }

    @Override
    protected void setRegisteredRenders(Map<String, IPrint.Renderer> map) {
        PrintingManager.setRegisteredRenders(map);
    }
}