package dev.ultreon.devices.api.app.component;

import dev.ultreon.devices.api.app.Component;
import dev.ultreon.devices.core.Laptop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;

import java.awt.*;

public class Spinner extends Component {
    protected final int MAX_PROGRESS = 31;
    protected int currentProgress = 0;

    protected Color spinnerColor = Color.WHITE;

    /**
     * Default spinner constructor
     *
     * @param left how many pixels from the left
     * @param top  how many pixels from the top
     */
    public Spinner(int left, int top) {
        super(left, top);
    }

    @Override
    public void handleTick() {
        if (currentProgress >= MAX_PROGRESS) {
            currentProgress = 0;
        }
        currentProgress++;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        if (this.visible) {
            Color bgColor = new Color(getColorScheme().getBackgroundColor()).brighter().brighter();
            float[] hsb = Color.RGBtoHSB(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), null);
            bgColor = new Color(Color.HSBtoRGB(hsb[0], hsb[1], 1f));
            graphics.blit(RenderPipelines.GUI_TEXTURED, Component.COMPONENTS_GUI, xPosition, yPosition, currentProgress % 8 * 12, 12 + 12 * (int) Math.floor((double) currentProgress / 8), 12, 12, 256, 256, bgColor.getRGB());
        }
    }
}
