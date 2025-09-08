package dev.ultreon.devices.programs.system.object;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public record Preset(ColorScheme colorScheme, ResourceLocation id) {

    public Tag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", id.toString());
        return tag;
    }

    public static @Nullable Preset fromTag(CompoundTag tag) {
        ResourceLocation id = ResourceLocation.tryParse(tag.getString("id"));
        if (id == null) return null;
        return ColorSchemePresetRegistry.getPreset(id);
    }
}
