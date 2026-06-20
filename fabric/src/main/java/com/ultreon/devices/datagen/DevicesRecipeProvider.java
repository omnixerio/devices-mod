package com.ultreon.devices.datagen;

import com.ultreon.devices.OmnixerioDevicesMod;
import com.ultreon.devices.block.OfficeChairBlock;
import com.ultreon.devices.block.PrinterBlock;
import com.ultreon.devices.block.RouterBlock;
import com.ultreon.devices.init.ModBlocks;
import com.ultreon.devices.init.ModItems;
import com.ultreon.devices.init.tags.ModItemTags;
import com.ultreon.devices.item.FlashDriveItem;
import com.ultreon.devices.util.ItemColors;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;

public class DevicesRecipeProvider extends FabricRecipeProvider {
    public DevicesRecipeProvider(FabricDataOutput dataGenerator, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(dataGenerator, registriesFuture);
    }

    @Override
    public void buildRecipes(RecipeOutput exporter) {
        ModBlocks.LAPTOPS.getMap().forEach(((dyeColor, blockRegistrySupplier) -> laptop(exporter, blockRegistrySupplier.get(), dyeColor)));

        new ShapedRecipeBuilder(RecipeCategory.TOOLS, ModItems.MAC_MAX_X.get(), 1)
                .pattern("iii")
                .pattern("ssd")
                .pattern("bcb")
                .define('i', Items.IRON_INGOT)
                .define('s', ModItems.COMPONENT_SCREEN.get())
                .define('d', ModItems.COMPONENT_SOLID_STATE_DRIVE.get())
                .define('c', ModItems.COMPONENT_MOTHERBOARD_FULL.get())
                .define('b', Items.IRON_BLOCK)
                .unlockedBy("has_motherboard", has(ModItems.COMPONENT_MOTHERBOARD_FULL.get()))
                .save(exporter);

        //***********************//
        //      Flash Drives     //
        //***********************//
        for (FlashDriveItem flashDrive : ModItems.getAllFlashDrives()) {
            DyeColor color = flashDrive.getColor();
            new ShapedRecipeBuilder(RecipeCategory.TOOLS, flashDrive, 1)
                    .pattern("did")
                    .pattern("pfp")
                    .pattern("pcp")
                    .define('d', DyeItem.byColor(color))
                    .define('i', Items.IRON_INGOT)
                    .define('p', ModItems.PLASTIC_FRAME.get())
                    .define('f', ModItems.COMPONENT_FLASH_CHIP.get())
                    .define('c', ModItems.COMPONENT_CIRCUIT_BOARD.get())
                    .unlockedBy("has_flash_chip", has(ModItems.COMPONENT_FLASH_CHIP.get()))
                    .group(OmnixerioDevicesMod.MOD_ID + ":laptop")
                    .save(exporter);
        }

        //******************//
        //     Printers     //
        //******************//
        for (PrinterBlock printer : ModBlocks.getAllPrinters()) {
            new ShapedRecipeBuilder(RecipeCategory.TOOLS, printer, 1)
                    .pattern("psp")
                    .pattern("mcb")
                    .pattern("pdp")
                    .define('d', DyeItem.byColor(printer.getColor()))
                    .define('p', ModItems.PLASTIC_FRAME.get())
                    .define('s', ModItems.COMPONENT_SCREEN.get())
                    .define('m', ModItems.COMPONENT_SMALL_ELECTRIC_MOTOR.get())
                    .define('c', ModItems.COMPONENT_CARRIAGE.get())
                    .define('b', ModItems.COMPONENT_CONTROLLER_UNIT.get())
                    .unlockedBy("has_carriage", has(ModItems.COMPONENT_CARRIAGE.get()))
                    .group(OmnixerioDevicesMod.MOD_ID + ":printer")
                    .save(exporter);
        }

        //*****************//
        //     Routers     //
        //*****************//
        for (RouterBlock router : ModBlocks.getAllRouters()) {
            new ShapedRecipeBuilder(RecipeCategory.TOOLS, router, 1)
                    .pattern("rdr")
                    .pattern("ppp")
                    .pattern("wcb")
                    .define('d', DyeItem.byColor(router.getColor()))
                    .define('r', Items.END_ROD)
                    .define('p', ModItems.PLASTIC_FRAME.get())
                    .define('w', ModItems.COMPONENT_WIFI.get())
                    .define('c', ModItems.COMPONENT_CIRCUIT_BOARD.get())
                    .define('b', ModItems.COMPONENT_BATTERY.get())
                    .unlockedBy("has_circuit_board", has(ModItems.COMPONENT_CIRCUIT_BOARD.get()))
                    .group(OmnixerioDevicesMod.MOD_ID + ":router")
                    .save(exporter);
        }

        //*****************//
        //     Routers     //
        //*****************//
        for (OfficeChairBlock router : ModBlocks.getAllOfficeChairs()) {
            new ShapedRecipeBuilder(RecipeCategory.TOOLS, router, 1)
                    .pattern("cdc")
                    .pattern("clc")
                    .pattern("wfw")
                    .define('d', ItemColors.woolByColor(router.getColor()))
                    .define('c', Items.GRAY_CONCRETE)
                    .define('l', Items.LEATHER)
                    .define('w', ModItems.WHEEL.get())
                    .define('f', Items.COBBLESTONE_WALL)
                    .unlockedBy("has_wheel", has(ModItems.WHEEL.get()))
                    .group(OmnixerioDevicesMod.MOD_ID + ":office_chair")
                    .save(exporter);
        }

        //*************************//
        //     Component Items     //
        //*************************//
        new ShapedRecipeBuilder(RecipeCategory.MISC, ModItems.COMPONENT_CIRCUIT_BOARD.get(), 1)
                .pattern("iri")
                .pattern("rcr")
                .pattern("ppp")
                .define('p', ModItems.PLASTIC.get())
                .define('i', Items.IRON_NUGGET)
                .define('r', Items.REDSTONE)
                .define('c', Items.COPPER_INGOT)
                .unlockedBy("has_laptop", has(ModItemTags.LAPTOPS))
                .save(exporter);
        new ShapedRecipeBuilder(RecipeCategory.MISC, ModItems.COMPONENT_FLASH_CHIP.get(), 1)
                .pattern("iri")
                .pattern("ppp")
                .pattern("ggg")
                .define('p', ModItems.PLASTIC_FRAME.get())
                .define('i', Items.IRON_INGOT)
                .define('r', Items.REDSTONE)
                .define('g', Items.GOLD_NUGGET)
                .unlockedBy("has_laptop", has(ModItemTags.LAPTOPS))
                .save(exporter);
        new ShapelessRecipeBuilder(RecipeCategory.MISC, ModItems.COMPONENT_MOTHERBOARD_FULL.get(), 1)
                .requires(ModItems.COMPONENT_CPU.get(), 1)
                .requires(ModItems.COMPONENT_GPU.get(), 1)
                .requires(ModItems.COMPONENT_RAM.get(), 1)
                .requires(ModItems.COMPONENT_WIFI.get(), 1)
                .requires(ModItems.COMPONENT_MOTHERBOARD.get(), 1)
                .unlockedBy("has_motherboard", has(ModItems.COMPONENT_MOTHERBOARD.get()))
                .save(exporter);
        new ShapedRecipeBuilder(RecipeCategory.MISC, ModItems.WHEEL.get(), 1)
                .pattern("p p")
                .pattern("pip")
                .pattern("p p")
                .define('p', ModItems.PLASTIC_FRAME.get())
                .define('i', Items.IRON_BLOCK)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(exporter);
        new ShapedRecipeBuilder(RecipeCategory.MISC, ModItems.COMPONENT_SOLID_STATE_DRIVE.get(), 1)
                .pattern("oro")
                .pattern("ofo")
                .pattern("ofo")
                .define('o', Items.OBSIDIAN)
                .define('f', ModItems.COMPONENT_FLASH_CHIP.get())
                .define('r', Items.REDSTONE)
                .unlockedBy("has_flash_chip", has(ModItems.COMPONENT_FLASH_CHIP.get()))
                .save(exporter);
        new ShapedRecipeBuilder(RecipeCategory.MISC, ModItems.COMPONENT_WIFI.get(), 1)
                .pattern(" e ")
                .pattern("idi")
                .pattern("rcr")
                .define('e', Items.END_ROD)
                .define('i', Items.IRON_INGOT)
                .define('d', Items.ENDER_PEARL)
                .define('r', Items.REDSTONE)
                .define('c', ModItems.COMPONENT_CIRCUIT_BOARD.get())
                .unlockedBy("has_circuit_board", has(ModItems.COMPONENT_CIRCUIT_BOARD.get()))
                .save(exporter);
        new ShapedRecipeBuilder(RecipeCategory.MISC, ModItems.COMPONENT_SCREEN.get(), 1)
                .pattern("rgb")
                .pattern("ppp")
                .pattern("cdc")
                .define('r', Items.RED_STAINED_GLASS_PANE)
                .define('g', Items.GREEN_STAINED_GLASS_PANE)
                .define('b', Items.BLUE_STAINED_GLASS_PANE)
                .define('p', Items.PRISMARINE_CRYSTALS)
                .define('c', ModItems.COMPONENT_CIRCUIT_BOARD.get())
                .define('d', Items.REDSTONE)
                .unlockedBy("has_circuit_board", has(ModItems.COMPONENT_CIRCUIT_BOARD.get()))
                .save(exporter);
        new ShapedRecipeBuilder(RecipeCategory.MISC, ModItems.COMPONENT_RAM.get(), 1)
                .pattern("bbb")
                .pattern("rcr")
                .pattern("ggg")
                .define('b', ModItems.COMPONENT_CIRCUIT_BOARD.get())
                .define('r', Items.REDSTONE)
                .define('c', Items.ENDER_EYE)
                .define('g', Items.GOLD_NUGGET)
                .unlockedBy("has_circuit_board", has(ModItems.COMPONENT_CIRCUIT_BOARD.get()))
                .save(exporter);
        new ShapedRecipeBuilder(RecipeCategory.MISC, ModItems.PLASTIC_FRAME.get(), 1)
                .pattern("pp")
                .pattern("pp")
                .define('p', ModItems.PLASTIC.get())
                .unlockedBy("has_plastic", has(ModItems.PLASTIC.get()))
                .save(exporter);
        new ShapedRecipeBuilder(RecipeCategory.MISC, ModItems.COMPONENT_HARD_DRIVE.get(), 1)
                .pattern("nri")
                .pattern("ibi")
                .pattern("ibi")
                .define('n', Items.IRON_NUGGET)
                .define('i', Items.IRON_INGOT)
                .define('r', Items.REDSTONE)
                .define('b', Items.SHULKER_SHELL)
                .unlockedBy("has_plastic", has(ModItems.PLASTIC.get()))
                .save(exporter);
        new ShapedRecipeBuilder(RecipeCategory.MISC, ModItems.COMPONENT_GPU.get(), 1)
                .pattern("ogo")
                .pattern("rmp")
                .pattern("ccc")
                .define('o', Items.OBSIDIAN)
                .define('g', Items.GOLD_INGOT)
                .define('r', Items.REDSTONE)
                .define('m', ModItems.COMPONENT_RAM.get())
                .define('p', Items.PRISMARINE_SHARD)
                .define('c', ModItems.COMPONENT_CIRCUIT_BOARD.get())
                .unlockedBy("has_plastic", has(ModItems.PLASTIC.get()))
                .save(exporter);
        new ShapedRecipeBuilder(RecipeCategory.MISC, ModItems.ETHERNET_CABLE.get(), 1)
                .pattern("pil")
                .pattern("gig")
                .pattern("lip")
                .define('p', Items.PRISMARINE_CRYSTALS)
                .define('i', Items.IRON_INGOT)
                .define('l', ModItems.PLASTIC.get())
                .define('g', Items.GOLD_NUGGET)
                .unlockedBy("has_plastic", has(ModItems.PLASTIC.get()))
                .save(exporter);
        new ShapelessRecipeBuilder(RecipeCategory.MISC, ModItems.PLASTIC_UNREFINED.get(), 1)
                .requires(Items.BLACK_DYE)
                .requires(Items.WHITE_DYE)
                .requires(Items.SLIME_BALL)
                .unlockedBy("has_slime_ball", has(Items.SLIME_BALL))
                .save(exporter);

        //**************************//
        //     Smelting recipes     //
        //**************************//
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ModItems.PLASTIC_UNREFINED.get()), RecipeCategory.MISC, ModItems.PLASTIC.get(), 0.5f, 200)
                .unlockedBy("has_plastic_unrefined", has(ModItems.PLASTIC_UNREFINED.get()))
                .save(exporter);
    }

    public static void laptop(RecipeOutput exporter, ItemLike laptop, DyeColor color) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, laptop)
                .define('+', DyeUtils.getWoolFromDye(color))
                .define('/', Items.IRON_INGOT)
                .define('#', ModItems.COMPONENT_SCREEN.get())
                .define('.', Items.IRON_NUGGET)
                .define('$', ModItems.COMPONENT_BATTERY.get())
                .define('@', ModItems.COMPONENT_MOTHERBOARD_FULL.get())
                .define('O', ModItemTags.INTERNAL_STORAGE)
                .pattern("+#+")
                .pattern(".@$")
                .pattern("/O/").group("devices:laptop")
                .unlockedBy(getHasName(Items.NETHERITE_INGOT), has(Items.NETHERITE_INGOT))
                .unlockedBy(getHasName(ModItems.COMPONENT_MOTHERBOARD_FULL.get()), has(ModItems.COMPONENT_MOTHERBOARD_FULL.get()))
                .save(exporter);
    }
}
