package com.ultreon.devices.datagen;

import com.ultreon.devices.init.DeviceBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.BiConsumer;

public class DevicesBlockLootTableGenerator extends FabricBlockLootTableProvider {
    public DevicesBlockLootTableGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate() {
        DeviceBlocks.getAllLaptops().forEach(this::dropSelf);
        DeviceBlocks.getAllOfficeChairs().forEach(this::dropSelf);
        DeviceBlocks.getAllPrinters().forEach(this::dropSelf);
        DeviceBlocks.getAllRouters().forEach(this::dropSelf);
    }
}
