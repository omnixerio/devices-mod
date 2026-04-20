package dev.ultreon.devices.util;

import net.minecraft.client.Minecraft;

public class ScaledResolution {
    private final double scaleFactor;
    private final double scale;
    private int scaledWidth;
    private int scaledHeight;

    public ScaledResolution(Minecraft mc) {
        this.scaleFactor = mc.getWindow().getGuiScale();
        this.scale = this.scaleFactor == 0 ? 1 : this.scaleFactor;
        this.updateScaledWidthAndHeight();
    }

    private void updateScaledWidthAndHeight() {
        this.scaledWidth = (int) (this.scale * Minecraft.getInstance().getWindow().getScreenWidth());
        this.scaledHeight = (int) (this.scale * Minecraft.getInstance().getWindow().getScreenHeight());
    }

    public double getScaleFactor() {
        return scaleFactor;
    }

    public double getScale() {
        return scale;
    }

    public int getScaledWidth() {
        return scaledWidth;
    }

    public int getScaledHeight() {
        return scaledHeight;
    }
}
