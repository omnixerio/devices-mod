package com.ultreon.devices.fabric;

import com.ultreon.devices.DeviceConfig;
import com.ultreon.devices.Devices;
import com.ultreon.devices.api.app.Application;
import com.ultreon.devices.api.print.IPrint;
import com.ultreon.devices.api.print.PrintingManager;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.init.RegistrationHandler;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraftforge.fml.config.ModConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DevicesFabric extends Devices implements ModInitializer {
    @Override
    public void onInitialize() {
        ForgeConfigRegistry.INSTANCE.register(Devices.MOD_ID, ModConfig.Type.CLIENT, DeviceConfig.CONFIG);

        this.init();

        RegistrationHandler.register();
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
        (eve = new ArrayList<>(eve)).remove(builtin);
        for (EntrypointContainer<FabricApplicationRegistration> fabricApplicationRegistrationEntrypointContainer : eve) {
            fabricApplicationRegistrationEntrypointContainer.getEntrypoint().registerApplications();
        }
    }

    @Override
    protected List<Application> getApplications() {
        return Laptop.getApplicationsForFabric();
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