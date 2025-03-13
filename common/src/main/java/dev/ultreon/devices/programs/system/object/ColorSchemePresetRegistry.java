package dev.ultreon.devices.programs.system.object;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.resources.ResourceLocation;

public class ColorSchemePresetRegistry {
    private static final BiMap<ResourceLocation, Preset> PRESETS = HashBiMap.create();

    private ColorSchemePresetRegistry() {

    }

    public static Preset getPreset(ResourceLocation id) {
        return PRESETS.get(id);
    }

    public static void register(ResourceLocation id, Preset colorScheme) {
        PRESETS.put(id, colorScheme);
    }

    public static ResourceLocation getKey(Preset colorScheme) {
        return PRESETS.inverse().get(colorScheme);
    }

    public static Iterable<ResourceLocation> getKeys() {
        return PRESETS.keySet();
    }

    public static Iterable<Preset> getValues() {
        return PRESETS.values();
    }
}
