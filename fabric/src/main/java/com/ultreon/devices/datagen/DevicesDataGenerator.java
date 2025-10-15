package com.ultreon.devices.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

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
        pack.addProvider(DevicesRecipeProvider::new);
        pack.addProvider(DevicesLootTableGenerator::new);
        pack.addProvider(DevicesBlockEntityTagProvider::new);
        pack.addProvider(DevicesBlockTagProvider::new);
        pack.addProvider(DevicesItemTagProvider::new);
    }
}
