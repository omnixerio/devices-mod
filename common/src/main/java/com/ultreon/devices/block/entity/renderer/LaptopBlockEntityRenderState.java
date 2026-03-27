package com.ultreon.devices.block.entity.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;

public class LaptopBlockEntityRenderState extends BlockEntityRenderState {
    public Direction facing;
    public boolean externalDriveAttached;
    public DyeColor externalDriveColor;
    public float screenAngle;
    public BlockState blockState;
}
