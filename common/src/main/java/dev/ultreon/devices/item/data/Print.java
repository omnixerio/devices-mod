package dev.ultreon.devices.item.data;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;

public record Print(CompoundTag printTag) {
    public static final Codec<Print> CODEC = CompoundTag.CODEC.xmap(Print::new, Print::printTag);
}
