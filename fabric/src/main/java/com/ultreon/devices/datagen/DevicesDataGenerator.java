package com.ultreon.devices.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class DevicesDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(DevicesModelGenerator::new);
//        pack.addProvider((FabricDataOutput output) -> new DevicesLanguageGenerator(output));
//        pack.addProvider((FabricDataOutput output) -> new DevicesLanguageGenerator(output, "en_au"));
//        pack.addProvider((FabricDataOutput output) -> new DevicesLanguageGenerator(output, "en_pt"));
//        pack.addProvider((FabricDataOutput output) -> new DevicesLanguageGenerator(output, "lol_us"));
//        pack.addProvider((FabricDataOutput output) -> new DevicesLanguageGenerator(output, "ja_jp"));
        CompletableFuture<HolderLookup.Provider> registries = fabricDataGenerator.getRegistries();
        pack.addProvider((FabricDataOutput output) -> new DevicesRecipeProvider(output, registries));
        pack.addProvider((FabricDataOutput output) -> new DevicesAdvancementsProvider(output, registries));
        pack.addProvider((FabricDataOutput output) -> new DevicesLootTableGenerator(output, registries));
        pack.addProvider(DevicesBlockTagProvider::new);
        pack.addProvider(DevicesItemTagProvider::new);
    }
}
