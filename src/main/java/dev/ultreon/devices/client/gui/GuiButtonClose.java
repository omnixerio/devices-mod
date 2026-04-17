package dev.ultreon.devices.client.gui;

import dev.ultreon.devices.core.Window;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import org.jspecify.annotations.NonNull;

public class GuiButtonClose extends Button {
    public GuiButtonClose(int x, int y) {
        super(x, y, 11, 11, Component.literal(""),
                _ -> { }, _ -> MutableComponent.create(new PlainTextContents.LiteralContents("")));
    }

    @Override
    protected void extractContents(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;

            if (this.isHovered) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Window.CLOSE_HOVER, this.getX(), this.getY(), 11, 11);
            } else {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Window.CLOSE, this.getX(), this.getY(), 11, 11);
            }
        }
    }
}
