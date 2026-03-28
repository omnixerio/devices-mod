package com.ultreon.devices.datagen;

import com.ultreon.devices.Devices;
import com.ultreon.devices.init.DeviceBlocks;
import com.ultreon.devices.init.ModTags;
import dev.architectury.registry.registries.Registrar;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.world.level.block.Block;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class DevicesBlockTagProvider extends FabricTagProvider<Block> {
    public DevicesBlockTagProvider(FabricDataOutput dataGenerator) {
        super(dataGenerator, Registries.BLOCK, CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor()));
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        Registrar<Block> blocks = Devices.REGISTRIES.get().get(Registries.BLOCK);
        TagAppender<Block> laptops = this.tag(ModTags.Blocks.LAPTOPS);
        TagAppender<Block> printers = this.tag(ModTags.Blocks.PRINTERS);
        TagAppender<Block> routers = this.tag(ModTags.Blocks.ROUTERS);

        DeviceBlocks.getAllLaptops().forEach(o -> laptops.addOptional(Objects.requireNonNull(blocks.getId(o))));
        DeviceBlocks.getAllPrinters().forEach(o -> printers.addOptional(Objects.requireNonNull(blocks.getId(o))));
        DeviceBlocks.getAllRouters().forEach(o -> routers.addOptional(Objects.requireNonNull(blocks.getId(o))));
    }
}
