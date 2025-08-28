package com.ultreon.devices.api.app.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.devices.api.app.IIcon;
import com.ultreon.devices.core.Laptop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;

import java.awt.*;

public class TextField extends TextArea {
    private IIcon icon;

    /**
     * Default text field constructor
     *
     * @param left  how many pixels from the left
     * @param top   how many pixels from the top
     * @param width the width of the text field
     */
    public TextField(int left, int top, int width) {
        super(left, top, width, 16);
        this.setScrollBarVisible(false);
        this.setMaxLines(1);
    }

    @Override
    public void render(GuiGraphics graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        if (icon != null) {
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            Color bgColor = new Color(color(backgroundColor, getColorScheme().getBackgroundColor()));
            graphics.fill(x, y, x + 15, y + 16, bgColor.darker().darker().getRGB());
            graphics.fill(x + 1, y + 1, x + 15, y + 15, bgColor.brighter().getRGB());
            icon.draw(graphics, mc, x + 3, y + 3);
        }
        super.render(graphics, laptop, mc, x + (icon != null ? 15 : 0), y, mouseX, mouseY, windowActive, partialTicks);
    }

    @Override
    public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
        super.handleMouseClick(mouseX - (icon != null ? 15 : 0), mouseY, mouseButton);
    }

    @Override
    protected void handleMouseDrag(int mouseX, int mouseY, int mouseButton) {
        super.handleMouseDrag(mouseX - (icon != null ? 15 : 0), mouseY, mouseButton);
    }

    @Override
    protected void handleMouseRelease(int mouseX, int mouseY, int mouseButton) {
        super.handleMouseRelease(mouseX - (icon != null ? 15 : 0), mouseY, mouseButton);
    }

    public void setIcon(IIcon icon) {
        if (this.icon == null) {
            width -= 15;
        } else if (icon == null) {
            width += 15;
        }
        this.icon = icon;
    }
}
