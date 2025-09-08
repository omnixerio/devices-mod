package dev.ultreon.devices.fabric.datagen;

import dev.ultreon.devices.Devices;
import dev.ultreon.devices.init.DeviceBlocks;
import dev.ultreon.mods.xinexlib.registrar.Registrar;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class DevicesLootTableGenerator extends FabricBlockLootTableProvider {
    public DevicesLootTableGenerator(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        for (var block : DeviceBlocks.getAllBlocks().toList()) {
            dropSelf(block);
        }
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer) {
        Registrar<Block> blocks = Devices.REGISTRIES.get().getRegistrar(Registries.BLOCK);
        for (var block : DeviceBlocks.getAllBlocks().toList()) {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
            if (id.getNamespace().equals(Devices.MOD_ID)) {
                biConsumer.accept(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "blocks/" + id.getPath())), createSingleItemTable(block.asItem()));
            }
        }
    }
}
