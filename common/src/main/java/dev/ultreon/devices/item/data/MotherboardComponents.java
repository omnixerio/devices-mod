package dev.ultreon.devices.item.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record MotherboardComponents(
        boolean hasCpu,
        boolean hasRam,
        boolean hasGpu,
        boolean hasWifi
) {
    public static final Codec<MotherboardComponents> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("has_cpu").forGetter(MotherboardComponents::hasCpu),
            Codec.BOOL.fieldOf("has_ram").forGetter(MotherboardComponents::hasRam),
            Codec.BOOL.fieldOf("has_gpu").forGetter(MotherboardComponents::hasGpu),
            Codec.BOOL.fieldOf("has_wifi").forGetter(MotherboardComponents::hasWifi)
    ).apply(instance, MotherboardComponents::new));
}
