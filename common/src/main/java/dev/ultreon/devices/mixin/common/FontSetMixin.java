package dev.ultreon.devices.mixin.common;

import net.minecraft.client.gui.font.FontSet;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FontSet.class)
public class FontSetMixin {
//    @Shadow @Final private ResourceLocation name;
//    private static final GlyphInfo DEVICES_TAB_INFO = () -> 16.0f;
//
//    @Inject(method = "getGlyphInfoForSpace", at = @At("HEAD"))
//    public void.json getGlyphInfoForSpace(int i, CallbackInfoReturnable<GlyphInfo> cir) {
//        if (name.equals(Devices.res("laptop")) && i == 9) {
//            cir.setReturnValue(DEVICES_TAB_INFO);
//        }
//    }
}
