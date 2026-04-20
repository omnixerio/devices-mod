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
     * @param event the mouse button event
     */
    void onClick(MouseButtonEvent event);
}
