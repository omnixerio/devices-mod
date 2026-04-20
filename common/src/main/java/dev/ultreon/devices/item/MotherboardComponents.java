package dev.ultreon.devices.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record MotherboardComponents(
        boolean wifi,
        boolean cpu,
        boolean gpu,
        boolean ram
) {
    public static final Codec<MotherboardComponents> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("wifi").forGetter(MotherboardComponents::wifi),
            Codec.BOOL.fieldOf("cpu").forGetter(MotherboardComponents::cpu),
            Codec.BOOL.fieldOf("gpu").forGetter(MotherboardComponents::gpu),
            Codec.BOOL.fieldOf("ram").forGetter(MotherboardComponents::ram)
    ).apply(instance, MotherboardComponents::new));
}
