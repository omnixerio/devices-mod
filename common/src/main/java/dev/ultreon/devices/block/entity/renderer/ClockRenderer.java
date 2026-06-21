package dev.ultreon.devices.block.entity.renderer;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.ultreon.devices.OmnixerioDevicesMod;
import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.block.ClockBlock;
import dev.ultreon.devices.block.entity.ClockBlockEntity;
import dev.ultreon.devices.core.TaskBar;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.awt.*;
import java.util.Objects;

import static net.minecraft.world.phys.shapes.Shapes.box;

/**
 * @author MrCrayfish
 */
public record ClockRenderer(BlockEntityRendererProvider.Context context) implements BlockEntityRenderer<ClockBlockEntity> {
    private static final Quaternionf tmpQ = new Quaternionf();
    public static final float DEG2RAD = 0.017453292519943295f;
    public static final double PIXEL_SIZE = 0.015625;

    @Override
    public void render(ClockBlockEntity blockEntity, float partialTick, @NotNull PoseStack pose, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        PaperModel paperModel = new PaperModel(Minecraft.getInstance().getEntityModels().bakeLayer(PaperModel.LAYER_LOCATION));

        BlockState state = Objects.requireNonNull(blockEntity.getLevel()).getBlockState(blockEntity.getBlockPos());
        if (state.getBlock() != blockEntity.getBlock())
            return;

        pose.pushPose();

        // region <RenderMain()>
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        pose.pushPose();
        renderDisplay(blockEntity, pose, bufferSource, state);
        pose.popPose();
        // endregion

        pose.popPose();
    }

    private static void renderPaper(@NotNull PoseStack pose, @NotNull MultiBufferSource bufferSource, int packedLight, BlockState state, PaperModel paperModel) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, PaperModel.TEXTURE);

        pose.translate(0.5, 0.5, 0.5);
        pose.mulPose(state.getValue(ClockBlock.FACING).getRotation());
        pose.mulPose(new Quaternionf().rotateX(67.5f * DEG2RAD));
        pose.translate(0, 0, 0.4);
        pose.translate(-13 * PIXEL_SIZE, -13 * PIXEL_SIZE, -1 * PIXEL_SIZE);
        pose.scale(0.3f, 0.3f, 0.3f);

        drawBuffer(pose, bufferSource, packedLight, paperModel);
    }

    private static void drawBuffer(@NotNull PoseStack pose, @NotNull MultiBufferSource bufferSource, int packedLight, PaperModel paperModel) {
        VertexConsumer buffer = bufferSource.getBuffer(paperModel.renderType(PaperModel.TEXTURE));
        paperModel.renderToBuffer(pose, buffer, 1, 1, 1);
    }

    private static void renderDisplay(ClockBlockEntity blockEntity, @NotNull PoseStack pose, @NotNull MultiBufferSource bufferSource, BlockState state) {
        RenderSystem.depthMask(false);

        // region <Prepare()>
        pose.pushPose();
        pose.translate(0.5, 0.5, 0.5);
        pose.mulPose(state.getValue(ClockBlock.FACING).getRotation());
        pose.mulPose(tmpQ.identity().rotateY(180f * DEG2RAD));
//        pose.translate(0.0675, 0.005, -0.032);
        pose.translate(0, 0.232, -0.375);
        pose.pushPose();
        pose.scale(-0.02f, -0.02f, -0.02f);
        pose.mulPose(tmpQ.identity().rotateX((90) * DEG2RAD));
        // endregion

        Level level = blockEntity.getLevel();
        long dayTime = level != null ? level.getDayTime() : 0;
        String string = TaskBar.timeToString(dayTime);
        Minecraft.getInstance().font.drawInBatch(string, -Minecraft.getInstance().font.width(string) / 2f, -Minecraft.getInstance().font.lineHeight, Color.WHITE.getRGB(), false, pose.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0x00000000, 15728880);
        pose.popPose();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.depthMask(true);
        pose.popPose();
    }

    public static class PaperModel extends Model {
        public static final ResourceLocation TEXTURE = OmnixerioDevicesMod.id("textures/block/paper.png");
        public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(OmnixerioDevicesMod.id("paper_model"), "main");
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

        public ModelPart getMain() {
            return main;
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, int k) {
            RenderSystem.setShaderTexture(0, TEXTURE);
            this.root.render(poseStack, vertexConsumer, i, j, k);
        }
    }
}
