package dev.ultreon.devices.api.app.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/**
 * @author MrCrayfish
 */
public abstract class ItemRenderer<E> {
    public abstract void render(GuiGraphics pose, E e, Minecraft mc, int x, int y, int width, int height);
}
