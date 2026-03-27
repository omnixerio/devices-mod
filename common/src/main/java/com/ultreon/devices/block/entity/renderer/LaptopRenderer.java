package com.ultreon.devices.block.entity.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ultreon.devices.block.ComputerBlock;
import com.ultreon.devices.block.LaptopBlock;
import com.ultreon.devices.block.entity.LaptopBlockEntity;
import com.ultreon.devices.init.DeviceItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;

public class LaptopRenderer implements BlockEntityRenderer<LaptopBlockEntity, LaptopBlockEntityRenderState> {
    private final BlockEntityRendererProvider.Context context;
    private final Minecraft mc = Minecraft.getInstance();

    public LaptopRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void submit(LaptopBlockEntityRenderState laptopBlockEntityRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {

    }

    @Override
    public LaptopBlockEntityRenderState createRenderState() {
        return new LaptopBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(LaptopBlockEntity blockEntity, LaptopBlockEntityRenderState renderState, float partialTick) {
        renderState.facing = blockEntity.getBlockState().getValue(LaptopBlock.FACING);
        renderState.externalDriveAttached = blockEntity.isExternalDriveAttached();
        renderState.externalDriveColor = blockEntity.getExternalDriveColor();
        renderState.screenAngle = blockEntity.getScreenAngle(partialTick);
        renderState.blockState = blockEntity.getBlockState();
    }

    @Override
    public void render(LaptopBlockEntityRenderState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        var direction = renderState.facing.getClockWise().toYRot();

        ItemEntity entityItem = new ItemEntity(Minecraft.getInstance().level, 0, 0, 0, ItemStack.EMPTY) {
            @Override
            public float getSpin(float partialTicks) {
                return ((float)this.getAge() + partialTicks) / 20.0f + 0;
            }
        };

        entityItem.bobOffs = 0;
        entityItem.setYRot(0);
        BlockState state = ((LaptopBlock) renderState.blockState.getBlock()).defaultBlockState().setValue(ComputerBlock.TYPE, LaptopBlock.Type.SCREEN);

        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        poseStack.pushPose();
        {
            if (renderState.externalDriveAttached) {
                poseStack.pushPose();
                {
                    poseStack.translate(0.5, 0, 0.5);
                    poseStack.mulPose(renderState.facing.getRotation());
                    poseStack.mulPose(new Quaternionf().rotateZ((float) Math.toRadians(-90)));
                    poseStack.mulPose(new Quaternionf().rotateX((float) Math.toRadians(-90)));
                    poseStack.translate(-0.5, 0, -0.5);
                    poseStack.translate(0.595, -0.2075, -0.005);
//                    poseStack.translate(0.1, 0, 0);

                    entityItem.flyDist = 0.0F;
                    assert DeviceItems.getFlashDriveByColor(renderState.externalDriveColor) != null;
                    entityItem.setItem(new ItemStack(DeviceItems.getFlashDriveByColor(renderState.externalDriveColor), 1/*, blockEntity.getExternalDriveColor().*/));
                    Minecraft.getInstance().getEntityRenderDispatcher().render(entityItem, 0, 0, 0, 0, 0, poseStack, bufferSource, packedLight);
                }
                poseStack.popPose();
            }

            poseStack.pushPose();
            {
                poseStack.translate(0.5, 0, 0.5);//west/east +90 north/south -90
                poseStack.mulPose(Axis.YP.rotationDegrees(renderState.facing == Direction.EAST || renderState.facing == Direction.WEST ? direction + 90 : direction - 90));
                poseStack.translate(-0.5, 0, -0.5);
                poseStack.translate(0, 0.0625, 0.25);
                poseStack.mulPose(Axis.XP.rotationDegrees(renderState.screenAngle + 180));
                //poseStack.mulPose(Vector3f.YP.rotationDegrees(180));
                poseStack.mulPose(Axis.XP.rotationDegrees(180));
                // Lighting.setupForFlatItems();
                //      Tesselator tessellator = Tesselator.getInstance();
                //BufferBuilder buffer = tessellator.getBuilder();
                //buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
                //    poseStack.pushPose();
                //poseStack.translate(-pos.getX(), -pos.getY(), -pos.getZ());

                BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                BakedModel ibakedmodel = mc.getBlockRenderer().getBlockModel(state);
                poseStack.pushPose();
                //poseStack.mulPose(Vector3f.ZP.rotationDegrees(180));
                //poseStack.mulPose(Vector3f.XP.rotationDegrees(180));
                //poseStack.mulPose(Vector3f.ZP.rotationDegrees(180));
                dispatcher.renderSingleBlock(state, poseStack, bufferSource, packedLight, packedOverlay);
                poseStack.popPose();
                //poseStack.popPose();
                //    tessellator.end();
                //   Lighting.setupFor3DItems();
            }
            poseStack.popPose();
        }
        poseStack.popPose();
    }
}