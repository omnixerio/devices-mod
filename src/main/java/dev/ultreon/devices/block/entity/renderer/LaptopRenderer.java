package dev.ultreon.devices.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.ultreon.devices.block.ComputerBlock;
import dev.ultreon.devices.block.LaptopBlock;
import dev.ultreon.devices.block.entity.LaptopBlockEntity;
import dev.ultreon.devices.init.DeviceItems;
import dev.ultreon.devices.item.FlashDriveItem;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class LaptopRenderer implements BlockEntityRenderer<LaptopBlockEntity, LaptopBlockEntityRenderState> {
    private final ItemModelResolver itemModelResolver;
    private final BlockModelResolver blockModelResolver;

    public LaptopRenderer(BlockEntityRendererProvider.Context context) {
        itemModelResolver = context.itemModelResolver();
        blockModelResolver = context.blockModelResolver();
    }

    @Override
    public @NonNull LaptopBlockEntityRenderState createRenderState() {
        return new LaptopBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(@NonNull LaptopBlockEntity blockEntity, @NonNull LaptopBlockEntityRenderState state, float partialTicks, @NonNull Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        state.screenRotation = blockEntity.getScreenAngle(partialTicks);
        state.laptopState = blockEntity.getBlockState();
        if (blockEntity.isExternalDriveAttached()) {
            FlashDriveItem flashDriveByColor = DeviceItems.getFlashDriveByColor(blockEntity.getExternalDriveColor());
            if (flashDriveByColor == null) {
                state.itemA = null;
            } else {
                int seed = (int) blockEntity.getBlockPos().asLong();
                ItemStackRenderState itemState = new ItemStackRenderState();
                itemModelResolver.updateForTopItem(itemState, new ItemStack(flashDriveByColor, 1), ItemDisplayContext.FIXED, blockEntity.getLevel(), null, seed);
                state.itemA = itemState;
            }
        } else {
            state.itemA = null;
        }
        state.itemB = null;
        BlockModelRenderState screenState = new BlockModelRenderState();
        BlockModelRenderState baseState = new BlockModelRenderState();
        blockModelResolver.update(screenState, state.laptopState.setValue(LaptopBlock.TYPE, ComputerBlock.Type.SCREEN), BlockDisplayContext.create());
        blockModelResolver.update(screenState, state.laptopState.setValue(LaptopBlock.TYPE, ComputerBlock.Type.BASE), BlockDisplayContext.create());
        state.screenState = screenState;
        state.baseState = baseState;
    }

    @Override
    public void submit(LaptopBlockEntityRenderState state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState camera) {

        if (state.isExternalDriveAttached()) {
            poseStack.pushPose();
            {
                poseStack.translate(0.5, 0, 0.5);
                poseStack.mulPose(state.getRotation());
                poseStack.mulPose(new Quaternionf().rotateZ((float) Math.toRadians(-90)));
                poseStack.mulPose(new Quaternionf().rotateX((float) Math.toRadians(-90)));
                poseStack.translate(-0.5, 0, -0.5);
                poseStack.translate(0.595, -0.2075, -0.005);
                state.itemA.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            }
            poseStack.popPose();
        }

        state.baseState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

        poseStack.pushPose();
        {
            poseStack.translate(0.5, 0, 0.5);//west/east +90 north/south -90
            poseStack.mulPose(Axis.YP.rotationDegrees(state.laptopState.getValue(LaptopBlock.FACING) == Direction.EAST || state.laptopState.getValue(LaptopBlock.FACING) == Direction.WEST ? 90 : -90));
            poseStack.translate(-0.5, 0, -0.5);
            poseStack.translate(0, 0.0625, 0.25);
            poseStack.mulPose(Axis.XP.rotationDegrees(state.screenRotation + 180));
            poseStack.mulPose(Axis.XP.rotationDegrees(180));
            poseStack.pushPose();
            state.screenState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

}