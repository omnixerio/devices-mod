package com.ultreon.devices.datagen;

import com.ultreon.devices.Devices;
import com.ultreon.devices.block.OfficeChairBlock;
import com.ultreon.devices.block.PrinterBlock;
import com.ultreon.devices.block.RouterBlock;
import com.ultreon.devices.init.DeviceBlocks;
import com.ultreon.devices.init.DeviceItems;
import com.ultreon.devices.init.DeviceTags;
import com.ultreon.devices.init.ModTags;
import com.ultreon.devices.item.FlashDriveItem;
import com.ultreon.devices.util.ItemColors;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

public class DevicesRecipeProvider extends FabricRecipeProvider {
    public DevicesRecipeProvider(FabricDataOutput dataGenerator) {
        super(dataGenerator);
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> exporter) {
        DeviceBlocks.LAPTOPS.getMap().forEach(((dyeColor, blockRegistrySupplier) -> laptop(exporter, blockRegistrySupplier.get(), dyeColor)));

        //***********************//
        //      Flash Drives     //
        //***********************//
        for (FlashDriveItem flashDrive : DeviceItems.getAllFlashDrives()) {
            DyeColor color = flashDrive.getColor();
            new ShapedRecipeBuilder(RecipeCategory.TOOLS, flashDrive, 1)
                    .pattern("did")
                    .pattern("pfp")
                    .pattern("pcp")
                    .define('d', DyeItem.byColor(color))
                    .define('i', Items.IRON_INGOT)
                    .define('p', DeviceItems.PLASTIC_FRAME.get())
                    .define('f', DeviceItems.COMPONENT_FLASH_CHIP.get())
                    .define('c', DeviceItems.COMPONENT_CIRCUIT_BOARD.get())
                    .unlockedBy("has_flash_chip", has(DeviceItems.COMPONENT_FLASH_CHIP.get()))
                    .group(Devices.MOD_ID + ":laptop")
                    .save(exporter);
        }

        //******************//
        //     Printers     //
        //******************//
        for (PrinterBlock printer : DeviceBlocks.getAllPrinters()) {
            new ShapedRecipeBuilder(RecipeCategory.TOOLS, printer, 1)
                    .pattern("psp")
                    .pattern("mcb")
                    .pattern("pdp")
                    .define('d', DyeItem.byColor(printer.getColor()))
                    .define('p', DeviceItems.PLASTIC_FRAME.get())
                    .define('s', DeviceItems.COMPONENT_SCREEN.get())
                    .define('m', DeviceItems.COMPONENT_SMALL_ELECTRIC_MOTOR.get())
                    .define('c', DeviceItems.COMPONENT_CARRIAGE.get())
                    .define('b', DeviceItems.COMPONENT_CONTROLLER_UNIT.get())
                    .unlockedBy("has_carriage", has(DeviceItems.COMPONENT_CARRIAGE.get()))
                    .group(Devices.MOD_ID + ":printer")
                    .save(exporter);
        }

        //*****************//
        //     Routers     //
        //*****************//
        for (RouterBlock router : DeviceBlocks.getAllRouters()) {
            new ShapedRecipeBuilder(RecipeCategory.TOOLS, router, 1)
                    .pattern("rdr")
                    .pattern("ppp")
                    .pattern("wcb")
                    .define('d', DyeItem.byColor(router.getColor()))
                    .define('r', Items.END_ROD)
                    .define('p', DeviceItems.PLASTIC_FRAME.get())
                    .define('w', DeviceItems.COMPONENT_WIFI.get())
                    .define('c', DeviceItems.COMPONENT_CIRCUIT_BOARD.get())
                    .define('b', DeviceItems.COMPONENT_BATTERY.get())
                    .unlockedBy("has_circuit_board", has(DeviceItems.COMPONENT_CIRCUIT_BOARD.get()))
                    .group(Devices.MOD_ID + ":router")
                    .save(exporter);
        }

        //*****************//
        //     Routers     //
        //*****************//
        for (OfficeChairBlock router : DeviceBlocks.getAllOfficeChairs()) {
            new ShapedRecipeBuilder(RecipeCategory.TOOLS, router, 1)
                    .pattern("cdc")
                    .pattern("clc")
                    .pattern("wfw")
                    .define('d', ItemColors.woolByColor(router.getColor()))
                    .define('c', Items.GRAY_CONCRETE)
                    .define('l', Items.LEATHER)
                    .define('w', DeviceItems.WHEEL.get())
                    .define('f', Items.COBBLESTONE_WALL)
                    .unlockedBy("has_wheel", has(DeviceItems.WHEEL.get()))
                    .group(Devices.MOD_ID + ":office_chair")
                    .save(exporter);
        }

        //*************************//
        //     Component Items     //
        //*************************//
        new ShapedRecipeBuilder(RecipeCategory.MISC, DeviceItems.COMPONENT_FLASH_CHIP.get(), 1)
                .pattern("iri")
                .pattern("ppp")
                .pattern("ggg")
                .define('p', DeviceItems.PLASTIC_FRAME.get())
                .define('i', Items.IRON_INGOT)
                .define('r', Items.REDSTONE)
                .define('g', Items.GOLD_NUGGET)
                .unlockedBy("has_laptop", has(ModTags.Items.LAPTOPS))
                .save(exporter);
        new ShapelessRecipeBuilder(RecipeCategory.MISC, DeviceItems.COMPONENT_MOTHERBOARD_FULL.get(), 1)
                .requires(DeviceItems.COMPONENT_CPU.get(), 1)
                .requires(DeviceItems.COMPONENT_GPU.get(), 1)
                .requires(DeviceItems.COMPONENT_RAM.get(), 1)
                .requires(DeviceItems.COMPONENT_WIFI.get(), 1)
                .requires(DeviceItems.COMPONENT_MOTHERBOARD.get(), 1)
                .unlockedBy("has_motherboard", has(DeviceItems.COMPONENT_MOTHERBOARD.get()))
                .save(exporter);
        new ShapedRecipeBuilder(RecipeCategory.MISC, DeviceItems.WHEEL.get(), 1)
                .pattern("p p")
                .pattern("pip")
                .pattern("p p")
                .define('p', DeviceItems.PLASTIC_FRAME.get())
                .define('i', Items.IRON_BLOCK)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(exporter);
        new ShapedRecipeBuilder(RecipeCategory.MISC, DeviceItems.COMPONENT_SOLID_STATE_DRIVE.get(), 1)
                .pattern("oro")
                .pattern("ofo")
                .pattern("ofo")
                .define('o', Items.OBSIDIAN)
                .define('f', DeviceItems.COMPONENT_FLASH_CHIP.get())
                .define('r', Items.REDSTONE)
                .unlockedBy("has_flash_chip", has(DeviceItems.COMPONENT_FLASH_CHIP.get()))
                .save(exporter);
        new ShapedRecipeBuilder(RecipeCategory.MISC, DeviceItems.COMPONENT_WIFI.get(), 1)
                .pattern(" e ")
                .pattern("idi")
                .pattern("rcr")
                .define('e', Items.END_ROD)
                .define('i', Items.IRON_INGOT)
                .define('d', Items.ENDER_PEARL)
                .define('r', Items.REDSTONE)
                .define('c', DeviceItems.COMPONENT_CIRCUIT_BOARD.get())
                .unlockedBy("has_circuit_board", has(DeviceItems.COMPONENT_CIRCUIT_BOARD.get()))
                .save(exporter);
        new ShapedRecipeBuilder(RecipeCategory.MISC, DeviceItems.COMPONENT_SCREEN.get(), 1)
                .pattern("rgb")
                .pattern("ppp")
                .pattern("cdc")
                .define('r', Items.RED_STAINED_GLASS_PANE)
                .define('g', Items.GREEN_STAINED_GLASS_PANE)
                .define('b', Items.BLUE_STAINED_GLASS_PANE)
                .define('p', Items.PRISMARINE_CRYSTALS)
                .define('c', DeviceItems.COMPONENT_CIRCUIT_BOARD.get())
                .define('d', Items.REDSTONE)
                .unlockedBy("has_circuit_board", has(DeviceItems.COMPONENT_CIRCUIT_BOARD.get()))
                .save(exporter);
        new ShapedRecipeBuilder(RecipeCategory.MISC, DeviceItems.COMPONENT_RAM.get(), 1)
                .pattern("bbb")
                .pattern("rcr")
                .pattern("ggg")
                .define('b', DeviceItems.COMPONENT_CIRCUIT_BOARD.get())
                .define('r', Items.REDSTONE)
                .define('c', Items.ENDER_EYE)
                .define('g', Items.GOLD_NUGGET)
                .unlockedBy("has_circuit_board", has(DeviceItems.COMPONENT_CIRCUIT_BOARD.get()))
                .save(exporter);
        new ShapedRecipeBuilder(RecipeCategory.MISC, DeviceItems.PLASTIC_FRAME.get(), 1)
                .pattern("pp")
                .pattern("pp")
                .define('p', DeviceItems.PLASTIC.get())
                .unlockedBy("has_plastic", has(DeviceItems.PLASTIC.get()))
                .save(exporter);
        new ShapedRecipeBuilder(RecipeCategory.MISC, DeviceItems.COMPONENT_HARD_DRIVE.get(), 1)
                .pattern("nri")
                .pattern("ibi")
                .pattern("ibi")
                .define('n', Items.IRON_NUGGET)
                .define('i', Items.IRON_INGOT)
                .define('r', Items.REDSTONE)
                .define('b', Items.SHULKER_SHELL)
                .unlockedBy("has_plastic", has(DeviceItems.PLASTIC.get()))
                .save(exporter);
        new ShapedRecipeBuilder(RecipeCategory.MISC, DeviceItems.COMPONENT_GPU.get(), 1)
                .pattern("ogo")
                .pattern("rmp")
                .pattern("ccc")
                .define('o', Items.OBSIDIAN)
                .define('g', Items.GOLD_INGOT)
                .define('r', Items.REDSTONE)
                .define('m', DeviceItems.COMPONENT_RAM.get())
                .define('p', Items.PRISMARINE_SHARD)
                .define('c', DeviceItems.COMPONENT_CIRCUIT_BOARD.get())
                .unlockedBy("has_plastic", has(DeviceItems.PLASTIC.get()))
                .save(exporter);
        new ShapedRecipeBuilder(RecipeCategory.MISC, DeviceItems.ETHERNET_CABLE.get(), 1)
                .pattern("pil")
                .pattern("gig")
                .pattern("lip")
                .define('p', Items.PRISMARINE_CRYSTALS)
                .define('i', Items.IRON_INGOT)
                .define('l', DeviceItems.PLASTIC.get())
                .define('g', Items.GOLD_NUGGET)
                .unlockedBy("has_plastic", has(DeviceItems.PLASTIC.get()))
                .save(exporter);
        new ShapelessRecipeBuilder(RecipeCategory.MISC, DeviceItems.PLASTIC_UNREFINED.get(), 1)
                .requires(Items.BLACK_DYE)
                .requires(Items.WHITE_DYE)
                .requires(Items.SLIME_BALL)
                .unlockedBy("has_slime_ball", has(Items.SLIME_BALL))
                .save(exporter);

        //**************************//
        //     Smelting recipes     //
        //**************************//
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(DeviceItems.PLASTIC_UNREFINED.get()), RecipeCategory.MISC, DeviceItems.PLASTIC.get(), 0.5f, 200)
                .unlockedBy("has_plastic_unrefined", has(DeviceItems.PLASTIC_UNREFINED.get()))
                .save(exporter);
    }

    public static void laptop(Consumer<FinishedRecipe> exporter, ItemLike laptop, DyeColor color) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, laptop)
                .define('+', DyeUtils.getWoolFromDye(color))
                .define('/', Items.IRON_INGOT)
                .define('#', DeviceItems.COMPONENT_SCREEN.get())
                .define('.', Items.IRON_NUGGET)
                .define('$', DeviceItems.COMPONENT_BATTERY.get())
                .define('@', DeviceItems.COMPONENT_MOTHERBOARD_FULL.get())
                .define('O', DeviceTags.Items.INTERNAL_STORAGE)
                .pattern("+#+")
                .pattern(".@$")
                .pattern("/O/").group("devices:laptop")
                .unlockedBy(getHasName(Items.NETHERITE_INGOT), has(Items.NETHERITE_INGOT))
                .unlockedBy(getHasName(DeviceItems.COMPONENT_MOTHERBOARD_FULL.get()), has(DeviceItems.COMPONENT_MOTHERBOARD_FULL.get()))
                .save(exporter);
    }
}
