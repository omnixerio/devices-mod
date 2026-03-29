package dev.ultreon.devices.block.entity.renderer;

import dev.ultreon.devices.block.LaptopBlock;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;

public class LaptopBlockEntityRenderState extends BlockEntityRenderState {
    public BlockModelRenderState screenState;
    public BlockModelRenderState baseState;
    public float screenRotation;
    public ItemStackRenderState itemA;
    public ItemStack itemB;
    public BlockState laptopState;

    public boolean isExternalDriveAttached() {
        return itemA != null || itemB != null;
    }

    public Quaternionf getRotation() {
        return laptopState.getValue(LaptopBlock.FACING).getRotation();
    }
}
