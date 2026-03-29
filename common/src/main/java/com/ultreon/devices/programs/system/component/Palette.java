package com.ultreon.devices.programs.system.component;

import com.mojang.blaze3d.vertex.*;
import com.ultreon.devices.api.app.Component;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.api.app.component.ComboBox;
import com.ultreon.devices.api.app.component.Slider;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.util.GLHelper;
import com.ultreon.devices.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

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
            if (percentage >= 1d / 6d * 5d) {
                currentColor = new Color(1f, 1f - (percentage - 1f / 6f * 5f) * 6f, 0f);
            } else if (percentage >= 1d / 6d * 4d) {
                currentColor = new Color((percentage - 1f / 6f * 4f) * 6f, 1f, 0f);
            } else if (percentage >= 1d / 6d * 3d) {
                currentColor = new Color(0f, 1f, 1f - (percentage - 1f / 6f * 3f) * 6f);
            } else if (percentage >= 1d / 6d * 2d) {
                currentColor = new Color(0f, (percentage - 1f / 6f * 2f) * 6f, 1f);
            } else if (percentage >= 1d / 6d) {
                currentColor = new Color(1f - (percentage - 1f / 6f) * 6f, 0f, 1f);
            } else if (percentage >= 1d / 6d * 0d) {
                currentColor = new Color(1f, 0f, percentage * 6f);
            }
        });
        layout.addComponent(colorSlider);
    }

    @Override
    protected void render(GuiGraphicsExtractor graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        graphics.fill(x, y, x + 52, y + 52, Color.DARK_GRAY.getRGB());

//        Tesselator tessellator = Tesselator.getInstance();
//        BufferBuilder buffer = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
//        buffer.addVertex((double) x + 1, y + 1 + 50, 1).color(0f, 0f, 0f, 1f).endVertex();
//        buffer.addVertex(x + 1 + 50, y + 1 + 50, 1).color(0f, 0f, 0f, 1f).endVertex();
//        buffer.addVertex(x + 1 + 50, (double) y + 1, 1).color(currentColor.getRed() / 255f, currentColor.getGreen() / 255f, currentColor.getBlue() / 255f, 1f).endVertex();
//        buffer.addVertex((double) x + 1, (double) y + 1, 1).color(1f, 1f, 1f, 1f).endVertex();
//        tessellator.clear();

        // TODO: Implement palette rendering

        graphics.fill(x + 1, y + 1, x + 51, y + 51, currentColor.getRGB());
    }

    @Override
    protected void handleMouseClick(MouseButtonEvent event) {
        if (event.button() != 0) return;

        if (GuiHelper.isMouseInside((int) event.x(), (int) event.y(), xPosition + 1, yPosition + 1, xPosition + 51, yPosition + 51)) {
            colorPicker.setValue(GLHelper.getPixel((int) event.x(), (int) event.y()).getRGB());
        }
    }
}
