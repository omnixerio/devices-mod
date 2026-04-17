package dev.ultreon.devices.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ultreon.devices.block.entity.PrinterBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.jspecify.annotations.NonNull;

/**
 * @author MrCrayfish
 */
public record PrinterRenderer(
        BlockEntityRendererProvider.Context context) implements BlockEntityRenderer<PrinterBlockEntity, PrinterRenderState> {
//    @Override
//    public void render(PrinterBlockEntity blockEntity, float partialTick, @NotNull PoseStack pose, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
// //       PaperModel paperModel = new PaperModel(Minecraft.getInstance().getEntityModels().bakeLayer(PaperModel.LAYER_LOCATION));
//
//       // Tesselator tesselator = Tesselator.getInstance();
//        BlockState state = Objects.requireNonNull(blockEntity.getLevel()).getBlockState(blockEntity.getBlockPos());
//        if (state.getBlock() != blockEntity.getBlock()) return;
//
//        pose.pushMatrix();
//        {
//            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
//       //     pose.translate(blockEntity.getBlockPos().getX(), blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ());
//
//            if (blockEntity.hasPaper()) {
//                pose.pushMatrix();
//                {
//                    pose.translate(0.5, 0.5, 0.5);
//                    pose.mulPose(state.getValue(PrinterBlock.FACING).getRotation());
//                    pose.mulPose(new Quaternionf(1, 0, 0, 22.5f));
//                    pose.translate(0, 0, 0.4);
//                    pose.translate(-11 * 0.015625, -13 * 0.015625, -0.5 * 0.015625);
//
//                    // BUFFER START
//                //    BufferBuilder builder = bufferSource.getBuffer();
//
//                //    MultiBufferSource.BufferSource modelBufferSource = MultiBufferSource.immediate(builder);
//                   // VertexConsumer vertexconsumer = modelBufferSource.getBuffer(paperModel.renderType(PaperModel.TEXTURE));
//                    //paperModel.renderToBuffer(pose, vertexconsumer, 15728880, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
//
//    //                tesselator.end();
//                    // BUFFER END
//                }
//                pose.popMatrix();
//            }
//
//            pose.pushMatrix();
//            {
//                if (blockEntity.isLoading()) {
//                    pose.translate(0.5, 0.5, 0.5);
//                    pose.mulPose(state.getValue(PrinterBlock.FACING).getRotation());
//                    pose.mulPose(new Quaternionf(1, 0, 0, 22.5f));
//                    double progress = Math.max(-0.4, -0.4 + (0.4 * ((double) (blockEntity.getRemainingPrintTime() - 10) / 20)));
//                    pose.translate(0, progress, 0.36875);
//                    pose.translate(-11 * 0.015625, -13 * 0.015625, -0.5 * 0.015625);
//
//                    // BUFFER START
//                //    BufferBuilder builder = tesselator.getBuilder();
//
//             //       MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(builder);
//                 //   VertexConsumer vertexconsumer = buffer.getBuffer(paperModel.renderType(PaperModel.TEXTURE));
//                 //   paperModel.renderToBuffer(pose, vertexconsumer, 15728880, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
//
//       //             tesselator.end();
//                    // BUFFER END
//                } else if (blockEntity.isPrinting()) {
//                    pose.translate(0.5, 0.078125, 0.5);
//                    pose.mulPose(state.getValue(PrinterBlock.FACING).getRotation());
//                    pose.mulPose(new Quaternionf(1, 0, 0, 90f));
//                    double progress = -0.35 + (0.50 * ((double) (blockEntity.getRemainingPrintTime() - 20) / blockEntity.getTotalPrintTime()));
//                    pose.translate(0, progress, 0);
//                    pose.translate(-11 * 0.015625, -13 * 0.015625, -0.5 * 0.015625);
//
//                    // BUFFER START
//                 //   BufferBuilder builder = tesselator.getBuilder();
//
//           //         MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(builder);
//            //  //      VertexConsumer vertexconsumer = buffer.getBuffer(paperModel.renderType(PaperModel.TEXTURE));
//            //        paperModel.renderToBuffer(pose, vertexconsumer, 15728880, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
//
//                  //  tesselator.end();
//                    // BUFFER END
//
//                    pose.translate(0.3225, 0.085, -0.001);
//                    pose.mulPose(new Quaternionf(0, 1, 0, 180f));
//                    pose.scale(0.3f, 0.3f, 0.3f);
//
//             //       IPrint print = blockEntity.getPrint();
//         //           if (print != null) {
//              //          IPrint.Renderer renderer = PrintingManager.getRenderer(print);
//        //                renderer.render(pose, print.toTag());
//             //       }
//                }
//            }
//            pose.popMatrix();
//
//            pose.pushMatrix(); // mulPose == rotation
//            {
//                RenderSystem.depthMask(false);
//                pose.translate(0.5, 0.5, 0.5);
//             //   pose.mulPose(state.getValue(PrinterBlock.FACING).getRotation());
//           //     pose.mulPose(new Quaternion(180, 0, 0, 0));
//                pose.translate(-0.0675, 0.005, -0.032);
//                pose.translate(0.8125, 0, -0.315);
//                pose.translate(-6.5 * 0.0625, -3.5 * 0.0625, 3.01 * 0.0625);
//              //  pose.translate(0, 3, 0);
//                pose.pushMatrix();
//                pose.mulPose(Axis.of(new Vector3f(22.5f, 180, 0)).rotationDegrees(0));
//              //  pose.pushMatrix();
//             //   pose.scale(0.1f, -0.1f, 0.1f);
//                pose.scale(0.010416667f, -0.010416667f, 0.010416667f);
//                Minecraft.getInstance().font.drawInBatch(Integer.toString(blockEntity.getPaperCount()), Minecraft.getInstance().font.width("00")-Minecraft.getInstance().font.width(Integer.toString(blockEntity.getPaperCount())), 0, Color.WHITE.getRGB(), false, pose.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0x00000000, packedLight);
//                pose.popMatrix();
//                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
//                RenderSystem.depthMask(true);
//            }
//            pose.popMatrix();
//        }
//        pose.popMatrix();
//
////        pose.pushMatrix();
////        {
////            pose.translate(0, -0.5, 0);
////            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, pose, bufferSource, packedLight, packedOverlay, EmptyModelData.INSTANCE);
////                super.render(blockEntity, x, y, z, partialTicks, destroyStage, alpha);
////        }
////        pose.popMatrix();
//    }

    @Override
    public @NonNull PrinterRenderState createRenderState() {
        return new PrinterRenderState();
    }

    @Override
    public void submit(@NonNull PrinterRenderState state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState camera) {

    }

//    public static class PaperModel extends Model {
//        public static final Identifier TEXTURE = new Identifier(Reference.MOD_ID, "textures/model/paper.png");
//        public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Devices.id("paper_model"), "main");
//        private final ModelPart root;
////        private final ModelPart main;
//
////            private final ModelRenderer box = new ModelRenderer(this, 0, 0).addBox(0, 0, 0, 22, 30, 1);
//
//        public PaperModel(ModelPart pRoot) {
//            super(RenderType::entitySolid);
//            this.root = pRoot;
////            this.main = pRoot.getChild("main");
//        }
//
//        public static LayerDefinition createBodyLayer() {
//            MeshDefinition meshdefinition = new MeshDefinition();
//            PartDefinition partdefinition = meshdefinition.getRoot();
//            partdefinition.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0, 22, 30, 1), PartPose.offset(0f, 0f, 0f));
//            return LayerDefinition.create(meshdefinition, 64, 32);
//        }
//
//        @Override
//        public void renderToBuffer(@NotNull PoseStack pPoseStack, @NotNull VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
//            this.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
//        }
//
//        private void render(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
//            RenderSystem.setShaderTexture(0, TEXTURE);
//            this.root.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
//        }
//
////        public ModelPart getMain() {
////            return main;
////        }
//    }
}
