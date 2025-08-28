package dev.ultreon.devices.fabric.datagen;

import dev.ultreon.devices.Devices;
import dev.ultreon.devices.init.DeviceBlocks;
import dev.ultreon.mods.xinexlib.registrar.Registrar;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
<<<<<<<< HEAD:fabric/src/main/java/com/ultreon/devices/datagen/DevicesLootTableGenerator.java
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
========
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
>>>>>>>> origin/wip/port-xinexlib:fabric/src/main/java/dev/ultreon/devices/fabric/datagen/DevicesLootTableGenerator.java
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
<<<<<<<< HEAD:fabric/src/main/java/com/ultreon/devices/datagen/DevicesLootTableGenerator.java
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> resourceLocationBuilderBiConsumer) {
        Registrar<Block> blocks = Devices.REGISTRIES.get().get(Registries.BLOCK);
        for (var block : DeviceBlocks.getAllBlocks().toList()) {
            ResourceLocation id = blocks.getId(block);
            if (id.getNamespace().equals(Devices.MOD_ID)) {
                resourceLocationBuilderBiConsumer.accept(new ResourceLocation(id.getNamespace(), "blocks/" + id.getPath()),
                        createSingleItemTable(block.asItem()));
========
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer) {
        Registrar<Block> blocks = Devices.REGISTRIES.get().getRegistrar(Registries.BLOCK);
        for (var block : DeviceBlocks.getAllBlocks().toList()) {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
            if (id.getNamespace().equals(Devices.MOD_ID)) {
                biConsumer.accept(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "blocks/" + id.getPath())), createSingleItemTable(block.asItem()));
>>>>>>>> origin/wip/port-xinexlib:fabric/src/main/java/dev/ultreon/devices/fabric/datagen/DevicesLootTableGenerator.java
            }
        }
    }
}
