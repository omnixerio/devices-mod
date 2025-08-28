package com.ultreon.devices.api.app;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.devices.api.app.listener.InitListener;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.core.Wrappable;
import com.ultreon.devices.util.GLHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The Layout class is the main implementation for displaying
 * components in your application. You can have multiple layouts
 * in your application to switch interfaces during runtime.
 * <p>
 * Use {@link Application#setCurrentLayout(Layout)}
 * inside of {@link Wrappable#init(CompoundTag)}
 * to set the current layout for your application.
 * <p>
 * Check out the example applications to get a better understand of
 * how this works.
 *
 * @author MrCrayfish
 */
@SuppressWarnings("unused")
public class Layout extends com.ultreon.devices.api.app.Component {
    /**
     * The list of components in the layout
     */
    public List<com.ultreon.devices.api.app.Component> components;

    /**
     * The width of the layout
     */
    public int width;

    /**
     * The height of the layout
     */
    public int height;

    private String title;
    private boolean initialized = false;

    private InitListener initListener;
    private Background background;

    /**
     * Default constructor. Initializes a layout with a width of
     * 200 and a height of 100. Use the alternate constructor to
     * set a custom width and height.
     */
    public Layout() {
        this(200, 100);
    }

    public Layout(int width, int height) {
        this(0, 0, width, height);

        if (width < 13 && false)
            throw new IllegalArgumentException("Width can not be less than 13 wide");

        if (height < 1 && false)
            throw new IllegalArgumentException("Height can not be less than 1 tall");

        this.components = new CopyOnWriteArrayList<>();
        this.width = width;
        this.height = height;
    }

    /**
     * Constructor to set a custom width and height. It should be
     * noted that the width must be in the range of 20 to 362 and
     * the height 20 to 164.
     *
     * @param width
     * @param height
     */
    public Layout(int left, int top, int width, int height) {
        super(left, top);

        if (width < 13 && false)
            throw new IllegalArgumentException("Width can not be less than 13 wide");

        if (height < 1 && false)
            throw new IllegalArgumentException("Height can not be less than 1 tall");

        this.components = new CopyOnWriteArrayList<>();
        this.width = width;
        this.height = height;
    }

    /**
     * Called on the initialization of the layout. Caused by
     * {@link Application#setCurrentLayout(Layout)}. Will
     * trigger on initialization listener if set.
     * See {@link #setInitListener(InitListener)}
     * TODO: Fix docs
     */
    public void init() {
    }

    /**
     * Adds a component to this layout and initializes it.
     *
     * @param c the component
     */
    public void addComponent(Component c) {
        if (c != null) {
            this.components.add(c);
            c.init(this);
        }
    }

    @Override
    public void init(Layout layout) {
    }

    @Override
    public void handleLoad() {
        if (!initialized) {
            this.init();
            initialized = true;
        }

        if (initListener != null) {
            initListener.onInit();
        }

        for (var c : components) {
            c.handleLoad();
        }
    }

    @Override
    protected void handleUnload() {
        for (var c : components) {
            c.handleUnload();
        }
    }

    @Override
    public void handleTick() {
        for (var c : components) {
            c.handleTick();
        }
    }

    /**
     * Renders the background of this layout if a {@link Background}
     * has be set. See {@link #setBackground(Background)}.
     *
     * @param graphics gui graphics helper
     * @param laptop a Gui instance
     * @param mc     a Minecraft instance
     * @param x      the starting x rendering position (left most)
     * @param y      the starting y rendering position (top most)
     */
    @Override
    public void render(GuiGraphics graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        if (!this.visible)
            return;

        if (background != null) {
            background.render(graphics, mc, x, y, width, height, mouseX, mouseY, windowActive);
        }

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        for (var c : new ArrayList<>(components)) {
            RenderSystem.disableDepthTest();
            GLHelper.pushScissor(x, y, width, height);
            c.render(graphics, laptop, mc, x + c.left, y + c.top, mouseX, mouseY, windowActive, partialTicks);
            GLHelper.popScissor();
        }
    }

    @Override
    public void renderOverlay(GuiGraphics graphics, Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive) {
        if (!visible)
            return;

        for (var c : components.stream().toList()) {
            c.renderOverlay(graphics, laptop, mc, mouseX, mouseY, windowActive);
        }
    }

    @Deprecated
    @Override
    public void handleKeyTyped(char character, int code) {
        if (!visible || !enabled)
            return;

        for (var c : components) {
            c.handleKeyTyped(character, code);
        }
    }

    @Deprecated
    @Override
    public void handleKeyReleased(char character, int code) {
        if (!visible || !enabled)
            return;

        for (var c : components) {
            c.handleKeyReleased(character, code);
        }
    }

    @Override
    public void handleCharTyped(char codePoint, int modifiers) {
        if (!visible || !enabled)
            return;

        for (var c : components) {
            c.handleCharTyped(codePoint, modifiers);
        }
    }

    @Override
    public void handleKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (!visible || !enabled)
            return;

        for (var c : components) {
            c.handleKeyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public void handleKeyReleased(int keyCode, int scanCode, int modifiers) {
        if (!visible || !enabled)
            return;

        for (var c : components) {
            c.handleKeyReleased(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (!visible || !enabled)
            return;

        for (var c : components) {
            c.handleMouseClick(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void handleMouseDrag(int mouseX, int mouseY, int mouseButton) {
        if (!visible || !enabled)
            return;

        for (var c : components) {
            c.handleMouseDrag(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void handleMouseRelease(int mouseX, int mouseY, int mouseButton) {
        if (!visible || !enabled)
            return;

        for (var c : components) {
            c.handleMouseRelease(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void handleMouseScroll(int mouseX, int mouseY, boolean direction) {
        if (!visible || !enabled)
            return;

        for (var c : components) {
            c.handleMouseScroll(mouseX, mouseY, direction);
        }
    }

    @Override
    public void updateComponents(int x, int y) {
        super.updateComponents(x, y);
        for (var c : components) {
            c.updateComponents(x + left, y + top);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        for (var c : components) {
            c.setEnabled(enabled);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        for (var c : components) {
            c.setVisible(visible);
        }
    }

    /**
     * Sets the initialization listener for this layout.
     * See {@link InitListener}.
     *
     * @param initListener the listener
     */
    public void setInitListener(InitListener initListener) {
        this.initListener = initListener;
    }

    /**
     * Sets the background for this layout.
     * See {@link Background}.
     *
     * @param background the background
     */
    public void setBackground(Background background) {
        this.background = background;
    }

    /**
     * Clears all components in this layout
     */
    public void clear() {
        this.components.clear();
    }

    public boolean hasTitle() {
        return title != null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    /**
     * The background interface
     *
     * @author MrCrayfish
     */
    public interface Background {
        /**
         * The render method
         *
         * @param graphics
         * @param mc     A Minecraft instance
         * @param x      the starting x rendering position (left most)
         * @param y      the starting y rendering position (top most)
         * @param width  the width of the layout
         * @param height the height of the layout
         */
        void render(GuiGraphics graphics, Minecraft mc, int x, int y, int width, int height, int mouseX, int mouseY, boolean windowActive);
    }

    public static class Context extends Layout {
        private boolean borderVisible = true;

        public Context(int width, int height) {
            super(width, height);
        }

        @Override
        public void render(GuiGraphics graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
            super.render(graphics, laptop, mc, x, y, mouseX, mouseY, windowActive, partialTicks);
            if (borderVisible) {
                drawHorizontalLine(graphics, x, x + width - 1, y, Color.DARK_GRAY.getRGB());
                drawHorizontalLine(graphics, x, x + width - 1, y + height - 1, Color.DARK_GRAY.getRGB());
                drawVerticalLine(graphics, x, y, y + height - 1, Color.DARK_GRAY.getRGB());
                drawVerticalLine(graphics, x + width - 1, y, y + height - 1, Color.DARK_GRAY.getRGB());
            }
        }

        public void setBorderVisible(boolean visible) {
            this.borderVisible = visible;
        }
    }
}
