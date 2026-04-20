package dev.ultreon.devices.init;

import dev.ultreon.devices.platform.Services;
import dev.ultreon.devices.platform.services.RegistrySupplier;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Function;

/**
 * @author MrCrayfish
 */
public class DeviceSounds {
    public static final RegistrySupplier<SoundEvent> PRINTER_PRINTING = register("printer_printing", SoundEvent::createVariableRangeEvent);
    public static final RegistrySupplier<SoundEvent> PRINTER_LOADING_PAPER = register("printer_loading_paper", SoundEvent::createVariableRangeEvent);

    private static RegistrySupplier<SoundEvent> register(String name, Function<Identifier, SoundEvent> func) {
        return Services.PLATFORM.registerSound(name, func);
    }

    public static void register() {

    }
}
