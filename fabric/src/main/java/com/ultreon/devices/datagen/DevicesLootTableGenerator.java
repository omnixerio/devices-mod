package com.ultreon.devices.datagen;

import com.ultreon.devices.OmnixerioDevicesMod;
import com.ultreon.devices.init.ModBlocks;
import dev.architectury.registry.registries.Registrar;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class DevicesLootTableGenerator extends FabricBlockLootTableProvider {
    public DevicesLootTableGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generate() {
        for (var block : ModBlocks.getAllBlocks().toList()) {
            dropSelf(block);
        }
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer) {
        Registrar<Block> blocks = OmnixerioDevicesMod.REGISTRIES.get().get(Registries.BLOCK);
        for (var block : ModBlocks.getAllBlocks().toList()) {
            ResourceLocation id = blocks.getId(block);
            if (id.getNamespace().equals(OmnixerioDevicesMod.MOD_ID)) {
                biConsumer.accept(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "blocks/" + id.getPath())),
                        createSingleItemTable(block.asItem()));
            }
        }
    }
}
