package dev.ultreon.devices.fabric.datagen;

import dev.ultreon.devices.UltreonDevices;
import dev.ultreon.devices.init.DeviceItems;
import dev.ultreon.devices.init.ModTags;
import dev.ultreon.mods.xinexlib.registrar.Registrar;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.world.item.Item;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class DevicesItemTagProvider extends FabricTagProvider<Item> {
    public DevicesItemTagProvider(FabricDataOutput dataGenerator) {
        super(dataGenerator, Registries.ITEM, CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor()));
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        Registrar<Item> items = UltreonDevices.REGISTRIES.get().getRegistrar(Registries.ITEM);
        TagAppender<Item> laptops = this.tag(ModTags.Items.LAPTOPS);
        TagAppender<Item> printers = this.tag(ModTags.Items.PRINTERS);
        TagAppender<Item> routers = this.tag(ModTags.Items.ROUTERS);
        TagAppender<Item> flashDrives = this.tag(ModTags.Items.FLASH_DRIVES);

        DeviceItems.getAllLaptops().forEach(o -> laptops.addOptional(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(o))));
        DeviceItems.getAllPrinters().forEach(o -> printers.addOptional(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(o))));
        DeviceItems.getAllRouters().forEach(o -> routers.addOptional(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(o))));
        DeviceItems.getAllFlashDrives().forEach(o -> flashDrives.addOptional(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(o))));
    }
}
