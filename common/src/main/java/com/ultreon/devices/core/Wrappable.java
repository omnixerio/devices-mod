package com.ultreon.devices.core;

import com.ultreon.devices.api.app.Dialog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public abstract class Wrappable {
    private Window<?> window;

    /**
     * The default initialization method. Clears any components in the default
     * layout and sets it as the current layout. If you override this method and
     * are using the default layout, make sure you call it using
     * <code>super.init()</code>
     *
     * @param intent
     */
    public abstract void init(@Nullable CompoundTag intent);

    /**
     * When the games ticks. Note if you override, make sure you call this super
     * method.
     */
    public abstract void onTick();

    /**
     * The main render loop. Note if you override, make sure you call this super
     * method.
     *
     * @param graphics
     * @param laptop       laptop instance
     * @param mc           a Minecraft instance
     * @param x            the starting x position
     * @param y            the start y position
     * @param mouseX       the mouse position x
     * @param mouseY       the mouse position y
     * @param active       if the window active
     * @param partialTicks time passed since tick
     */
    public abstract void render(GuiGraphicsExtractor graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active, float partialTicks);

    /**
     * Called when a key is typed from your keyboard. Note if you override, make
     * sure you call this super method.
     *
     * @param character the typed character
     */
    public abstract void handleCharTyped(CharacterEvent event);

    /**
     * Called when a key is pressed from your keyboard. Note if you override, make
     * sure you call this super method.
     */
    public abstract void handleKeyPressed(KeyEvent event);

    /**
     * Called when a key is pressed from your keyboard. Note if you override, make
     * sure you call this super method.
     */
    public abstract void handleKeyReleased(KeyEvent event);

    /**
     * Called when you press a mouse button.
     */
    public abstract void handleMouseClick(MouseButtonEvent event);

    /**
     * Called when you drag the mouse with a button pressed down.
     */
    public abstract void handleMouseDrag(MouseButtonEvent event);

    /**
     * Called when you release the currently pressed mouse button.
     */
    public abstract void handleMouseRelease(MouseButtonEvent event);

    /**
     * Called when you scroll the wheel on your mouse.
     *
     * @param mouseX    the x position of the mouse
     * @param mouseY    the y position of the mouse
     * @param delta     the scroll delta.
     * @param direction the direction of the scroll. true is up, false is down
     */
    public abstract void handleMouseScroll(int mouseX, int mouseY, double delta, boolean direction);

    /**
     * Gets the text in the title bar.
     *
     * @return The display name
     */
    public abstract String getWindowTitle();

    /**
     * Gets the width of the content (application/dialog) including the border.
     *
     * @return the height
     */
    public abstract int getWidth();

    /**
     * Gets the height of the content (application/dialog) including the title
     * bar.
     *
     * @return the height
     */
    public abstract int getHeight();

    /**
     * Marks the content's layout for updating
     */
    public abstract void markForLayoutUpdate();

    /**
     * Gets if this content's layout is currently pending a update
     *
     * @return if pending layout update
     */
    public abstract boolean isPendingLayoutUpdate();

    /**
     * Clears the pending layout update for this content
     */
    public abstract void clearPendingLayout();

    /**
     * Updates the components of this content
     *
     * @param x the starting rendering x position (left)
     * @param y the starting rendering y position (top)
     */
    public abstract void updateComponents(int x, int y);

    /**
     * Called when this content is closed
     */
    public void onClose() {
    }

    /**
     * Gets the Window this Application is wrapped in.
     *
     * @return the window
     */
    public final Window<?> getWindow() {
        return window;
    }

    /**
     * Sets the Window instance. Used by the core.
     *
     * @param window
     */
    public final void setWindow(Window<?> window) {
        if (window == null) throw new IllegalArgumentException("You can't set a null window instance");
        this.window = window;
    }

    public final void openDialog(Dialog dialog) {
        window.openDialog(dialog);
    }

}
