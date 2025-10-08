package dev.ultreon.devices.client;

import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

/// @author Qubix
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
