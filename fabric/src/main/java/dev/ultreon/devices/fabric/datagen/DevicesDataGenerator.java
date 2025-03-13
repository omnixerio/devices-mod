package dev.ultreon.devices.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

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
        pack.addProvider((FabricDataOutput output) -> new DevicesRecipeProvider(output, fabricDataGenerator.getRegistries()));
        pack.addProvider((FabricDataOutput output) -> new DevicesLootTableGenerator(output, fabricDataGenerator.getRegistries()));
        pack.addProvider(DevicesBlockTagProvider::new);
        pack.addProvider(DevicesItemTagProvider::new);
    }
}
