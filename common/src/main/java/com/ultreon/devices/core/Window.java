package com.ultreon.devices.core;

import com.mojang.blaze3d.systems.RenderSystem;
import com.ultreon.devices.api.app.Application;
import com.ultreon.devices.api.app.Dialog;
import com.ultreon.devices.gui.GuiButtonClose;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.Nullable;
import java.awt.*;

public class Window<T extends Wrappable> {
    public static final ResourceLocation WINDOW_GUI = new ResourceLocation("devices:textures/gui/application.png");

    public static final int COLOR_WINDOW_DARK = new Color(0f, 0f, 0f, 0.25f).getRGB();
    final Laptop laptop;
    double dragFromX;
    double dragFromY;
    protected GuiButtonClose btnClose;
    T content;
    int width, height;
    int offsetX, offsetY;
    Window<Dialog> dialogWindow = null;
    Window<? extends Wrappable> parent = null;
    protected boolean removed;

    public Window(T wrappable, Laptop laptop) {
        this.content = wrappable;
        this.laptop = laptop;
        wrappable.setWindow(this);
    }

    void setWidth(int width) {
        this.width = width + 2;
        if (this.width > Laptop.getScreenWidth()) {
            this.width = Laptop.getScreenWidth();
        }
    }

    void setHeight(int height) {
        this.height = height + 14;
        if (this.height > 178) {
            this.height = 178;
        }
    }

    void init(int x, int y, @Nullable CompoundTag intent) {
        try {
            btnClose = new GuiButtonClose(x + offsetX + width - 12, y + offsetY + 1);
            content.init(intent);
        } catch (Exception e) {
            e.printStackTrace();

            Window.this.close();
            Dialog.Message message = new Dialog.Message("Error initializing window:\n" + e.getMessage()) {
                @Override
                public void onClose() {
                    super.onClose();
                }
            };

            closeDialog();
            openDialog(message);
        }
    }

    public void onTick() {
        if (dialogWindow != null) {
            dialogWindow.onTick();
        }
        content.onTick();
    }

    public void render(GuiGraphics graphics, Laptop gui, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active, float partialTicks) {
        if (content.isPendingLayoutUpdate()) {
            this.setWidth(content.getWidth());
            this.setHeight(content.getHeight());
            this.offsetX = (Laptop.getScreenWidth() - width) / 2;
            this.offsetY = (Laptop.getScreenHeight() - TaskBar.BAR_HEIGHT - height) / 2;
            updateComponents(x, y);
            content.clearPendingLayout();
        }

        graphics.pose().pushPose();

        Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getWindowBackgroundColor());
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, WINDOW_GUI);
        RenderSystem.setShaderColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1);

        /* Corners */
        graphics.blit(WINDOW_GUI,x + offsetX, y + offsetY, 0, 0, 1, 1);
        graphics.blit(WINDOW_GUI,x + offsetX + width - 13, y + offsetY, 2, 0, 13, 13);
        graphics.blit(WINDOW_GUI,x + offsetX + width - 1, y + offsetY + height - 1, 14, 14, 1, 1);
        graphics.blit(WINDOW_GUI,x + offsetX, y + offsetY + height - 1, 0, 14, 1, 1);

        /* Edges */
        graphics.blit(WINDOW_GUI,x + offsetX + 1, y + offsetY, width - 14, 13, 1, 0, 1, 13, 256, 256);
        graphics.blit(WINDOW_GUI,x + offsetX + width - 1, y + offsetY + 13, 1, height - 14, 14, 13, 1, 1, 256, 256);
        graphics.blit(WINDOW_GUI,x + offsetX + 1, y + offsetY + height - 1, width - 2, 1, 1, 14, 13, 1, 256, 256);
        graphics.blit(WINDOW_GUI,x + offsetX, y + offsetY + 13, 1, height - 14, 0, 13, 1, 1, 256, 256);

        /* Center */
        graphics.blit(WINDOW_GUI, x + offsetX + 1, y + offsetY + 13, width - 2, height - 14, 1, 13, 13, 1, 256, 256);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        String windowTitle = content.getWindowTitle();
        if (mc.font.width(windowTitle) > width - 2 - 13 - 3) { // window width, border, close button, padding, padding
            windowTitle = mc.font.plainSubstrByWidth(windowTitle, width - 2 - 13 - 3);
        }
        graphics.drawString(mc.font, windowTitle, x + offsetX + 3, y + offsetY + 3, Color.WHITE.getRGB(), true);

        btnClose.renderWidget(graphics, mouseX, mouseY, partialTicks);

        RenderSystem.disableBlend();

        /* Render content */
        content.render(graphics, gui, mc, x + offsetX + 1, y + offsetY + 13, mouseX, mouseY, active && dialogWindow == null, partialTicks);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        graphics.pose().translate(0, 0, 200);

        if (dialogWindow != null) {
            graphics.fill(x + offsetX, y + offsetY, x + offsetX + width, y + offsetY + height, COLOR_WINDOW_DARK);
            dialogWindow.render(graphics, gui, mc, x, y, mouseX, mouseY, active, partialTicks);
        }

        graphics.pose().popPose();
    }

    @Deprecated
    public void handleKeyTyped(char character, int code) {
        if (dialogWindow != null) {
            dialogWindow.handleKeyTyped(character, code);
            return;
        }
        content.handleKeyTyped(character, code);
    }

    @Deprecated
    public void handleKeyReleased(char character, int code) {
        if (dialogWindow != null) {
            dialogWindow.handleKeyReleased(character, code);
            return;
        }
        content.handleKeyReleased(character, code);
    }

    public void handleCharTyped(char codePoint, int modifiers) {
        if (dialogWindow != null) {
            dialogWindow.handleCharTyped(codePoint, modifiers);
            return;
        }
        content.handleCharTyped(codePoint, modifiers);
    }

    public void handleKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (dialogWindow != null) {
            dialogWindow.handleKeyPressed(keyCode, scanCode, modifiers);
            return;
        }
        content.handleKeyPressed(keyCode, scanCode, modifiers);
    }

    public void handleKeyReleased(int keyCode, int scanCode, int modifiers) {
        if (dialogWindow != null) {
            dialogWindow.handleKeyReleased(keyCode, scanCode, modifiers);
            return;
        }
        content.handleKeyReleased(keyCode, scanCode, modifiers);
    }

    public void handleWindowMove(int screenStartX, int screenStartY, int newX, int newY) {
        if (newX >= 0 && newX <= Laptop.getScreenWidth() - width) {
            this.offsetX = newX;
        } else if (newX < 0) {
            this.offsetX = 0;
        } else {
            this.offsetX = Laptop.getScreenWidth() - width;
        }

        if (newY >= 0 && newY <= Laptop.getScreenHeight() - TaskBar.BAR_HEIGHT - height) {
            this.offsetY = newY;
        } else if (newY < 0) {
            this.offsetY = 0;
        } else {
            this.offsetY = Laptop.getScreenHeight() - TaskBar.BAR_HEIGHT - height;
        }

        updateComponents(screenStartX, screenStartY);
    }

    @SuppressWarnings("unused")
    void handleMouseClick(Laptop gui, int x, int y, int mouseX, int mouseY, int mouseButton) {
        if (btnClose.isHovered()) {
            if (content instanceof Application) {
                gui.closeApplication(((Application) content).getInfo());
                return;
            }

            if (parent != null) {
                parent.closeDialog();
            }
        }

        if (dialogWindow != null) {
            dialogWindow.handleMouseClick(gui, x, y, mouseX, mouseY, mouseButton);
            return;
        }

        content.handleMouseClick(mouseX, mouseY, mouseButton);
    }

    void handleMouseDrag(int mouseX, int mouseY, int mouseButton) {
        if (dialogWindow != null) {
            dialogWindow.handleMouseDrag(mouseX, mouseY, mouseButton);
            return;
        }
        content.handleMouseDrag(mouseX, mouseY, mouseButton);
    }

    void handleMouseRelease(int mouseX, int mouseY, int mouseButton) {
        if (dialogWindow != null) {
            dialogWindow.handleMouseRelease(mouseX, mouseY, mouseButton);
            return;
        }
        content.handleMouseRelease(mouseX, mouseY, mouseButton);
    }

    void handleMouseScroll(int mouseX, int mouseY, double delta, boolean direction) {
        if (dialogWindow != null) {
            dialogWindow.handleMouseScroll(mouseX, mouseY, delta, direction);
            return;
        }
        content.handleMouseScroll(mouseX, mouseY, delta, direction);
    }

    public void handleClose() {
        content.onClose();
    }

    private void updateComponents(int x, int y) {
        content.updateComponents(x + offsetX + 1, y + offsetY + 13);
        btnClose.setX(x + offsetX + width - 12);
        btnClose.setY(y + offsetY + 1);
    }

    public void openDialog(Dialog dialog) {
        if (dialogWindow != null) {
            dialogWindow.openDialog(dialog);
        } else {
            dialogWindow = new Window<>(dialog, null);
            dialogWindow.init(0, 0, null);
            dialogWindow.setParent(this);
        }
    }

    public void closeDialog() {
        if (dialogWindow != null) {
            dialogWindow.handleClose();
            dialogWindow = null;
        }
    }

    public Window<Dialog> getDialogWindow() {
        return dialogWindow;
    }

    public final void close() {
        this.removed = true;
        if (content instanceof Application) {
            laptop.closeApplication(((Application) content).getInfo());
            return;
        }
        if (parent != null) {
            parent.closeDialog();
        }
    }

    public Window<?> getParent() {
        return parent;
    }

    public void setParent(Window<?> parent) {
        this.parent = parent;
    }

    public T getContent() {
        return content;
    }
}
