package dev.ultreon.devices.api.utils;

import dev.ultreon.devices.OmnixerioDevicesCommon;
import dev.ultreon.devices.core.Laptop;
import dev.ultreon.devices.object.AppInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

import java.awt.*;

@SuppressWarnings("unused")
public class RenderUtil {
    public static void renderItem(GuiGraphicsExtractor graphics, int x, int y, ItemStack stack, boolean overlay) {
        Matrix3x2fStack last = graphics.pose();
        graphics.item(stack, x, y);
        if (overlay)
            graphics.itemDecorations(Minecraft.getInstance().font, stack, x, y);
    }

    public static void drawIcon(GuiGraphicsExtractor graphics, double x, double y, AppInfo info, int width, int height) {
        //Gui.blit(pose, (int) x, (int) y, width, height, u, v, sourceWidth, sourceHeight, (int) textureWidth, (int) textureHeight);
        if (info == null || info.getIcon().getBase().getU() == -1 && info.getIcon().getBase().getV() == -1) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, info.getIcon().getBase().getIdentifier(), (int) x, (int) y, width, height);
            return;
        }
        var glyphs = new AppInfo.Icon.Glyph[]{info.getIcon().getBase(), info.getIcon().getOverlay0(), info.getIcon().getOverlay1()};
        for (AppInfo.Icon.Glyph glyph : glyphs) {
            if (glyph.getU() == -1 || glyph.getV() == -1) continue;
            var col = new Color(info.getTint(glyph.getType()));
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, info.getIcon().getBase().getIdentifier(), (int) x, (int) y, width, height, col.getRGB());
            //image.init(layout);
        }
    }

    public static void drawApplicationIcon(GuiGraphicsExtractor graphics, @Nullable AppInfo info, double x, double y) {
        //TODO: Reset color GlStateManager.color(1f, 1f, 1f);
        if (info != null) {
            drawIcon(graphics, x, y, info, 14, 14);
        } else {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, OmnixerioDevicesCommon.id("app/icon/base/missing"), (int) x, (int) y, 14, 14);
        }
    }

    public static void drawStringClipped(GuiGraphicsExtractor graphics, String text, int x, int y, int width, int color, boolean shadow) {
        if (shadow) graphics.text(Laptop.getFontStatic(), clipStringToWidth(text, width) + ChatFormatting.RESET, x, y, color);
        else graphics.text(Laptop.getFontStatic(), Laptop.getFontStatic().plainSubstrByWidth(text, width) + ChatFormatting.RESET, x, y, color, false);
    }

    public static String clipStringToWidth(String text, int width) {
        Font fontRenderer = Laptop.getFontStatic();
        String clipped = text;
        if (fontRenderer.width(clipped) > width) {
            clipped = fontRenderer.plainSubstrByWidth(clipped, width - 8) + "...";
        }
        return clipped;
    }

    public static boolean isMouseInside(int mouseX, int mouseY, int x1, int y1, int x2, int y2) {
        return mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2;
    }

    public static int color(int color, int defaultColor) {
        return color > 0 ? color : defaultColor;
    }
}
