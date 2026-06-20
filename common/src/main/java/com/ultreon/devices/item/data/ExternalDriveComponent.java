package com.ultreon.devices.item.data;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;

public record ExternalDriveComponent(
        CompoundTag drive
) {
    public static final Codec<ExternalDriveComponent> CODEC = CompoundTag.CODEC.xmap(ExternalDriveComponent::new, ExternalDriveComponent::drive);
}
