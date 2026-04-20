package dev.ultreon.devices.programs.system.object;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;

public record Preset(ColorScheme colorScheme, Identifier id) {

    public Tag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", id.toString());
        return tag;
    }

    public static Preset fromTag(CompoundTag tag) {
        Identifier id = Identifier.tryParse(tag.getStringOr("id", "minecraft:"));
        return ColorSchemePresetRegistry.getPreset(id);
    }
}
