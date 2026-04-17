package dev.ultreon.devices.block.entity.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class PaperRenderState extends BlockEntityRenderState {
    public int[] pixels;
    public int resolution;
    public Direction direction;
}
