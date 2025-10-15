package com.ultreon.devices.block.entity.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ultreon.devices.Devices;
import com.ultreon.devices.Reference;
import com.ultreon.devices.api.print.IPrint;
import com.ultreon.devices.api.print.PrintingManager;
import com.ultreon.devices.block.PrinterBlock;
import com.ultreon.devices.block.entity.PrinterBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.awt.*;
import java.util.Objects;

/**
 * @author MrCrayfish
 */
public record PrinterRenderer(BlockEntityRendererProvider.Context context) implements BlockEntityRenderer<PrinterBlockEntity> {
    private static final Quaternionf tmpQ = new Quaternionf();
    public static final float DEG2RAD = 0.017453292519943295f;
    public static final double PIXEL_SIZE = 0.015625;

    @Override
    public void render(PrinterBlockEntity blockEntity, float partialTick, @NotNull PoseStack pose, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        PaperModel paperModel = new PaperModel(Minecraft.getInstance().getEntityModels().bakeLayer(PaperModel.LAYER_LOCATION));

        BlockState state = Objects.requireNonNull(blockEntity.getLevel()).getBlockState(blockEntity.getBlockPos());
        if (state.getBlock() != blockEntity.getBlock()) return;

        pose.pushPose();

        // region <RenderMain()>
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        if (blockEntity.hasPaper()) {
            pose.pushPose();
            renderPaper(pose, bufferSource, packedLight, state, paperModel);
            pose.popPose();
        }

        pose.pushPose();
        renderPrint(blockEntity, pose, state, bufferSource, packedLight, packedOverlay, paperModel);
        pose.popPose();

        pose.pushPose();
        renderDisplay(blockEntity, pose, bufferSource, state);
        pose.popPose();
        // endregion

        pose.popPose();
    }

    private static void renderPaper(@NotNull PoseStack pose, @NotNull MultiBufferSource bufferSource, int packedLight, BlockState state, PaperModel paperModel) {
        pose.translate(0.5, 0.5, 0.5);
        pose.mulPose(state.getValue(PrinterBlock.FACING).getRotation());
        pose.mulPose(new Quaternionf().rotateX(67.5f * DEG2RAD));
        pose.translate(0, 0, 0.4);
        pose.translate(-13 * PIXEL_SIZE, -13 * PIXEL_SIZE, -1 * PIXEL_SIZE);
        pose.scale(0.3f, 0.3f, 0.3f);

        drawBuffer(pose, bufferSource, packedLight, paperModel);
    }

    private static void renderPrint(PrinterBlockEntity blockEntity, @NotNull PoseStack pose, BlockState state, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay, PaperModel paperModel) {
        if (blockEntity.isLoading()) {
            pose.translate(0.5, 0.5, 0.5);
            pose.mulPose(state.getValue(PrinterBlock.FACING).getRotation());
            pose.mulPose(tmpQ.identity().rotateX(67.5f * DEG2RAD));

            double progress = Math.max(-0.4, -0.4 + (0.4 * ((double) (blockEntity.getRemainingPrintTime() - 10) / 20)));
            pose.translate(0, -progress, 0.4);
            pose.translate(-13 * PIXEL_SIZE, -13 * PIXEL_SIZE, -1 * PIXEL_SIZE);
            pose.scale(0.3f, 0.3f, 0.3f);

            drawBuffer(pose, bufferSource, packedLight, paperModel);
        } else if (blockEntity.isPrinting()) {
            pose.translate(0.5, 0.078125, 0.5);
            pose.mulPose(state.getValue(PrinterBlock.FACING).getRotation());

            double progress = -0.35 + (((double) (blockEntity.getRemainingPrintTime() - 20) / blockEntity.getTotalPrintTime()));
            pose.translate(0, -progress, 0);
            pose.translate(-13 * PIXEL_SIZE, -13 * PIXEL_SIZE, -0.5 * PIXEL_SIZE);
            pose.scale(0.3f, 0.3f, 0.3f);

            drawBuffer(pose, bufferSource, packedLight, paperModel);

            pose.translate(0.4, 0.085, -0.001);
            pose.mulPose(tmpQ.identity().rotateY(180f * DEG2RAD));

            //region <RenderPrint()>
            IPrint print = blockEntity.getPrint();
            if (print != null) {
                pose.pushPose();
                pose.translate(-15 * 0.0625, 15 * 0.03125, 0);
                pose.scale(1 / 16384f, 1 / 16384f, 1 / 16384f);
                pose.scale(1 / 1.5f, 1 / 1.5f, 1 / 1.5f);

                IPrint.Renderer renderer = PrintingManager.getRenderer(print);
                VertexConsumer buffer = bufferSource.getBuffer(paperModel.renderType(PaperModel.TEXTURE));
                renderer.render(pose, buffer, print.toTag(), packedLight, packedOverlay, blockEntity.getBlockState().getValue(PrinterBlock.FACING));
                pose.popPose();
            }
            //endregion
        }
    }

    private static void drawBuffer(@NotNull PoseStack pose, @NotNull MultiBufferSource bufferSource, int packedLight, PaperModel paperModel) {
        VertexConsumer buffer = bufferSource.getBuffer(paperModel.renderType(PaperModel.TEXTURE));
        paperModel.renderToBuffer(pose, buffer, packedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    private static void renderDisplay(PrinterBlockEntity blockEntity, @NotNull PoseStack pose, @NotNull MultiBufferSource bufferSource, BlockState state) {
        RenderSystem.depthMask(false);

        // region <Prepare()>
        pose.translate(0.5, 0.5, 0.5);
        pose.mulPose(state.getValue(PrinterBlock.FACING).getRotation());
        pose.mulPose(tmpQ.identity().rotateY(180f * DEG2RAD));
        pose.translate(0.0675, 0.005, -0.032);
        pose.translate((8 -5.85) * 0.0625, (8 -5) * 0.0625, (-4.25) * 0.0625);
        pose.pushPose();
        pose.scale(-0.010416667f, -0.010416667f, -0.010416667f);
        pose.mulPose(tmpQ.identity().rotateX((90 + 22.5f) * DEG2RAD));
        // endregion

        Minecraft.getInstance().font.drawInBatch(Integer.toString(blockEntity.getPaperCount()), -Minecraft.getInstance().font.width(Integer.toString(blockEntity.getPaperCount())), -Minecraft.getInstance().font.lineHeight, Color.WHITE.getRGB(), false, pose.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0x00000000, 15728880);
        pose.popPose();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.depthMask(true);
    }

    public static class PaperModel extends Model {
        public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/model/paper.png");
        public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Devices.id("paper_model"), "main");
        private final ModelPart root;
        private final ModelPart main;

        public PaperModel(ModelPart pRoot) {
            super(RenderType::entitySolid);
            this.root = pRoot;
            this.main = pRoot.getChild("main");
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();
            partdefinition.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0, 22, 30, 1), PartPose.offset(0f, 0f, 0f));
            return LayerDefinition.create(meshdefinition, 64, 32);
        }

        @Override
        public void renderToBuffer(@NotNull PoseStack pPoseStack, @NotNull VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
            this.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        }

        private void render(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
            RenderSystem.setShaderTexture(0, TEXTURE);
            this.root.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        }

        public ModelPart getMain() {
            return main;
        }
    }
}
