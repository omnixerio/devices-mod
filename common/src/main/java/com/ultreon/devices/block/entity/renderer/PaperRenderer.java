package com.ultreon.devices.block.entity.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.ultreon.devices.DeviceConfig;
import com.ultreon.devices.Devices;
import com.ultreon.devices.api.print.IPrint;
import com.ultreon.devices.api.print.PrintingManager;
import com.ultreon.devices.block.PaperBlock;
import com.ultreon.devices.block.entity.PaperBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;
import java.util.Objects;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

/**
 * @author MrCrayfish
 */
public record PaperRenderer(
        BlockEntityRendererProvider.Context context) implements BlockEntityRenderer<PaperBlockEntity> {
    private static long TextureIndex = 0;


    @SuppressWarnings("SameParameterValue")
    private static void drawCuboid(double x, double y, double z, double width, double height, double depth, MultiBufferSource bufferSource) {
        x /= 16;
        y /= 16;
        z /= 16;
        width /= 16;
        height /= 16;
        depth /= 16;
//        RenderSystem.disableLighting();
//        GlStateManager.enableRescaleNormal();
//        pose.glNormal3f(0f, 1f, 0f);
        double v = x + width + 1 - (width + width);
        drawQuad(x + (1 - width), y, z, x + width + (1 - width), y + height, z, Direction.NORTH, bufferSource);
        drawQuad(x + 1, y, z, x + 1, y + height, z + depth, Direction.EAST, bufferSource);
        drawQuad(v, y, z + depth, v, y + height, z, Direction.WEST, bufferSource);
        drawQuad(x + (1 - width), y, z + depth, x + width + (1 - width), y, z, Direction.DOWN, bufferSource);
        drawQuad(x + (1 - width), y + height, z, x + width + (1 - width), y, z + depth, Direction.UP, bufferSource);
//        GlStateManager.disableRescaleNormal();
//        GlStateManager.enableLighting();
    }

    private static void drawQuad(double xFrom, double yFrom, double zFrom, double xTo, double yTo, double zTo, Direction direction, MultiBufferSource bufferSource) {
        double textureWidth = Math.abs(xTo - xFrom);
        double textureHeight = Math.abs(yTo - yFrom);
        double textureDepth = Math.abs(zTo - zFrom);

        BufferBuilder buffer = new BufferBuilder(256);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        switch (direction.getAxis()) {
            case X -> {
                buffer.vertex(xFrom, yFrom, zFrom).uv((float) (1 - xFrom + textureDepth), (float) (1 - yFrom + textureHeight)).endVertex();
                buffer.vertex(xFrom, yTo, zFrom).uv((float) (1 - xFrom + textureDepth), (float) (1 - yFrom)).endVertex();
                buffer.vertex(xTo, yTo, zTo).uv((float) (1 - xFrom), (float) (1 - yFrom)).endVertex();
                buffer.vertex(xTo, yFrom, zTo).uv((float) (1 - xFrom), (float) (1 - yFrom + textureHeight)).endVertex();
            }
            case Y -> {
                buffer.vertex(xFrom, yFrom, zFrom).uv((float) (1 - xFrom + textureWidth), (float) (1 - yFrom + textureDepth)).endVertex();
                buffer.vertex(xFrom, yFrom, zTo).uv((float) (1 - xFrom + textureWidth), (float) (1 - yFrom)).endVertex();
                buffer.vertex(xTo, yFrom, zTo).uv((float) (1 - xFrom), (float) (1 - yFrom)).endVertex();
                buffer.vertex(xTo, yFrom, zFrom).uv((float) (1 - xFrom), (float) (1 - yFrom + textureDepth)).endVertex();
            }
            case Z -> {
                buffer.vertex(xFrom, yFrom, zFrom).uv((float) (1 - xFrom + textureWidth), (float) (1 - yFrom + textureHeight)).endVertex();
                buffer.vertex(xFrom, yTo, zFrom).uv((float) (1 - xFrom + textureWidth), (float) (1 - yFrom)).endVertex();
                buffer.vertex(xTo, yTo, zTo).uv((float) (1 - xFrom), (float) (1 - yFrom)).endVertex();
                buffer.vertex(xTo, yFrom, zTo).uv((float) (1 - xFrom), (float) (1 - yFrom + textureHeight)).endVertex();
            }
        }
    }

    private static void drawPixels(PoseStack poseStack, int[] pixels, int resolution, boolean cut, int packedLight, int packedOverlay, MultiBufferSource bufferSource) {
        var d = new DynamicTexture(resolution, resolution, true);
        for (int i = 0; i < resolution; i++) {
            for (int j = 0; j < resolution; j++) {
                int r = (pixels[j + i * resolution] >> 16 & 255);
                int g = (pixels[j + i * resolution] >> 8 & 255);
                int b = (pixels[j + i * resolution] & 255);
                int a = (int) (double) (pixels[j + i * resolution] >> 24 & 255);

                assert d.getPixels() != null;
                d.getPixels().setPixelRGBA(i, j, new Color(r, g, b, a).getRGB());
            }
        }

        ResourceLocation resourcelocation = Minecraft.getInstance().getTextureManager().register("map/" + TextureIndex, d);
        Matrix4f matrix4f = poseStack.last().pose();
        var vertexconsumer = bufferSource.getBuffer(RenderType.entitySolid(resourcelocation));
        vertexconsumer.vertex(matrix4f, 0.0f, 128.0f, -0.01f).color(255, 255, 255, 255).uv(0.0f, 1.0f).uv2(packedLight).overlayCoords(packedOverlay).endVertex();
        vertexconsumer.vertex(matrix4f, 128.0f, 128.0f, -0.01f).color(255, 255, 255, 255).uv(1.0f, 1.0f).uv2(packedLight).overlayCoords(packedOverlay).endVertex();
        vertexconsumer.vertex(matrix4f, 128.0f, 0.0f, -0.01f).color(255, 255, 255, 255).uv(1.0f, 0.0f).uv2(packedLight).overlayCoords(packedOverlay).endVertex();
        vertexconsumer.vertex(matrix4f, 0.0f, 0.0f, -0.01f).color(255, 255, 255, 255).uv(0.0f, 0.0f).uv2(packedLight).overlayCoords(packedOverlay).endVertex();
        TextureIndex++;
    }

    @Override
    public void render(PaperBlockEntity blockEntity, float partialTick, @NotNull PoseStack pose, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = Objects.requireNonNull(blockEntity.getLevel()).getBlockState(blockEntity.getBlockPos());
        if (blockEntity.getBlockState().getBlock() != state.getBlock()) {
            Devices.LOGGER.error("Paper block mismatch: {} != {}", blockEntity.getBlockState().getBlock(), state.getBlock());
            return;
        }

        //region <RenderRoot()>
        pose.pushPose();
        {

            //region <RenderMain()>
            pose.pushPose();
            Vector3f vector3f = new Vector3f(0.5f, 0f, 0.5f);
            Quaternionf quat = (switch (state.getValue(PaperBlock.FACING)) {
                case DOWN -> new Quaternionf().rotationX((float) Math.PI);
                case UP -> new Quaternionf();
                case NORTH -> new Quaternionf().rotateXYZ(0, 0.0F, 0);
                case SOUTH -> new Quaternionf().rotateXYZ(0, (float) (Math.PI), 0);
                case WEST -> new Quaternionf().rotateXYZ(0, (float) (Math.PI / 2), 0);
                case EAST -> new Quaternionf().rotateXYZ(0, (float) -(Math.PI / 2), 0);
            });
            vector3f.set(-1, -1, -1).rotate(quat);
            pose.translate(0.5, 0.5, 0.5);
            pose.mulPose(quat);
            pose.translate(-0.5, -0.5, -0.5);

            float scale = 32768f;
            pose.scale(1 / scale, 1 / scale, 1 / scale);

            IPrint print = blockEntity.getPrint();
            if (print != null) {
                CompoundTag data = print.toTag();
                if (data.contains("pixels", Tag.TAG_INT_ARRAY) && data.contains("resolution", Tag.TAG_INT)) {
                    RenderSystem.setShaderTexture(0, PrinterRenderer.PaperModel.TEXTURE);

                    // TODO: Fix in either 0.9 or 0.10
//                    if (DeviceConfig.RENDER_PRINTED_3D.get() && !data.getBoolean("cut")) {
//                        drawCuboid(0, 0, 0, 16, 16, 1, bufferSource);
//                    }

                    pose.translate(0, 0, DeviceConfig.RENDER_PRINTED_3D.get() ? 0.0625 : 0.001);

                    //region <RenderPrint()>
                    pose.pushPose();
                    {
                        IPrint.Renderer renderer = PrintingManager.getRenderer(print);
                        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entitySolid(PrinterRenderer.PaperModel.TEXTURE));
                        renderer.render(pose, buffer, data, packedLight, NO_OVERLAY, blockEntity.getBlockState().getValue(PaperBlock.FACING));
                    }
                    pose.popPose();
                    //endregion

                    //region <RenderPrint3D()>
                    pose.pushPose();
                    {
                        if (DeviceConfig.RENDER_PRINTED_3D.get() && data.getBoolean("cut")) {
                            CompoundTag tag = print.toTag();
                            drawPixels(pose, tag.getIntArray("pixels"), tag.getInt("resolution"), tag.getBoolean("cut"), packedLight, packedOverlay, bufferSource);
                        }
                    }
                    pose.popPose();
                    //endregion
                }
            }
            pose.popPose();
            //endregion
        }
        pose.popPose();
        //endregion
    }
}
