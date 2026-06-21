package dev.ultreon.devices.datagen;

import dev.ultreon.devices.OmnixerioDevicesMod;
import dev.ultreon.devices.init.ModBlocks;
import dev.ultreon.devices.init.tags.ModBlockTags;
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
        Registrar<Block> blocks = OmnixerioDevicesMod.REGISTRIES.get().get(Registries.BLOCK);
        TagAppender<Block> laptops = this.tag(ModBlockTags.LAPTOPS);
        TagAppender<Block> printers = this.tag(ModBlockTags.PRINTERS);
        TagAppender<Block> routers = this.tag(ModBlockTags.ROUTERS);

        ModBlocks.getAllLaptops().forEach(o -> laptops.addOptional(Objects.requireNonNull(blocks.getId(o))));
        ModBlocks.getAllPrinters().forEach(o -> printers.addOptional(Objects.requireNonNull(blocks.getId(o))));
        ModBlocks.getAllRouters().forEach(o -> routers.addOptional(Objects.requireNonNull(blocks.getId(o))));
    }
}
