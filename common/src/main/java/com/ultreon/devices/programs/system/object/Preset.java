package com.ultreon.devices.programs.system.object;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

public record Preset(ColorScheme colorScheme, ResourceLocation id) {

    public Tag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", id.toString());
        return tag;
    }

    public static Preset fromTag(CompoundTag tag) {
        ResourceLocation id = new ResourceLocation(tag.getString("id"));
        return ColorSchemePresetRegistry.getPreset(id);
    }
}
