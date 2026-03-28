package com.ultreon.devices.object;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.devices.api.app.Component;
import com.ultreon.devices.api.app.component.Slider;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.awt.*;

public class ColorGrid extends Component {
    private static final Color[] colors = {Color.BLACK, Color.GRAY, Color.LIGHT_GRAY, Color.WHITE, Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, new Color(0, 150, 0), new Color(0, 255, 255), new Color(0, 148, 255), Color.BLUE, new Color(72, 0, 255), Color.MAGENTA, new Color(255, 0, 110)};

    private final int hoverColor = new Color(255, 255, 255, 100).getRGB();

    private final com.ultreon.devices.object.Canvas canvas;
    private final Slider redSlider;
    private final Slider greenSlider;
    private final Slider blueSlider;

    private final int width;

    public ColorGrid(int left, int top, int width, Canvas canvas, Slider redSlider, Slider greenSlider, Slider blueSlider) {
        super(left, top);
        this.width = width;
        this.canvas = canvas;
        this.redSlider = redSlider;
        this.greenSlider = greenSlider;
        this.blueSlider = blueSlider;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        int endX = xPosition + width + 2;
        int endY = yPosition + (colors.length / 5) * 10 + 2;
        graphics.fill(xPosition, yPosition, endX, endY, Color.DARK_GRAY.getRGB());
        for (int i = 0; i < colors.length; i++) {
            int startX = xPosition + (i % 5) * 10 + 1;
            int startY = yPosition + (i / 5) * 10 + 1;
            graphics.fill(startX, startY, startX + 10, startY + 10, colors[i].getRGB());
        }

        if (GuiHelper.isMouseInside(mouseX, mouseY, xPosition + 1, yPosition + 1, endX - 2, endY - 2)) {
            int boxX = (mouseX - xPosition - 1) / 10;
            int boxY = (mouseY - yPosition - 1) / 10;
            graphics. fill(xPosition + (boxX * 10) + 1, yPosition + (boxY * 10) + 1, xPosition + (boxX * 10) + 11, yPosition + (boxY * 10) + 11, hoverColor);
        }
    }

    @Override
    public void handleMouseClick(MouseButtonEvent event) {
        int endX = xPosition + width + 2;
        int endY = yPosition + (colors.length / 5) * 10 + 2;
        if (GuiHelper.isMouseInside(mouseX, mouseY, xPosition + 1, yPosition + 1, endX - 2, endY - 2)) {
            int boxX = (mouseX - xPosition - 1) / 10;
            int boxY = (mouseY - yPosition - 1) / 10;
            int index = boxX + boxY * 5;
            redSlider.setPercentage(colors[index].getRed() / 255F);
            greenSlider.setPercentage(colors[index].getGreen() / 255F);
            blueSlider.setPercentage(colors[index].getBlue() / 255F);
            canvas.setRed(redSlider.getPercentage());
            canvas.setGreen(greenSlider.getPercentage());
            canvas.setBlue(blueSlider.getPercentage());
        }
    }

}
