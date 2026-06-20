package com.ultreon.devices.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.ultreon.devices.core.Window;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class GuiButtonClose extends Button {
    public GuiButtonClose(int x, int y) {
        super(x, y, 11, 11, Component.literal(""),
                (button) -> { }, (output)-> Component.empty());
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            RenderSystem.setShaderTexture(0, Window.WINDOW_GUI);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;

            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(770, 771, 1, 0);
            RenderSystem.blendFunc(770, 771);

            if (this.isHovered) {
                graphics.blit(Window.WINDOW_GUI, this.getX(), this.getY(), this.width + 15, 0, this.width, this.height);
            }
        }
    }

    public boolean isHovered() {
        return isHovered;
    }
}
