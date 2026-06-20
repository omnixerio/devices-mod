package com.ultreon.devices.datagen;

import com.ultreon.devices.init.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class DevicesBlockLootTableGenerator extends FabricBlockLootTableProvider {
    public DevicesBlockLootTableGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generate() {
        ModBlocks.getAllLaptops().forEach(this::dropSelf);
        ModBlocks.getAllOfficeChairs().forEach(this::dropSelf);
        ModBlocks.getAllPrinters().forEach(this::dropSelf);
        ModBlocks.getAllRouters().forEach(this::dropSelf);
    }
}
