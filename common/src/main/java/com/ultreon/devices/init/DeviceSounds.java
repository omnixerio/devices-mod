package com.ultreon.devices.init;

import com.ultreon.devices.Devices;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;

/**
 * @author MrCrayfish
 */
public class DeviceSounds {
    private static final Registrar<SoundEvent> REGISTER = Devices.REGISTRIES.get().get(Registries.SOUND_EVENT);

    public static final RegistrySupplier<SoundEvent> PRINTER_PRINTING = REGISTER.register(Devices.id("printer_printing"), () -> SoundEvent.createVariableRangeEvent(Devices.id("printer_printing")));
    public static final RegistrySupplier<SoundEvent> PRINTER_LOADING_PAPER = REGISTER.register(Devices.id("printer_loading_paper"), () -> SoundEvent.createVariableRangeEvent(Devices.id("printer_loading_paper")));

    public static void register() {

    }
}
