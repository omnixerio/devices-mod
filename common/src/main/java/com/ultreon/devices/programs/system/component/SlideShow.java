package com.ultreon.devices.programs.system.component;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.devices.api.app.Component;
import com.ultreon.devices.api.app.Icons;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.api.app.component.Image;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.programs.system.object.ImageEntry;
import com.ultreon.devices.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;


/**
 * @author MrCrayfish
 */
@SuppressWarnings("unused")
public class SlideShow extends Component {
    private static final java.awt.Color OVERLAY = new java.awt.Color(0f, 0f, 0f, 0.15f);
    private static final java.awt.Color OVERLAY_HOVER = new java.awt.Color(0.35f, 0.35f, 0.35f, 0.15f);

    private final int width;
    private final int height;

    private final NonNullList<ImageEntry> IMAGES = NonNullList.create();
    private final Image image;
    private int currentImage = -1;

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
    public SlideShow(int left, int top, int width, int height) {
        super(left, top);
        this.width = width;
        this.height = height;
        this.image = new Image(left, top, width, height);
        this.image.setBorderVisible(true);
    }

    @Override
    protected void init(Layout layout) {
        image.setDrawFull(true);
        image.init(layout);
    }

    @Override
    protected void handleLoad() {
        image.handleLoad();
    }

    @Override
    protected void render(GuiGraphics graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        if (!this.visible) return;
        image.render(graphics, laptop, mc, x, y, mouseX, mouseY, windowActive, partialTicks);

        if (currentImage > 0) {
            if (GuiHelper.isMouseWithin(mouseX, mouseY, x, y, 15, height)) {
                graphics.fill(x, y, x + 15, y + height, OVERLAY_HOVER.getRGB());
            } else {
                graphics.fill(x, y, x + 15, y + height, OVERLAY.getRGB());
            }
            Icons.CHEVRON_LEFT.draw(graphics, mc, x + 2, y + (height - 10) / 2);
        }

        if (currentImage < IMAGES.size() - 1) {
            if (GuiHelper.isMouseWithin(mouseX, mouseY, x + width - 15, y, 15, height)) {
                graphics.fill(x + width - 15, y, x + width, y + height, OVERLAY_HOVER.getRGB());
            } else {
                graphics.fill(x + width - 15, y, x + width, y + height, OVERLAY.getRGB());
            }
            Icons.CHEVRON_RIGHT.draw(graphics, mc, x + 3 + width - 15, y + (height - 10) / 2);
        }
    }

    @Override
    protected void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (!this.visible || !this.enabled || mouseButton != 0) return;

        if (GuiHelper.isMouseWithin(mouseX, mouseY, xPosition, yPosition, 15, height)) {
            if (currentImage > 0) {
                this.setImage(currentImage - 1);
            }
        }

        if (GuiHelper.isMouseWithin(mouseX, mouseY, xPosition + width - 15, yPosition, 15, height)) {
            if (currentImage < IMAGES.size() - 1) {
                this.setImage(currentImage + 1);
            }
        }
    }

    public void addImage(ResourceLocation resource) {
        IMAGES.add(new ImageEntry(resource));
        if (currentImage == -1) {
            this.setImage(0);
        }
    }

    public void addImage(String url) {
        IMAGES.add(new ImageEntry(url));
        if (currentImage == -1) {
            this.setImage(0);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void setImage(int index) {
        if (IMAGES.isEmpty() && index < 0 && index >= IMAGES.size()) return;

        currentImage = index;

        ImageEntry entry = IMAGES.get(index);
        switch (entry.getType()) {
            case LOCAL -> image.setImage(entry.getResource());
            case REMOTE -> image.setImage(entry.getUrl());
        }
    }
}
