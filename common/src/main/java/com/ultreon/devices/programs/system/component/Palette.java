package com.ultreon.devices.programs.system.component;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.ultreon.devices.api.app.Component;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.api.app.component.ComboBox;
import com.ultreon.devices.api.app.component.Slider;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.util.GLHelper;
import com.ultreon.devices.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;

import java.awt.*;

/**
 * @author MrCrayfish
 */
public class Palette extends Component {
    private final ComboBox.Custom<Integer> colorPicker;

    private Color currentColor = Color.RED;

    private Slider colorSlider;

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
    public Palette(int left, int top, ComboBox.Custom<Integer> colorPicker) {
        super(left, top);
        this.colorPicker = colorPicker;
    }

    @Override
    protected void init(Layout layout) {
        colorSlider = new Slider(5, 58, 52);
        colorSlider.setSlideListener(percentage -> {
            if (percentage >= (1d / 6d) * 5d) {
                currentColor = new Color(1f, 1f - (percentage - (1f / 6f) * 5f) * 6f, 0f);
            } else if (percentage >= (1d / 6d) * 4d) {
                currentColor = new Color((percentage - ((1f / 6f) * 4f)) * 6f, 1f, 0f);
            } else if (percentage >= (1d / 6d) * 3d) {
                currentColor = new Color(0f, 1f, 1f - (percentage - ((1f / 6f) * 3f)) * 6f);
            } else if (percentage >= (1d / 6d) * 2d) {
                currentColor = new Color(0f, (percentage - ((1f / 6f) * 2f)) * 6f, 1f);
            } else if (percentage >= (1d / 6d)) {
                currentColor = new Color(1f - (percentage - ((1f / 6f))) * 6f, 0f, 1f);
            } else if (percentage >= (1d / 6d) * 0d) {
                currentColor = new Color(1f, 0f, percentage * 6f);
            }
        });
        layout.addComponent(colorSlider);
    }

    @Override
    protected void render(GuiGraphics graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        graphics.fill(x, y, x + 52, y + 52, Color.DARK_GRAY.getRGB());

        // Todo: Disable lighting somehow.
        Lighting.setupForFlatItems();
//        RenderSystem.disableLighting();
        // RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        // Todo: Disable alpha somehow
//        RenderSystem.disableAlpha();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        // Todo: Make shade model for GL_SMOOTH possible.
       // RenderSystem.setShaderTexture(0, GameRenderer.());
//        RenderSystem.shadeModel(GL11.GL_SMOOTH);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex((double) x + 1, y + 1 + 50, 1).color(0f, 0f, 0f, 1f).endVertex();
        buffer.vertex(x + 1 + 50, y + 1 + 50, 1).color(0f, 0f, 0f, 1f).endVertex();
        buffer.vertex(x + 1 + 50, (double) y + 1, 1).color(currentColor.getRed() / 255f, currentColor.getGreen() / 255f, currentColor.getBlue() / 255f, 1f).endVertex();
        buffer.vertex((double) x + 1, (double) y + 1, 1).color(1f, 1f, 1f, 1f).endVertex();
        tessellator.end();

        // Todo: Make shade model flag again.
//        GlStateManager.shadeModel(GL11.GL_FLAT);
        RenderSystem.disableBlend();
        // Todo: Make enabling alpha possible.
//        RenderSystem.enableAlpha();
        // RenderSystem.enableTexture();
        Lighting.setupFor3DItems();
    }

    @Override
    protected void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton != 0) return;

        if (GuiHelper.isMouseInside(mouseX, mouseY, xPosition + 1, yPosition + 1, xPosition + 51, yPosition + 51)) {
            colorPicker.setValue(GLHelper.getPixel(mouseX, mouseY).getRGB());
        }
    }
}
