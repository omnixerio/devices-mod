package com.ultreon.devices.programs.system.object;

import com.ultreon.devices.Devices;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class ColorSchemePresets {
    public static final Preset DEFAULT = createPreset("default", new ColorScheme(), colorScheme -> {

    });

    public static final Preset DARK = createPreset("dark", new ColorScheme(), scheme -> {
        scheme.buttonColor = 0x303030;
        scheme.backgroundColor = 0x202020;
        scheme.textColor = 0xffffff;
        scheme.backgroundSecondaryColor = 0x303030;
        scheme.textSecondaryColor = 0xa0a0a0;
        scheme.itemBackgroundColor = 0x404040;
        scheme.itemHighlightColor = 0x505050;
        scheme.headerColor = 0x303030;
        scheme.windowBackgroundColor = 0x202020;
    });

    public static final Preset LIGHT = createPreset("light", new ColorScheme(), scheme -> {
        scheme.buttonColor = 0x0080ff;
        scheme.backgroundColor = 0xd0d0d0;
        scheme.textColor = 0x000000;
        scheme.backgroundSecondaryColor = 0xa0a0a0;
        scheme.textSecondaryColor = 0x303030;
        scheme.itemBackgroundColor = 0xb0b0b0;
        scheme.itemHighlightColor = 0xc0c0c0;
        scheme.headerColor = 0xa0a0a0;
        scheme.windowBackgroundColor = 0xe0e0e0;
    });

    private static Preset createPreset(String name, ColorScheme colorScheme, Consumer<ColorScheme> consumer) {
        consumer.accept(colorScheme);
        ResourceLocation id = Devices.id(name);
        Preset preset = new Preset(colorScheme, id);
        ColorSchemePresetRegistry.register(id, preset);

        return preset;
    }

    public static void init() {
        // No-op
    }
}
