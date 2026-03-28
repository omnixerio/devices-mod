package com.ultreon.devices.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.Tag;
import net.minecraft.util.ExtraCodecs;

public record DriveComponent(Tag tag) {
    public static final Codec<DriveComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.NBT.fieldOf("tag").forGetter(DriveComponent::tag)
    ).apply(instance, DriveComponent::new));
}
