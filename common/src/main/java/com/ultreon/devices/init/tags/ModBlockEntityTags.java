package com.ultreon.devices.init.tags;

import com.ultreon.devices.OmnixerioDevicesMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntityTags {
    public static final TagKey<BlockEntityType<?>> NETWORK_DEVICES = create("network_devices");

    private static TagKey<BlockEntityType<?>> create(String name) {
        return TagKey.create(Registries.BLOCK_ENTITY_TYPE, OmnixerioDevicesMod.id(name));
    }

    public static void init() {

    }
}
