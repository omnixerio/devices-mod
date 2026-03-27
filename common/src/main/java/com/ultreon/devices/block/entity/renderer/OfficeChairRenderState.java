package com.ultreon.devices.block.entity.renderer;

import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public class OfficeChairRenderState extends BlockEntityRenderState {
    public BlockModelRenderState seat;
    public BlockModelRenderState legs;
    public float rotationDeg;
}
