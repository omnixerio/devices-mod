package com.ultreon.devices.init.tags;

import com.ultreon.devices.OmnixerioDevicesMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class ModBlockTags {
    public static final TagKey<Block> LAPTOPS = create("laptops");
    public static final TagKey<Block> PRINTERS = create("printers");
    public static final TagKey<Block> ROUTERS = create("routers");

    private static TagKey<Block> create(String name) {
        return TagKey.create(Registries.BLOCK, OmnixerioDevicesMod.id(name));
    }

    public static void init() {

    }
}
