package dev.ultreon.devices.init;

import dev.ultreon.devices.UltreonDevices;
import dev.ultreon.mods.xinexlib.registrar.Registrar;
import dev.ultreon.mods.xinexlib.registrar.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;

/// @author MrCrayfish
public class DeviceSounds {
    private static final Registrar<SoundEvent> REGISTER = UltreonDevices.REGISTRIES.get().getRegistrar(Registries.SOUND_EVENT);

    public static final RegistrySupplier<SoundEvent, SoundEvent> PRINTER_PRINTING = REGISTER.register("printer_printing", () -> SoundEvent.createVariableRangeEvent(UltreonDevices.res("printer_printing")));
    public static final RegistrySupplier<SoundEvent, SoundEvent> PRINTER_LOADING_PAPER = REGISTER.register("printer_loading_paper", () -> SoundEvent.createVariableRangeEvent(UltreonDevices.res("printer_loading_paper")));

    public static void register() {
        REGISTER.load();
    }
}
