package dev.ultreon.devices.util;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public class ItemColors {
    public static ItemLike woolByColor(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.WHITE_WOOL;
            case ORANGE -> Items.ORANGE_WOOL;
            case MAGENTA -> Items.MAGENTA_WOOL;
            case LIGHT_BLUE -> Items.LIGHT_BLUE_WOOL;
            case YELLOW -> Items.YELLOW_WOOL;
            case LIME -> Items.LIME_WOOL;
            case PINK -> Items.PINK_WOOL;
            case GRAY -> Items.GRAY_WOOL;
            case LIGHT_GRAY -> Items.LIGHT_GRAY_WOOL;
            case CYAN -> Items.CYAN_WOOL;
            case PURPLE -> Items.PURPLE_WOOL;
            case BLUE -> Items.BLUE_WOOL;
            case BROWN -> Items.BROWN_WOOL;
            case GREEN -> Items.GREEN_WOOL;
            case RED -> Items.RED_WOOL;
            case BLACK -> Items.BLACK_WOOL;
        };
    }

    public static ItemLike dyeByColor(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.WHITE_DYE;
            case ORANGE -> Items.ORANGE_DYE;
            case MAGENTA -> Items.MAGENTA_DYE;
            case LIGHT_BLUE -> Items.LIGHT_BLUE_DYE;
            case YELLOW -> Items.YELLOW_DYE;
            case LIME -> Items.LIME_DYE;
            case PINK -> Items.PINK_DYE;
            case GRAY -> Items.GRAY_DYE;
            case LIGHT_GRAY -> Items.LIGHT_GRAY_DYE;
            case CYAN -> Items.CYAN_DYE;
            case PURPLE -> Items.PURPLE_DYE;
            case BLUE -> Items.BLUE_DYE;
            case BROWN -> Items.BROWN_DYE;
            case GREEN -> Items.GREEN_DYE;
            case RED -> Items.RED_DYE;
            case BLACK -> Items.BLACK_DYE;
        };
    }

    public static ItemLike carpetByColor(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.WHITE_CARPET;
            case ORANGE -> Items.ORANGE_CARPET;
            case MAGENTA -> Items.MAGENTA_CARPET;
            case LIGHT_BLUE -> Items.LIGHT_BLUE_CARPET;
            case YELLOW -> Items.YELLOW_CARPET;
            case LIME -> Items.LIME_CARPET;
            case PINK -> Items.PINK_CARPET;
            case GRAY -> Items.GRAY_CARPET;
            case LIGHT_GRAY -> Items.LIGHT_GRAY_CARPET;
            case CYAN -> Items.CYAN_CARPET;
            case PURPLE -> Items.PURPLE_CARPET;
            case BLUE -> Items.BLUE_CARPET;
            case BROWN -> Items.BROWN_CARPET;
            case GREEN -> Items.GREEN_CARPET;
            case RED -> Items.RED_CARPET;
            case BLACK -> Items.BLACK_CARPET;
        };
    }

    public static ItemLike terracottaByColor(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.WHITE_TERRACOTTA;
            case ORANGE -> Items.ORANGE_TERRACOTTA;
            case MAGENTA -> Items.MAGENTA_TERRACOTTA;
            case LIGHT_BLUE -> Items.LIGHT_BLUE_TERRACOTTA;
            case YELLOW -> Items.YELLOW_TERRACOTTA;
            case LIME -> Items.LIME_TERRACOTTA;
            case PINK -> Items.PINK_TERRACOTTA;
            case GRAY -> Items.GRAY_TERRACOTTA;
            case LIGHT_GRAY -> Items.LIGHT_GRAY_TERRACOTTA;
            case CYAN -> Items.CYAN_TERRACOTTA;
            case PURPLE -> Items.PURPLE_TERRACOTTA;
            case BLUE -> Items.BLUE_TERRACOTTA;
            case BROWN -> Items.BROWN_TERRACOTTA;
            case GREEN -> Items.GREEN_TERRACOTTA;
            case RED -> Items.RED_TERRACOTTA;
            case BLACK -> Items.BLACK_TERRACOTTA;
        };
    }

    public static ItemLike glassByColor(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.WHITE_STAINED_GLASS;
            case ORANGE -> Items.ORANGE_STAINED_GLASS;
            case MAGENTA -> Items.MAGENTA_STAINED_GLASS;
            case LIGHT_BLUE -> Items.LIGHT_BLUE_STAINED_GLASS;
            case YELLOW -> Items.YELLOW_STAINED_GLASS;
            case LIME -> Items.LIME_STAINED_GLASS;
            case PINK -> Items.PINK_STAINED_GLASS;
            case GRAY -> Items.GRAY_STAINED_GLASS;
            case LIGHT_GRAY -> Items.LIGHT_GRAY_STAINED_GLASS;
            case CYAN -> Items.CYAN_STAINED_GLASS;
            case PURPLE -> Items.PURPLE_STAINED_GLASS;
            case BLUE -> Items.BLUE_STAINED_GLASS;
            case BROWN -> Items.BROWN_STAINED_GLASS;
            case GREEN -> Items.GREEN_STAINED_GLASS;
            case RED -> Items.RED_STAINED_GLASS;
            case BLACK -> Items.BLACK_STAINED_GLASS;
        };
    }

    public static ItemLike glassPaneByColor(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.WHITE_STAINED_GLASS_PANE;
            case ORANGE -> Items.ORANGE_STAINED_GLASS_PANE;
            case MAGENTA -> Items.MAGENTA_STAINED_GLASS_PANE;
            case LIGHT_BLUE -> Items.LIGHT_BLUE_STAINED_GLASS_PANE;
            case YELLOW -> Items.YELLOW_STAINED_GLASS_PANE;
            case LIME -> Items.LIME_STAINED_GLASS_PANE;
            case PINK -> Items.PINK_STAINED_GLASS_PANE;
            case GRAY -> Items.GRAY_STAINED_GLASS_PANE;
            case LIGHT_GRAY -> Items.LIGHT_GRAY_STAINED_GLASS_PANE;
            case CYAN -> Items.CYAN_STAINED_GLASS_PANE;
            case PURPLE -> Items.PURPLE_STAINED_GLASS_PANE;
            case BLUE -> Items.BLUE_STAINED_GLASS_PANE;
            case BROWN -> Items.BROWN_STAINED_GLASS_PANE;
            case GREEN -> Items.GREEN_STAINED_GLASS_PANE;
            case RED -> Items.RED_STAINED_GLASS_PANE;
            case BLACK -> Items.BLACK_STAINED_GLASS_PANE;
        };
    }

    public static ItemLike concreteByColor(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.WHITE_CONCRETE;
            case ORANGE -> Items.ORANGE_CONCRETE;
            case MAGENTA -> Items.MAGENTA_CONCRETE;
            case LIGHT_BLUE -> Items.LIGHT_BLUE_CONCRETE;
            case YELLOW -> Items.YELLOW_CONCRETE;
            case LIME -> Items.LIME_CONCRETE;
            case PINK -> Items.PINK_CONCRETE;
            case GRAY -> Items.GRAY_CONCRETE;
            case LIGHT_GRAY -> Items.LIGHT_GRAY_CONCRETE;
            case CYAN -> Items.CYAN_CONCRETE;
            case PURPLE -> Items.PURPLE_CONCRETE;
            case BLUE -> Items.BLUE_CONCRETE;
            case BROWN -> Items.BROWN_CONCRETE;
            case GREEN -> Items.GREEN_CONCRETE;
            case RED -> Items.RED_CONCRETE;
            case BLACK -> Items.BLACK_CONCRETE;
        };
    }

    public static ItemLike concretePowderByColor(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.WHITE_CONCRETE_POWDER;
            case ORANGE -> Items.ORANGE_CONCRETE_POWDER;
            case MAGENTA -> Items.MAGENTA_CONCRETE_POWDER;
            case LIGHT_BLUE -> Items.LIGHT_BLUE_CONCRETE_POWDER;
            case YELLOW -> Items.YELLOW_CONCRETE_POWDER;
            case LIME -> Items.LIME_CONCRETE_POWDER;
            case PINK -> Items.PINK_CONCRETE_POWDER;
            case GRAY -> Items.GRAY_CONCRETE_POWDER;
            case LIGHT_GRAY -> Items.LIGHT_GRAY_CONCRETE_POWDER;
            case CYAN -> Items.CYAN_CONCRETE_POWDER;
            case PURPLE -> Items.PURPLE_CONCRETE_POWDER;
            case BLUE -> Items.BLUE_CONCRETE_POWDER;
            case BROWN -> Items.BROWN_CONCRETE_POWDER;
            case GREEN -> Items.GREEN_CONCRETE_POWDER;
            case RED -> Items.RED_CONCRETE_POWDER;
            case BLACK -> Items.BLACK_CONCRETE_POWDER;
        };
    }

    public static ItemLike glazedTerracottaByColor(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.WHITE_GLAZED_TERRACOTTA;
            case ORANGE -> Items.ORANGE_GLAZED_TERRACOTTA;
            case MAGENTA -> Items.MAGENTA_GLAZED_TERRACOTTA;
            case LIGHT_BLUE -> Items.LIGHT_BLUE_GLAZED_TERRACOTTA;
            case YELLOW -> Items.YELLOW_GLAZED_TERRACOTTA;
            case LIME -> Items.LIME_GLAZED_TERRACOTTA;
            case PINK -> Items.PINK_GLAZED_TERRACOTTA;
            case GRAY -> Items.GRAY_GLAZED_TERRACOTTA;
            case LIGHT_GRAY -> Items.LIGHT_GRAY_GLAZED_TERRACOTTA;
            case CYAN -> Items.CYAN_GLAZED_TERRACOTTA;
            case PURPLE -> Items.PURPLE_GLAZED_TERRACOTTA;
            case BLUE -> Items.BLUE_GLAZED_TERRACOTTA;
            case BROWN -> Items.BROWN_GLAZED_TERRACOTTA;
            case GREEN -> Items.GREEN_GLAZED_TERRACOTTA;
            case RED -> Items.RED_GLAZED_TERRACOTTA;
            case BLACK -> Items.BLACK_GLAZED_TERRACOTTA;
        };
    }

    public static ItemLike bedByColor(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.WHITE_BED;
            case ORANGE -> Items.ORANGE_BED;
            case MAGENTA -> Items.MAGENTA_BED;
            case LIGHT_BLUE -> Items.LIGHT_BLUE_BED;
            case YELLOW -> Items.YELLOW_BED;
            case LIME -> Items.LIME_BED;
            case PINK -> Items.PINK_BED;
            case GRAY -> Items.GRAY_BED;
            case LIGHT_GRAY -> Items.LIGHT_GRAY_BED;
            case CYAN -> Items.CYAN_BED;
            case PURPLE -> Items.PURPLE_BED;
            case BLUE -> Items.BLUE_BED;
            case BROWN -> Items.BROWN_BED;
            case GREEN -> Items.GREEN_BED;
            case RED -> Items.RED_BED;
            case BLACK -> Items.BLACK_BED;
        };
    }

    public static ItemLike shulkerBoxByColor(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.WHITE_SHULKER_BOX;
            case ORANGE -> Items.ORANGE_SHULKER_BOX;
            case MAGENTA -> Items.MAGENTA_SHULKER_BOX;
            case LIGHT_BLUE -> Items.LIGHT_BLUE_SHULKER_BOX;
            case YELLOW -> Items.YELLOW_SHULKER_BOX;
            case LIME -> Items.LIME_SHULKER_BOX;
            case PINK -> Items.PINK_SHULKER_BOX;
            case GRAY -> Items.GRAY_SHULKER_BOX;
            case LIGHT_GRAY -> Items.LIGHT_GRAY_SHULKER_BOX;
            case CYAN -> Items.CYAN_SHULKER_BOX;
            case PURPLE -> Items.PURPLE_SHULKER_BOX;
            case BLUE -> Items.BLUE_SHULKER_BOX;
            case BROWN -> Items.BROWN_SHULKER_BOX;
            case GREEN -> Items.GREEN_SHULKER_BOX;
            case RED -> Items.RED_SHULKER_BOX;
            case BLACK -> Items.BLACK_SHULKER_BOX;
        };
    }

    public static ItemLike candleByColor(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.WHITE_CANDLE;
            case ORANGE -> Items.ORANGE_CANDLE;
            case MAGENTA -> Items.MAGENTA_CANDLE;
            case LIGHT_BLUE -> Items.LIGHT_BLUE_CANDLE;
            case YELLOW -> Items.YELLOW_CANDLE;
            case LIME -> Items.LIME_CANDLE;
            case PINK -> Items.PINK_CANDLE;
            case GRAY -> Items.GRAY_CANDLE;
            case LIGHT_GRAY -> Items.LIGHT_GRAY_CANDLE;
            case CYAN -> Items.CYAN_CANDLE;
            case PURPLE -> Items.PURPLE_CANDLE;
            case BLUE -> Items.BLUE_CANDLE;
            case BROWN -> Items.BROWN_CANDLE;
            case GREEN -> Items.GREEN_CANDLE;
            case RED -> Items.RED_CANDLE;
            case BLACK -> Items.BLACK_CANDLE;
        };
    }

    public static ItemLike bannerByColor(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.WHITE_BANNER;
            case ORANGE -> Items.ORANGE_BANNER;
            case MAGENTA -> Items.MAGENTA_BANNER;
            case LIGHT_BLUE -> Items.LIGHT_BLUE_BANNER;
            case YELLOW -> Items.YELLOW_BANNER;
            case LIME -> Items.LIME_BANNER;
            case PINK -> Items.PINK_BANNER;
            case GRAY -> Items.GRAY_BANNER;
            case LIGHT_GRAY -> Items.LIGHT_GRAY_BANNER;
            case CYAN -> Items.CYAN_BANNER;
            case PURPLE -> Items.PURPLE_BANNER;
            case BLUE -> Items.BLUE_BANNER;
            case BROWN -> Items.BROWN_BANNER;
            case GREEN -> Items.GREEN_BANNER;
            case RED -> Items.RED_BANNER;
            case BLACK -> Items.BLACK_BANNER;
        };
    }
}
