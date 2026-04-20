package dev.ultreon.devices.platform.services;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface BlockEntitySupplier<T extends BlockEntity> {
    T create(BlockPos var1, BlockState var2);
}