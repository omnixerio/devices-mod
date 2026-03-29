package dev.ultreon.devices.client.gui;

import dev.ultreon.devices.UltreonDevicesCommon;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class ClientWindow {
    public static final Identifier WINDOW_BACKGROUND = UltreonDevicesCommon.id("textures/gui/window_background.png");

    private String title;
    private int x;
    private int y;
    private int width;
    private int height;
    private boolean topMost;
    private boolean bottomMost;

    public ClientWindow(String title, int x, int y, int width, int height) {
        this.title = title;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.pose().pushMatrix();
        graphics.pose().translate(-x, -y);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, WINDOW_BACKGROUND, 0, 0, width, height);
        graphics.pose().popMatrix();
    }

    public void setTitle(String s) {
        this.title = s;
    }

    public String getTitle() {
        return title;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setX(int integer) {
        this.x = integer;
    }

    public void setY(int integer) {
        this.y = integer;
    }

    public void setWidth(int integer) {
        this.width = integer;
    }

    public void setHeight(int integer) {
        this.height = integer;
    }

    public boolean isTopMost() {
        return topMost;
    }

    public void setTopMost(boolean b) {
        this.topMost = b;
    }

    public boolean isBottomMost() {
        return bottomMost;
    }

    public void setBottomMost(boolean b) {
        this.bottomMost = b;
    }
}
