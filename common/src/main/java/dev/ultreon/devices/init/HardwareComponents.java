package dev.ultreon.devices.init;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

public record HardwareComponents(ItemStack cpu, ItemStack ram, ItemStack gpu, ItemStack wifi) {
    public static final Codec<HardwareComponents> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.fieldOf("cpu").forGetter(HardwareComponents::cpu),
            ItemStack.CODEC.fieldOf("ram").forGetter(HardwareComponents::ram),
            ItemStack.CODEC.fieldOf("gpu").forGetter(HardwareComponents::gpu),
            ItemStack.CODEC.fieldOf("wifi").forGetter(HardwareComponents::wifi)
    ).apply(instance, HardwareComponents::new));

}
