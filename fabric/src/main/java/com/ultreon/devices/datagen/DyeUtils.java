package com.ultreon.devices.datagen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;

public class DyeUtils {
    private enum WoolColor {
        WHITE_WOOL("white_wool", DyeColor.WHITE),
        ORANGE_WOOL("orange_wool", DyeColor.ORANGE),
        MAGENTA_WOOL("magenta_wool", DyeColor.MAGENTA),
        LIGHT_BLUE_WOOL("light_blue_wool", DyeColor.LIGHT_BLUE),
        YELLOW_WOOL("yellow_wool", DyeColor.YELLOW),
        LIME_WOOL("lime_wool", DyeColor.LIME),
        PINK_WOOL("pink_wool", DyeColor.PINK),
        GRAY_WOOL("gray_wool", DyeColor.GRAY),
        LIGHT_GRAY_WOOL("light_gray_wool", DyeColor.LIGHT_GRAY),
        CYAN_WOOL("cyan_wool", DyeColor.CYAN),
        PURPLE_WOOL("purple_wool", DyeColor.PURPLE),
        BLUE_WOOL("blue_wool", DyeColor.BLUE),
        BROWN_WOOL("brown_wool", DyeColor.BROWN),
        GREEN_WOOL("green_wool", DyeColor.GREEN),
        RED_WOOL("red_wool", DyeColor.RED),
        BLACK_WOOL("black_wool", DyeColor.BLACK);

        final Block block;
        final DyeColor dyeColor;
        WoolColor(String block, DyeColor dyeColor) {
            this.block = BuiltInRegistries.BLOCK.get(new ResourceLocation(block)); // accessing registry directly is fine as this class isn't used with forge at all
            this.dyeColor = dyeColor;
        }
    }
    public static Block getWoolFromDye(DyeColor color) {
        for (WoolColor value : WoolColor.values()) {
            if (value.dyeColor.equals(color)) {
                return value.block;
            }
        }
        return WoolColor.WHITE_WOOL.block;
    }
}
