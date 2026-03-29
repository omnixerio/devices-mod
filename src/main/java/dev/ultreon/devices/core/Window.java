package dev.ultreon.devices.core;

import dev.ultreon.devices.api.app.Application;
import dev.ultreon.devices.api.app.Dialog;
import dev.ultreon.devices.gui.GuiButtonClose;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;

import org.jetbrains.annotations.Nullable;
import java.awt.*;

public class Window<T extends Wrappable> {
    public static final Identifier WINDOW_GUI = Identifier.parse("devices:textures/gui/application.png");

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

    public void render(GuiGraphicsExtractor graphics, Laptop gui, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active, float partialTicks) {
        if (content.isPendingLayoutUpdate()) {
            this.setWidth(content.getWidth());
            this.setHeight(content.getHeight());
            this.offsetX = (Laptop.getScreenWidth() - width) / 2;
            this.offsetY = (Laptop.getScreenHeight() - TaskBar.BAR_HEIGHT - height) / 2;
            updateComponents(x, y);
            content.clearPendingLayout();
        }

        graphics.pose().pushMatrix();

        Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getWindowBackgroundColor());
        /* Corners */
        graphics.blit(RenderPipelines.GUI_TEXTURED, WINDOW_GUI,x + offsetX, y + offsetY, 0, 0, 1, 1, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, WINDOW_GUI,x + offsetX + width - 13, y + offsetY, 2, 0, 13, 13, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, WINDOW_GUI,x + offsetX + width - 1, y + offsetY + height - 1, 14, 14, 1, 1, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, WINDOW_GUI,x + offsetX, y + offsetY + height - 1, 0, 14, 1, 1, 256, 256);

        /* Edges */
        graphics.blit(RenderPipelines.GUI_TEXTURED, WINDOW_GUI,x + offsetX + 1, y + offsetY, width - 14, 13, 1, 0, 1, 13, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, WINDOW_GUI,x + offsetX + width - 1, y + offsetY + 13, 1, height - 14, 14, 13, 1, 1, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, WINDOW_GUI,x + offsetX + 1, y + offsetY + height - 1, width - 2, 1, 1, 14, 13, 1, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, WINDOW_GUI,x + offsetX, y + offsetY + 13, 1, height - 14, 0, 13, 1, 1, 256, 256);

        /* Center */
        graphics.blit(RenderPipelines.GUI_TEXTURED, WINDOW_GUI, x + offsetX + 1, y + offsetY + 13, width - 2, height - 14, 1, 13, 13, 1, 256, 256);

        String windowTitle = content.getWindowTitle();
        if (mc.font.width(windowTitle) > width - 2 - 13 - 3) { // window width, border, close button, padding, padding
            windowTitle = mc.font.plainSubstrByWidth(windowTitle, width - 2 - 13 - 3);
        }
        graphics.text(mc.font, windowTitle, x + offsetX + 3, y + offsetY + 3, Color.WHITE.getRGB(), true);

        btnClose.extractRenderState(graphics, mouseX, mouseY, partialTicks);

        /* Render content */
        content.render(graphics, gui, mc, x + offsetX + 1, y + offsetY + 13, mouseX, mouseY, active && dialogWindow == null, partialTicks);

        graphics.pose().translate(0, 0);

        if (dialogWindow != null) {
            graphics.fill(x + offsetX, y + offsetY, x + offsetX + width, y + offsetY + height, COLOR_WINDOW_DARK);
            dialogWindow.render(graphics, gui, mc, x, y, mouseX, mouseY, active, partialTicks);
        }

        graphics.pose().popMatrix();
    }

    public void handleCharTyped(CharacterEvent event) {
        if (dialogWindow != null) {
            dialogWindow.handleCharTyped(event);
            return;
        }
        content.handleCharTyped(event);
    }

    public void handleKeyPressed(KeyEvent event) {
        if (dialogWindow != null) {
            dialogWindow.handleKeyPressed(event);
            return;
        }
        content.handleKeyPressed(event);
    }

    public void handleKeyReleased(KeyEvent event) {
        if (dialogWindow != null) {
            dialogWindow.handleKeyReleased(event);
            return;
        }
        content.handleKeyReleased(event);
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
    void handleMouseClick(Laptop gui, int x, int y, MouseButtonEvent event) {
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
            dialogWindow.handleMouseClick(gui, x, y, event);
            return;
        }

        content.handleMouseClick(event);
    }

    void handleMouseDrag(MouseButtonEvent event) {
        if (dialogWindow != null) {
            dialogWindow.handleMouseDrag(event);
            return;
        }
        content.handleMouseDrag(event);
    }

    void handleMouseRelease(MouseButtonEvent event) {
        if (dialogWindow != null) {
            dialogWindow.handleMouseRelease(event);
            return;
        }
        content.handleMouseRelease(event);
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
