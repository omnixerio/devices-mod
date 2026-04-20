package dev.ultreon.devices.block.entity.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.ultreon.devices.DeviceConfig;
import dev.ultreon.devices.OmnixerioDevicesCommon;
import dev.ultreon.devices.api.print.IPrint;
import dev.ultreon.devices.block.PaperBlock;
import dev.ultreon.devices.block.entity.PaperBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * @author MrCrayfish
 */
public record PaperRenderer(
        BlockEntityRendererProvider.Context context) implements BlockEntityRenderer<PaperBlockEntity, PaperRenderState> {

//    @SuppressWarnings("SameParameterValue")
//    private static void drawCuboid(double x, double y, double z, double width, double height, double depth, MultiBufferSource bufferSource) {
//        x /= 16;
//        y /= 16;
//        z /= 16;
//        width /= 16;
//        height /= 16;
//        depth /= 16;
////        RenderSystem.disableLighting();
////        GlStateManager.enableRescaleNormal();
////        pose.glNormal3f(0f, 1f, 0f);
//        double v = x + width + 1 - (width + width);
//        drawQuad(x + (1 - width), y, z, x + width + (1 - width), y + height, z, Direction.NORTH, bufferSource);
//        drawQuad(x + 1, y, z, x + 1, y + height, z + depth, Direction.EAST, bufferSource);
//        drawQuad(v, y, z + depth, v, y + height, z, Direction.WEST, bufferSource);
//        drawQuad(x + (1 - width), y, z + depth, x + width + (1 - width), y, z, Direction.DOWN, bufferSource);
//        drawQuad(x + (1 - width), y + height, z, x + width + (1 - width), y, z + depth, Direction.UP, bufferSource);
////        GlStateManager.disableRescaleNormal();
////        GlStateManager.enableLighting();
//    }
//
//    private static void drawQuad(double xFrom, double yFrom, double zFrom, double xTo, double yTo, double zTo) {
//        double textureWidth = Math.abs(xTo - xFrom);
//        double textureHeight = Math.abs(yTo - yFrom);
//        double textureDepth = Math.abs(zTo - zFrom);
////        VertexConsumer buffer = bufferSource.getBuffer(RenderTypes.solidMovingBlock());
//
//    }

    private static long AA = 0;

    @Override
    public @NonNull PaperRenderState createRenderState() {
        return new PaperRenderState();
    }

    @Override
    public void extractRenderState(PaperBlockEntity blockEntity, PaperRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        IPrint print = blockEntity.getPrint();
        state.pixels = print != null ? print.getPixels() : null;
        state.resolution = print != null ? print.getResolution() : 0;
        state.direction = blockEntity.getBlockState().getValue(PaperBlock.FACING);
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
    }

    @Override
    public void submit(@NonNull PaperRenderState state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState camera) {
        poseStack.pushPose();
        {
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.mulPose(state.direction.getRotation());
            poseStack.translate(-0.5, -0.5, -0.5);

            if (state.pixels != null && state.resolution > 0) {
                int[] pixels = state.pixels;
                int resolution = state.resolution;

                poseStack.translate(0, 0, DeviceConfig.RENDER_PRINTED_3D.get() ? 0.0625 : 0.001);

                poseStack.pushPose();
                {
                    double scale = 16 / (double) resolution;
                    var d = new DynamicTexture("Paper Picture " + AA, resolution, resolution, true);
                    for (int i = 0; i < resolution; i++) {
                        for (int j = 0; j < resolution; j++) {

                            int r = (pixels[j + i * resolution] >> 16 & 255);
                            int g = (pixels[j + i * resolution] >> 8 & 255);
                            int b = (pixels[j + i * resolution] & 255);
                            int a = (int) Math.ceil((pixels[j + i * resolution] >> 24) / 254.0);
                            d.getPixels().setPixelABGR(i, j, a << 24 | r << 16 | g << 8 | b);
                        }
                    }
                    Identifier id = OmnixerioDevicesCommon.id("map" + AA);
                    Minecraft.getInstance().getTextureManager().register(id, d);
                    d.upload();

                    AA++;

                    submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(id), (PoseStack.Pose pose, VertexConsumer buffer) -> {
                        extracted(state, pose, buffer);
                        AA--;
                        Minecraft.getInstance().getTextureManager().release(id);
                    });
                }
                poseStack.popPose();
            }
        }
        poseStack.popPose();
    }

    private static void extracted(@NonNull PaperRenderState state, PoseStack.Pose pose, VertexConsumer buffer) {
        buffer.addVertex(pose.pose(), 0.0f, 1.0f, -0.01f).setColor(255, 255, 255, 255).setUv(0.0f, 1.0f).setLight(state.lightCoords);
        buffer.addVertex(pose.pose(), 1.0f, 1.0f, -0.01f).setColor(255, 255, 255, 255).setUv(1.0f, 1.0f).setLight(state.lightCoords);
        buffer.addVertex(pose.pose(), 1.0f, 0.0f, -0.01f).setColor(255, 255, 255, 255).setUv(1.0f, 0.0f).setLight(state.lightCoords);
        buffer.addVertex(pose.pose(), 0.0f, 0.0f, -0.01f).setColor(255, 255, 255, 255).setUv(0.0f, 0.0f).setLight(state.lightCoords);
    }
}
