package dev.ultreon.devices.programs.system.object;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.resources.Identifier;

public class ColorSchemePresetRegistry {
    private static final BiMap<Identifier, Preset> PRESETS = HashBiMap.create();

    private ColorSchemePresetRegistry() {

    }

    public static Preset getPreset(Identifier id) {
        return PRESETS.get(id);
    }

    public static void register(Identifier id, Preset colorScheme) {
        PRESETS.put(id, colorScheme);
    }

    public static Identifier getKey(Preset colorScheme) {
        return PRESETS.inverse().get(colorScheme);
    }

    public static Iterable<Identifier> getKeys() {
        return PRESETS.keySet();
    }

    public static Iterable<Preset> getValues() {
        return PRESETS.values();
    }
}
