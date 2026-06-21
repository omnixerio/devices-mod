package dev.ultreon.devices.block.entity.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.ultreon.devices.DeviceConfig;
import dev.ultreon.devices.OmnixerioDevicesMod;
import dev.ultreon.devices.api.print.IPrint;
import dev.ultreon.devices.api.print.PrintingManager;
import dev.ultreon.devices.block.PaperBlock;
import dev.ultreon.devices.block.entity.PaperBlockEntity;
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
    private static void drawCuboid(double x, double y, double z, double width, double height, double depth, MultiBufferSource bufferSource, int light) {
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
        drawQuad(x + (1 - width), y, z, x + width + (1 - width), y + height, z, Direction.NORTH, bufferSource, light);
        drawQuad(x + 1, y, z, x + 1, y + height, z + depth, Direction.EAST, bufferSource, light);
        drawQuad(v, y, z + depth, v, y + height, z, Direction.WEST, bufferSource, light);
        drawQuad(x + (1 - width), y, z + depth, x + width + (1 - width), y, z, Direction.DOWN, bufferSource, light);
        drawQuad(x + (1 - width), y + height, z, x + width + (1 - width), y, z + depth, Direction.UP, bufferSource, light);
//        GlStateManager.disableRescaleNormal();
//        GlStateManager.enableLighting();
    }

    private static void drawQuad(double xFrom, double yFrom, double zFrom, double xTo, double yTo, double zTo, Direction direction, MultiBufferSource bufferSource, int light) {
        double textureWidth = Math.abs(xTo - xFrom);
        double textureHeight = Math.abs(yTo - yFrom);
        double textureDepth = Math.abs(zTo - zFrom);

        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        switch (direction.getAxis()) {
            case X -> {
                buffer.addVertex((float) xFrom, (float) yFrom, (float) zFrom).setLight(light).setUv((float) (1 - xFrom + textureDepth), (float) (1 - yFrom + textureHeight));
                buffer.addVertex((float) xFrom, (float) yTo, (float) zFrom).setLight(light).setUv((float) (1 - xFrom + textureDepth), (float) (1 - yFrom));
                buffer.addVertex((float) xTo, (float) yTo, (float) zTo).setLight(light).setUv((float) (1 - xFrom), (float) (1 - yFrom));
                buffer.addVertex((float) xTo, (float) yFrom, (float) zTo).setLight(light).setUv((float) (1 - xFrom), (float) (1 - yFrom + textureHeight));
            }
            case Y -> {
                buffer.addVertex((float) xFrom, (float) yFrom, (float) zFrom).setLight(light).setUv((float) (1 - xFrom + textureWidth), (float) (1 - yFrom + textureDepth));
                buffer.addVertex((float) xFrom, (float) yFrom, (float) zTo).setLight(light).setUv((float) (1 - xFrom + textureWidth), (float) (1 - yFrom));
                buffer.addVertex((float) xTo, (float) yFrom, (float) zTo).setLight(light).setUv((float) (1 - xFrom), (float) (1 - yFrom));
                buffer.addVertex((float) xTo, (float) yFrom, (float) zFrom).setLight(light).setUv((float) (1 - xFrom), (float) (1 - yFrom + textureDepth));
            }
            case Z -> {
                buffer.addVertex((float) xFrom, (float) yFrom, (float) zFrom).setLight(light).setUv((float) (1 - xFrom + textureWidth), (float) (1 - yFrom + textureHeight));
                buffer.addVertex((float) xFrom, (float) yTo, (float) zFrom).setLight(light).setUv((float) (1 - xFrom + textureWidth), (float) (1 - yFrom));
                buffer.addVertex((float) xTo, (float) yTo, (float) zTo).setLight(light).setUv((float) (1 - xFrom), (float) (1 - yFrom));
                buffer.addVertex((float) xTo, (float) yFrom, (float) zTo).setLight(light).setUv((float) (1 - xFrom), (float) (1 - yFrom + textureHeight));
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
        var addVertexconsumer = bufferSource.getBuffer(RenderType.entitySolid(resourcelocation));
        addVertexconsumer.addVertex(matrix4f, 0.0f, 128.0f, -0.01f).setLight(packedLight).setOverlay(packedOverlay).setColor(255, 255, 255, 255).setUv(0.0f, 1.0f).setLight(packedLight).setOverlay(packedOverlay);
        addVertexconsumer.addVertex(matrix4f, 128.0f, 128.0f, -0.01f).setLight(packedLight).setOverlay(packedOverlay).setColor(255, 255, 255, 255).setUv(1.0f, 1.0f).setLight(packedLight).setOverlay(packedOverlay);
        addVertexconsumer.addVertex(matrix4f, 128.0f, 0.0f, -0.01f).setLight(packedLight).setOverlay(packedOverlay).setColor(255, 255, 255, 255).setUv(1.0f, 0.0f).setLight(packedLight).setOverlay(packedOverlay);
        addVertexconsumer.addVertex(matrix4f, 0.0f, 0.0f, -0.01f).setLight(packedLight).setOverlay(packedOverlay).setColor(255, 255, 255, 255).setUv(0.0f, 0.0f).setLight(packedLight).setOverlay(packedOverlay);
        TextureIndex++;
    }

    @Override
    public void render(PaperBlockEntity blockEntity, float partialTick, @NotNull PoseStack pose, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = Objects.requireNonNull(blockEntity.getLevel()).getBlockState(blockEntity.getBlockPos());
        if (blockEntity.getBlockState().getBlock() != state.getBlock()) {
            OmnixerioDevicesMod.LOGGER.error("Paper block mismatch: {} != {}", blockEntity.getBlockState().getBlock(), state.getBlock());
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
                        renderer.render(pose, data, packedLight, NO_OVERLAY, blockEntity.getBlockState().getValue(PaperBlock.FACING));
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
