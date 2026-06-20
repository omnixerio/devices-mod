package com.ultreon.devices.item.data;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;

public record FlashDriveComponent(
        CompoundTag driveTag
) {

    public static final Codec<FlashDriveComponent> CODEC = CompoundTag.CODEC.xmap(FlashDriveComponent::new, FlashDriveComponent::driveTag);
}
