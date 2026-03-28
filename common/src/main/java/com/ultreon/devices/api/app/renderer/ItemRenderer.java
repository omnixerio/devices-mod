package com.ultreon.devices.api.app.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * @author MrCrayfish
 */
public abstract class ItemRenderer<E> {
    public abstract void render(GuiGraphicsExtractor pose, E e, Minecraft mc, int x, int y, int width, int height);
}
