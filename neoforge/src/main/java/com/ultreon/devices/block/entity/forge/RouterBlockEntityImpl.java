package com.ultreon.devices.block.entity.forge;

import net.minecraft.world.phys.AABB;

import static net.minecraftforge.common.extensions.IForgeBlockEntity.INFINITE_EXTENT_AABB;

public class RouterBlockEntityImpl {
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }
}
