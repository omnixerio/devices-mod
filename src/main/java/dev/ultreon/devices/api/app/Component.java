package dev.ultreon.devices.api.app;

import dev.ultreon.devices.core.Laptop;
import dev.ultreon.devices.programs.system.object.ColorScheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.resources.Identifier;

public abstract class Component {
    /**
     * The default components textures
     */
    public static final Identifier COMPONENTS_GUI = Identifier.parse("devices:textures/gui/components.png");
    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_RIGHT = 1;
    public static final int ALIGN_CENTER = 2;
    /**
     * The raw x position of the component. This is not relative to the application.
     */
    public int xPosition;
    /**
     * The raw y position of the component.  This is not relative to the application.
     */
    public int yPosition;
    /**
     * The relative x position from the left.
     */
    public int left;
    /**
     * The relative y position from the top.
     */
    public int top;
    /**
     * Is the component enabled
     */
    protected boolean enabled = true;
    /**
     * Is the component visible
     */
    protected boolean visible = true;

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
    public Component(int left, int top) {
        super();
        this.left = left;
        this.top = top;
    }

    protected static int color(int personalColor, int systemColor) {
        return personalColor >= 0 ? personalColor : systemColor;
    }

    /**
     * Called when this component is added to a Layout. You can add
     * sub-components through this method. Use {@link Layout#addComponent(Component)}
     *
     * @param layout the layout this component is added to
     */
    protected void init(Layout layout) {
    }

    /**
     * Called when the Layout this component is bound to is set as the current layout in an
     * application.
     */
    protected void handleLoad() {
    }

    /**
     * TODO: finish docs
     */
    protected void handleUnload() {
    }

    public void handleKeyPressed(java.awt.event.KeyEvent event) {

    }

    /**
     * Called when the game ticks
     */
    protected void handleTick() {
    }

    /**
     * The main render loop. This is where you draw your component.
     *
     * @param graphics     gui graphics helper
     * @param laptop       a Laptop instance
     * @param mc           a Minecraft instance
     * @param mouseX       the current x position of the mouse
     * @param mouseY       the current y position of the mouse
     * @param windowActive if the window is active (at front)
     * @param partialTicks percentage passed in-between two ticks
     */
    protected void render(GuiGraphicsExtractor graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
    }

    /**
     * The overlay render loop. Renders over the top of the main render
     * loop.
     *
     * @param graphics     gui graphics helper
     * @param laptop       a Laptop instance
     * @param mc           a Minecraft instance
     * @param mouseX       the current x position of the mouse
     * @param mouseY       the current y position of the mouse
     * @param windowActive if the window is active (at front)
     */
    protected void renderOverlay(GuiGraphicsExtractor graphics, Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive) {
    }

    /**
     * Called when you mouse button has been pressed. You have to do
     * your own checking to test if it was within the component's
     * bounds.
     */
    protected void handleMouseClick(MouseButtonEvent event) {

    }

    /**
     * Called when a character is typed from your keyboard.
     *
     * @param codePoint the typed character
     * @param modifiers the typed character modifiers
     */
    public void handleCharTyped(CharacterEvent event) {

    }

    /**
     * Called when a key is pressed from your keyboard.
     */
    public void handleKeyPressed(KeyEvent event) {

    }

    protected void handleKeyReleased(KeyEvent event) {

    }

    /**
     * Called when you drag the mouse with a button pressed down.
     */
    protected void handleMouseDrag(MouseButtonEvent event) {

    }

    /**
     * Called when you release the currently pressed mouse button. You have to do
     * your own checking to test if it was within the component's
     * bounds.
     */
    protected void handleMouseRelease(MouseButtonEvent event) {
    }

    //TODO document this plz
    protected void handleMouseScroll(int mouseX, int mouseY, boolean direction) {
    }

    /**
     * This method should be ignored. Used for the core.
     * Will probably be removed in the future.
     */
    protected void updateComponents(int x, int y) {
        this.xPosition = x + left;
        this.yPosition = y + top;
    }

    /**
     * Sets whether this component is enabled. You should respect
     * this value if you create your own custom components.
     *
     * @param enabled if this component should be enabled or not
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Sets whether this component is visible. You should respect
     * this value if you create your own custom components.
     *
     * @param visible if this component should be visible or not
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Gets the laptop's Color scheme. A simple helper method to clean up code.
     *
     * @return the laptop's Color scheme
     */
    protected ColorScheme getColorScheme() {
        return Laptop.getSystem().getSettings().getColorScheme();
    }

    public void drawVerticalLine(GuiGraphicsExtractor graphics, int x, int y1, int y2, int rgb) {
        graphics.fill(x, y1, x + 1, y2, rgb);
    }

    public void drawHorizontalLine(GuiGraphicsExtractor graphics, int x1, int x2, int y, int rgb) {
        graphics.fill(x1, y, x2, y + 1, rgb);
    }
}
