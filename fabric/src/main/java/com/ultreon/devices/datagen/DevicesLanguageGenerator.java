package com.ultreon.devices.datagen;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ultreon.devices.init.DeviceItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.DyeColor;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DevicesLanguageGenerator extends FabricLanguageProvider {
    private final String languageCode;
    public DevicesLanguageGenerator(FabricDataOutput dataGenerator) {
        super(dataGenerator);
        this.languageCode = "en_us";
    }

    public DevicesLanguageGenerator(FabricDataOutput dataGenerator, String languageCode) {
        super(dataGenerator, languageCode);
        this.languageCode = languageCode;
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        if (this.languageCode.equals("en_pt")) {
            createTranslationsForPirateSpeak(translationBuilder);
        } else if (this.languageCode.startsWith("en_")) { // engurishu
            createTranslationsForEnglish(translationBuilder);
        } else if (this.languageCode.startsWith("nl_")) { // dutch
            createTranslationsForDutch(translationBuilder);
        } else if (this.languageCode.equals("lol_us")) { // lolcat
            createTranslationsForLOLCAT(translationBuilder);
        } else if (this.languageCode.startsWith("ja_")) {
            createTranslationsForJapanese(translationBuilder);
        }
    }

    private void createTranslationsForPirateSpeak(TranslationBuilder translationBuilder) {
        createTranslationsFromTemplate(translationBuilder, "en_pt");
    }

    private void createTranslationsForJapanese(TranslationBuilder translationBuilder) {
        createTranslationsFromTemplate(translationBuilder, "ja");
    }

    private JsonObject getJSON(Path path) {
        try {
            FileInputStream d = new FileInputStream(path.toFile());
            var json = new Gson().fromJson(IOUtils.toString(d, StandardCharsets.UTF_8), JsonObject.class);
            if (!path.endsWith("en.json")) {
                var eng = getJSON(dataOutput.getModContainer().findPath("translations/en.json").orElseThrow());
                for (String s : eng.keySet()) {
                    if (!json.has(s)) {
                        eng.add(s, eng.get(s));
                    }
                }
            }
            return json;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void createTranslationsFromTemplate(TranslationBuilder translationBuilder, String file) {
        @NotNull
        var pathode = getJSON(dataOutput.getModContainer().findPath("translations/" + file + ".json").orElseThrow());

        DeviceItems.LAPTOPS.getMap().forEach((dye, item) -> {
            var laptop = pathode.get("laptop").getAsString();
            var laptop_block = String.format(pathode.get("laptop_block").getAsString(), laptop,get(dye));
            translationBuilder.add(item.get().getDescriptionId(), laptop_block);
        });

        DeviceItems.PRINTERS.getMap().forEach((dye, item) -> {
            var printer = pathode.get("printer").getAsString();
            var printer_block = String.format(pathode.get("printer_block").getAsString(), printer,get(dye));
            translationBuilder.add(item.get().getDescriptionId(), printer_block);
        });

        DeviceItems.FLASH_DRIVE.getMap().forEach((dye, item) -> {
            var flash_drive = pathode.get("flash_drive").getAsString();
            var flash_drive_item = String.format(pathode.get("flash_drive_item").getAsString(), flash_drive,get(dye));
            translationBuilder.add(item.get().getDescriptionId(), flash_drive_item);
        });

        DeviceItems.ROUTERS.getMap().forEach((dye, item) -> {
            var router = pathode.get("router").getAsString();
            var router_block = String.format(pathode.get("router_block").getAsString(), router, get(dye));
            translationBuilder.add(item.get().getDescriptionId(), router_block);
        });

        DeviceItems.OFFICE_CHAIRS.getMap().forEach((dye, item) -> {
            var office_chair = pathode.get("office_chair").getAsString();
            var office_chair_block = String.format(pathode.get("office_chair_block").getAsString(), office_chair, get(dye));
            translationBuilder.add(item.get().getDescriptionId(), office_chair_block);
        });
    }

    private void createTranslationsForLOLCAT(TranslationBuilder translationBuilder) {
        createTranslationsFromTemplate(translationBuilder, "lol");
    }

    private void createTranslationsForDutch(TranslationBuilder translationBuilder) { // TODO: @Qboi123
        createTranslationsFromTemplate(translationBuilder, "nl");
    }

    private void createTranslationsForEnglish(TranslationBuilder translationBuilder) {
        try {
            translationBuilder.add(dataOutput.getModContainer().findPath("en_us_existing.json").orElseThrow());
        } catch (Exception e) {e.printStackTrace();}
        createTranslationsFromTemplate(translationBuilder, "en");
    }

    private String get(DyeColor dye) {
        if(differentLanguageCode()) {
            return grabIt("color.minecraft." + dye.getName());
        }
        return I18n.get("color.minecraft." + dye.getName());
    }

    private String grabIt(String key) {
        try {
            var d = Paths.get("lang/" + this.languageCode + ".json").toFile();
            var q = new BufferedReader(new FileReader(d));
            var gg = new Gson().fromJson(q, JsonObject.class);
            return gg.get(key).getAsString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean differentLanguageCode() {
        return !this.languageCode.startsWith("en_") && !this.languageCode.equals("en_pt");
    }
}
