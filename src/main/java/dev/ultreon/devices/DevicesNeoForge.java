package dev.ultreon.devices;

import dev.ultreon.devices.api.app.Application;
import dev.ultreon.devices.api.print.IPrint;
import dev.ultreon.devices.api.print.PrintingManager;
import dev.ultreon.devices.core.Laptop;
import dev.ultreon.mods.xinexlib.platform.NeoForgePlatform;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import java.util.List;
import java.util.Map;

@Mod(value = UltreonDevicesCommon.MOD_ID)
public class DevicesNeoForge extends UltreonDevicesCommon {
    public DevicesNeoForge(IEventBus bus) {
        this.init();
    }

    @Override
    public int getBurnTime(ItemStack stack, RecipeType<?> type, Level level) {
        return level.fuelValues().burnDuration(stack);
    }

    @Override
    protected void registerApplicationEvent() {
        // TODO: Register Applications
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