package dev.ultreon.devices.api.app.listener;

import net.minecraft.client.input.MouseButtonEvent;

/**
 * The click listener interface. Used for handling clicks
 * on components.
 *
 * @author MrCrayfish
 */
public interface ClickListener {
    /**
     * Called when component is clicked
     *
     * @param mouseButton the mouse button used to click
     */
    void onClick(MouseButtonEvent event);
}
