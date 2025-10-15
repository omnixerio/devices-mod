package com.ultreon.devices.datagen;

import com.ultreon.devices.init.DeviceBlockEntities;
import com.ultreon.devices.init.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.concurrent.CompletableFuture;

public class DevicesBlockEntityTagProvider extends FabricTagProvider<BlockEntityType<?>> {
    public DevicesBlockEntityTagProvider(FabricDataOutput dataGenerator) {
        super(dataGenerator, Registries.BLOCK_ENTITY_TYPE, CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor()));
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        TagAppender<BlockEntityType<?>> networkDevices = this.tag(ModTags.BlockEntityTypes.NETWORK_DEVICES);
        TagAppender<BlockEntityType<?>> computers = this.tag(ModTags.BlockEntityTypes.COMPUTERS);
        TagAppender<BlockEntityType<?>> printers = this.tag(ModTags.BlockEntityTypes.PRINTERS);

        computers.addOptional(DeviceBlockEntities.LAPTOP.getId());
        computers.addOptional(DeviceBlockEntities.MAC_MAX_X.getId());
        printers.addOptional(DeviceBlockEntities.PRINTER.getId());
        networkDevices.addTag(ModTags.BlockEntityTypes.COMPUTERS);
        networkDevices.addTag(ModTags.BlockEntityTypes.PRINTERS);
    }
}
