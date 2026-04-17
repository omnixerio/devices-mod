package dev.ultreon.devices.init;

import dev.ultreon.devices.OmnixerioDevicesCommon;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Function;

/**
 * @author MrCrayfish
 */
public class DeviceSounds {
    public static final SoundEvent PRINTER_PRINTING = register("printer_printing", SoundEvent::createVariableRangeEvent);
    public static final SoundEvent PRINTER_LOADING_PAPER = register("printer_loading_paper", SoundEvent::createVariableRangeEvent);

    private static SoundEvent register(String name, Function<Identifier, SoundEvent> func) {
        SoundEvent apply = func.apply(OmnixerioDevicesCommon.id(name));
        Registry.register(BuiltInRegistries.SOUND_EVENT, OmnixerioDevicesCommon.id(name), apply);
        return apply;
    }

    public static void register() {

    }
}
