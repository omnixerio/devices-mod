package com.ultreon.devices.datagen;

import com.ultreon.devices.Devices;
import com.ultreon.devices.init.DeviceBlocks;
import dev.architectury.registry.registries.Registrar;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

import java.util.function.BiConsumer;

public class DevicesLootTableGenerator extends FabricBlockLootTableProvider {
    public DevicesLootTableGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate() {
        for (var block : DeviceBlocks.getAllBlocks().toList()) {
            dropSelf(block);
        }
    }

    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> resourceLocationBuilderBiConsumer) {
        Registrar<Block> blocks = Devices.REGISTRIES.get().get(Registries.BLOCK);
        for (var block : DeviceBlocks.getAllBlocks().toList()) {
            ResourceLocation id = blocks.getId(block);
            if (id.getNamespace().equals(Devices.MOD_ID)) {
                resourceLocationBuilderBiConsumer.accept(new ResourceLocation(id.getNamespace(), "blocks/" + id.getPath()),
                        createSingleItemTable(block.asItem()));
            }
        }
    }
}
