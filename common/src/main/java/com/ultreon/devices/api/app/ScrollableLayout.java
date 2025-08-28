package com.ultreon.devices.api.app;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.devices.api.app.component.Text;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.util.GLHelper;
import com.ultreon.devices.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

import java.awt.*;

/**
 * @author MrCrayfish
 */
public class ScrollableLayout extends Layout {
    protected int placeholderColor = new Color(1f, 1f, 1f, 0.35f).getRGB();

    protected int scroll;
    private final int visibleHeight;
    private int scrollSpeed = 5;

    public ScrollableLayout(int width, int height, int visibleHeight) {
        super(width, height);
        this.visibleHeight = visibleHeight;
    }

    /**
     * The default constructor for a component.
     * <p>
     * Laying out components is simply relative positioning. So for left (x position),
     * specific how many pixels from the left of the application window you want
     * it to be positioned at. The top is the same, but instead from the top (y position).
     *
     * @param left how many pixels from the left
     * @param top  how many pixels from the top
     */
    public ScrollableLayout(int left, int top, int width, int height, int visibleHeight) {
        super(left,  top, Math.max(13, width), Math.max(1, height));
        this.visibleHeight = visibleHeight;
    }

    @Override
    public void render(GuiGraphics graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        if (!visible)
            return;

        GLHelper.pushScissor(x, y, width, visibleHeight);
        super.render(graphics, laptop, mc, x, y - scroll, mouseX, mouseY, windowActive, partialTicks);
        GLHelper.popScissor();
    }

    @Override
    public void renderOverlay(GuiGraphics graphics, Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive) {
        if (!visible)
            return;

        super.renderOverlay(graphics, laptop, mc, mouseX, mouseY, windowActive);

        if (this.height > this.visibleHeight) {
            int visibleScrollBarHeight = visibleHeight;
            int scrollBarHeight = Math.max(20, (int) (visibleHeight / (float) height * (float) visibleScrollBarHeight));
            float scrollPercentage = Mth.clamp(scroll / (float) (height - visibleHeight), 0f, 1f);
            int scrollBarY = (int) ((visibleScrollBarHeight - scrollBarHeight) * scrollPercentage);
            int scrollY = yPosition + scrollBarY;
            graphics.fill(xPosition + width - 5, scrollY, xPosition + width - 2, scrollY + scrollBarHeight, placeholderColor);
        }
    }

    @Override
    public void updateComponents(int x, int y) {
        this.xPosition = x + left;
        this.yPosition = y + top;
        for (Component c : components) {
            c.updateComponents(x + left, y + top - scroll);
        }
    }

    @Override
    public void handleMouseScroll(int mouseX, int mouseY, boolean direction) {
        if (!visible || !enabled)
            return;

        if (GuiHelper.isMouseWithin(mouseX, mouseY, xPosition, yPosition, width, visibleHeight) && height > visibleHeight) {
            scroll += direction ? -scrollSpeed : scrollSpeed;
            if (scroll + visibleHeight > height) {
                scroll = height - visibleHeight;
            } else if (scroll < 0) {
                scroll = 0;
            }
            this.updateComponents(xPosition - left, yPosition - top);
        }
    }

    @Override
    public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (GuiHelper.isMouseWithin(mouseX, mouseY, xPosition, yPosition, width, visibleHeight)) {
            super.handleMouseClick(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void handleMouseRelease(int mouseX, int mouseY, int mouseButton) {
        if (GuiHelper.isMouseWithin(mouseX, mouseY, xPosition, yPosition, width, visibleHeight)) {
            super.handleMouseRelease(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void handleMouseDrag(int mouseX, int mouseY, int mouseButton) {
        if (GuiHelper.isMouseWithin(mouseX, mouseY, xPosition, yPosition, width, visibleHeight)) {
            super.handleMouseDrag(mouseX, mouseY, mouseButton);
        }
    }

    public static ScrollableLayout create(int left, int top, int width, int visibleHeight, String text) {
        return create(left, top, width, visibleHeight, text, false);
    }

    public static ScrollableLayout create(int left, int top, int width, int visibleHeight, String text, boolean shadow) {
        Text t = new Text(text, 0, 0, width);
        t.setShadow(shadow);
        ScrollableLayout layout = new ScrollableLayout(left, top, t.getWidth(), t.getHeight(), visibleHeight);
        layout.addComponent(t);
        return layout;
    }

    public void setScrollSpeed(int scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }

    public void resetScroll() {
        this.scroll = 0;
        this.updateComponents(xPosition - left, yPosition - top);
    }
}
