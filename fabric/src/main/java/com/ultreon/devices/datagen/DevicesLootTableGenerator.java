package com.ultreon.devices.datagen;

import com.ultreon.devices.Devices;
import com.ultreon.devices.init.DeviceBlocks;
import dev.architectury.registry.registries.Registrar;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
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
    public void generate(BiConsumer<Identifier, LootTable.Builder> IdentifierBuilderBiConsumer) {
        Registrar<Block> blocks = Devices.REGISTRIES.get().get(Registries.BLOCK);
        for (var block : DeviceBlocks.getAllBlocks().toList()) {
            Identifier id = blocks.getId(block);
            if (id.getNamespace().equals(Devices.MOD_ID)) {
                IdentifierBuilderBiConsumer.accept(new Identifier(id.getNamespace(), "blocks/" + id.getPath()),
                        createSingleItemTable(block.asItem()));
            }
        }
    }
}
