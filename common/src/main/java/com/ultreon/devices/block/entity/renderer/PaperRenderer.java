package com.ultreon.devices.block.entity.renderer;

import com.mojang.blaze3d.vertex.*;
import com.ultreon.devices.block.entity.PaperBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;

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
//    private static void drawQuad(double xFrom, double yFrom, double zFrom, double xTo, double yTo, double zTo, Direction direction, MultiBufferSource bufferSource) {
//        double textureWidth = Math.abs(xTo - xFrom);
//        double textureHeight = Math.abs(yTo - yFrom);
//        double textureDepth = Math.abs(zTo - zFrom);
//        VertexConsumer buffer = bufferSource.getBuffer(RenderType.solid());
//
//    }
//
//    private static long AA = 0;
//    private static void drawPixels(PoseStack poseStack, int[] pixels, int resolution, boolean cut, int packedLight, MultiBufferSource bufferSource) {
//        double scale = 16 / (double) resolution;
//        var d = new DynamicTexture(resolution, resolution, true);
//        for (int i = 0; i < resolution; i++) {
//            for (int j = 0; j < resolution; j++) {
//
//                int r = (pixels[j + i * resolution] >> 16 & 255);
//                int g = (pixels[j + i * resolution] >> 8 & 255);
//                int b = (pixels[j + i * resolution] & 255);
//                int a = (int) Math.floor((pixels[j + i * resolution] >> 24 & 255));
//                assert d.getPixels() != null;
//                d.getPixels().setPixelRGBA(i, j, new Color(r, g, b, a).getRGB());
//            }
//        }
//        Identifier Identifier = Minecraft.getInstance().getTextureManager().register("map/" + AA, d);
//        Matrix4f matrix4f = poseStack.last().pose();
//        var vertexconsumer = bufferSource.getBuffer(RenderType.text(Identifier));
//        vertexconsumer.vertex(matrix4f, 0.0f, 128.0f, -0.01f).color(255, 255, 255, 255).uv(0.0f, 1.0f).uv2(packedLight).endVertex();
//        vertexconsumer.vertex(matrix4f, 128.0f, 128.0f, -0.01f).color(255, 255, 255, 255).uv(1.0f, 1.0f).uv2(packedLight).endVertex();
//        vertexconsumer.vertex(matrix4f, 128.0f, 0.0f, -0.01f).color(255, 255, 255, 255).uv(1.0f, 0.0f).uv2(packedLight).endVertex();
//        vertexconsumer.vertex(matrix4f, 0.0f, 0.0f, -0.01f).color(255, 255, 255, 255).uv(0.0f, 0.0f).uv2(packedLight).endVertex();
//        AA++;
//    }
//
//    @Override
//    public void render(PaperBlockEntity blockEntity, float partialTick, @NotNull PoseStack pose, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
//        BlockState state = Objects.requireNonNull(blockEntity.getLevel()).getBlockState(blockEntity.getBlockPos());
//        if (blockEntity.getBlockState().getBlock() != state.getBlock()) {
//            Devices.LOGGER.error("Paper block mismatch: {} != {}", blockEntity.getBlockState().getBlock(), state.getBlock());
//            return;
//        }
//
//        pose.pushMatrix();
//        {
//            pose.translate(blockEntity.getBlockPos().getX(), blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ());
//            pose.translate(0.5, 0.5, 0.5);
//            pose.mulPose(state.getValue(PaperBlock.FACING).getRotation());
//            pose.mulPose(new Quaternionf(0, 0, 1, -blockEntity.getRotation()));
//            pose.translate(-0.5, -0.5, -0.5);
//
//            IPrint print = blockEntity.getPrint();
//            if (print != null) {
//                CompoundTag data = print.toTag();
//                if (data.contains("pixels", Tag.TAG_INT_ARRAY) && data.contains("resolution", Tag.TAG_INT)) {
//                    RenderSystem.setShaderTexture(0, PrinterRenderer.PaperModel.TEXTURE);
//                    if (DeviceConfig.RENDER_PRINTED_3D.get() && !data.getBooleanOr("cut", false)) {
//                       // drawCuboid(0, 0, 0, 16, 16, 1, bufferSource);
//                    }
//
//                    pose.translate(0, 0, DeviceConfig.RENDER_PRINTED_3D.get() ? 0.0625 : 0.001);
//
//                    pose.pushMatrix();
//                    {
//                        IPrint.Renderer renderer = PrintingManager.getRenderer(print);
//                        renderer.render(pose, data);
//                    }
//                    pose.popMatrix();
//
//                    pose.pushMatrix();
//                    {
//                        if (DeviceConfig.RENDER_PRINTED_3D.get() && data.getBooleanOr("cut", false)) {
//                            CompoundTag tag = print.toTag();
//                            drawPixels(pose, tag.getIntArray("pixels").orElse(null), tag.getIntOr("resolution", 0), tag.getBooleanOr("cut", false), packedLight, bufferSource);
//                        }
//                    }
//                    pose.popMatrix();
//                }
//            }
//        }
//        pose.popMatrix();
//    }


    @Override
    public PaperRenderState createRenderState() {
        return new PaperRenderState();
    }

    @Override
    public void submit(PaperRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {

    }
}
