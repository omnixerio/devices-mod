package com.ultreon.devices.api.app.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public abstract class ListItemRenderer<E> {
    private final int height;

    public ListItemRenderer(int height) {
        this.height = height;
    }

    public final int getHeight() {
        return height;
    }

    public abstract void render(GuiGraphicsExtractor graphics, E e, Minecraft mc, int x, int y, int width, int height, boolean selected);
}
