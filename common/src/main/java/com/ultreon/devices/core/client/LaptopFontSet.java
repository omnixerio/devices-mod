package com.ultreon.devices.core.client;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * @author Qboi123
 */
public class LaptopFontSet extends FontSet {
//    private static final GlyphInfo TAB_INFO = new GlyphInfo() {
//        @Override
//        public float getAdvance() {
//            return 0;
//        }
//
//        @Override
//        public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> function) {
//            return null;
//        }
//    };

    public LaptopFontSet(TextureManager pTextureManager, ResourceLocation pName) {
        super(pTextureManager, pName);
    }

//    @Nullable
//    @Override
//    public GlyphInfo getGlyphInfo(int i, boolean notFishy) {
//        return i == 9 ? TAB_INFO : super.getGlyphInfo(i, notFishy);
//    }
}
