package dev.ultreon.devices.block.entity.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.ultreon.devices.Devices;
import net.minecraft.nbt.CompoundTag;
import dev.ultreon.devices.DeviceConfig;
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
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.awt.*;
import java.util.Objects;

/**
 * @author MrCrayfish
 */
public record PaperRenderer(
        BlockEntityRendererProvider.Context context) implements BlockEntityRenderer<PaperBlockEntity> {

    private static final int TextureIndex = 0;

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
        var vertexconsumer = bufferSource.getBuffer(RenderType.text(resourcelocation));
        vertexconsumer.addVertex(matrix4f, 0.0f, 1.0f, -0.01f).setColor(255, 255, 255, 255).setUv(0.0f, 1.0f).setLight(packedLight);
        vertexconsumer.addVertex(matrix4f, 1.0f, 1.0f, -0.01f).setColor(255, 255, 255, 255).setUv(1.0f, 1.0f).setLight(packedLight);
        vertexconsumer.addVertex(matrix4f, 1.0f, 0.0f, -0.01f).setColor(255, 255, 255, 255).setUv(1.0f, 0.0f).setLight(packedLight);
        vertexconsumer.addVertex(matrix4f, 0.0f, 0.0f, -0.01f).setColor(255, 255, 255, 255).setUv(0.0f, 0.0f).setLight(packedLight);
    }

    @Override
    public void render(PaperBlockEntity blockEntity, float partialTick, @NotNull PoseStack pose, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = Objects.requireNonNull(blockEntity.getLevel()).getBlockState(blockEntity.getBlockPos());
        if (blockEntity.getBlockState().getBlock() != state.getBlock()) {
            Devices.LOGGER.error("Paper block mismatch: {} != {}", blockEntity.getBlockState().getBlock(), state.getBlock());
            return;
        }

        pose.pushPose();
        {
            pose.translate(blockEntity.getBlockPos().getX(), blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ());
            pose.translate(0.5, 0.5, 0.5);
            pose.mulPose(state.getValue(PaperBlock.FACING).getRotation());
            pose.mulPose(new Quaternionf(0, 0, 1, -blockEntity.getRotation()));
            pose.translate(-0.5, -0.5, -0.5);

            IPrint print = blockEntity.getPrint();
            if (print != null) {
                CompoundTag data = print.toTag();
                if (data.contains("pixels", Tag.TAG_INT_ARRAY) && data.contains("resolution", Tag.TAG_INT)) {
                    RenderSystem.setShaderTexture(0, PrinterRenderer.PaperModel.TEXTURE);
                    if (DeviceConfig.RENDER_PRINTED_3D.get() && !data.getBoolean("cut")) {
                       // drawCuboid(0, 0, 0, 16, 16, 1, bufferSource);
                    }

                    pose.translate(0, 0, DeviceConfig.RENDER_PRINTED_3D.get() ? 0.0625 : 0.001);

                    pose.pushPose();
                    {
                        IPrint.Renderer renderer = PrintingManager.getRenderer(print);
                        renderer.render(pose, data);
                    }
                    pose.popPose();

                    pose.pushPose();
                    {
                        if (DeviceConfig.RENDER_PRINTED_3D.get() && data.getBoolean("cut")) {
                            CompoundTag tag = print.toTag();
                            drawPixels(pose, tag.getIntArray("pixels"), tag.getInt("resolution"), tag.getBoolean("cut"), packedLight, packedOverlay, bufferSource);
                        }
                    }
                    pose.popPose();
                }
            }
        }
        pose.popPose();
    }
}
