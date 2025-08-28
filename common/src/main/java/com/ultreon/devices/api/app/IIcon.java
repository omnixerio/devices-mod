package com.ultreon.devices.api.app;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public interface IIcon {
    ResourceLocation getIconAsset();

    int getIconSize();

    int getGridWidth();

    int getGridHeight();

    /**
     * Width of the source texture in pixels.
     *
     * @return The source width.
     */
    int getSourceWidth();

    /**
     * Height of the source texture in pixels.
     *
     * @return The source height.
     */
    int getSourceHeight();

    int getU();

    int getV();

    int getOrdinal();

    default void draw(GuiGraphics graphics, Minecraft mc, int x, int y) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, getIconAsset());
        int size = getIconSize();
        int assetWidth = getGridWidth() * size;
//        java.lang.DebugLog.log("assetWidth = " + assetWidth);
        int assetHeight = getGridHeight() * size;
//        java.lang.DebugLog.log("assetHeight = " + assetHeight);
//        GuiComponent.blit(pose, x + contentX, y + iconY, iconWidth, iconHeight, iconU, iconV, iconWidth, iconHeight, iconSourceWidth, iconSourceHeight);
        graphics.blit(getIconAsset(), x, y, size, size, getU(), getV(), size, size, assetWidth, assetHeight);
    }
}
