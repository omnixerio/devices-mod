package dev.ultreon.devices.api.app.listener;

import net.minecraft.client.input.CharacterEvent;

/**
 * @author MrCrayfish
 */
@SuppressWarnings("UnusedReturnValue")
public interface KeyListener {
    boolean onCharTyped(CharacterEvent c);
}
