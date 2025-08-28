package dev.ultreon.devices.block.entity.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.ultreon.devices.block.computer.LaptopBlock;
import dev.ultreon.devices.block.entity.computer.LaptopBlockEntity;
import dev.ultreon.devices.init.DeviceItems;
import dev.ultreon.devices.item.FlashDriveItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class LaptopRenderer implements BlockEntityRenderer<LaptopBlockEntity> {
    private final Minecraft mc = Minecraft.getInstance();

    public LaptopRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(LaptopBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        var direction = blockEntity.getBlockState().getValue(LaptopBlock.FACING).getClockWise().toYRot();

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        ItemEntity entityItem = new ItemEntity(level, 0, 0, 0, ItemStack.EMPTY) {
            @Override
            public float getSpin(float partialTicks) {
                return ((float) this.getAge() + partialTicks) / 20.0f + 0;
            }
        };

//        entityItem.bobOffs = 0; // TODO fix
        entityItem.setYRot(0);
        BlockState state = blockEntity.getBlock().defaultBlockState().setValue(LaptopBlock.TYPE, LaptopBlock.Type.SCREEN);

        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        poseStack.pushPose();
        //<editor-fold desc="rendering: <...>">
            if (blockEntity.isExternalDriveAttached()) {
                poseStack.pushPose();
                //<editor-fold desc="External drive <...>">
                {
                    poseStack.translate(0.5, 0, 0.5);
                    poseStack.mulPose(blockEntity.getBlockState().getValue(LaptopBlock.FACING).getRotation());
                    poseStack.mulPose(new Quaternionf().rotateZ((float) Math.toRadians(-90)));
                    poseStack.mulPose(new Quaternionf().rotateX((float) Math.toRadians(-90)));
                    poseStack.translate(-0.5, 0, -0.5);
                    poseStack.translate(0.595, -0.2075, -0.005);

                    entityItem.flyDist = 0.0F;
                    FlashDriveItem flashDriveByColor = DeviceItems.getFlashDriveByColor(blockEntity.getExternalDriveColor());
                    assert flashDriveByColor != null;
                    entityItem.setItem(new ItemStack(flashDriveByColor, 1/*, blockEntity.getExternalDriveColor().*/));
                    Minecraft.getInstance().getEntityRenderDispatcher().render(entityItem, 0, 0, 0, 0, 0, poseStack, bufferSource, packedLight);
                }
                //</editor-fold>
                poseStack.popPose();
            }

            poseStack.pushPose();
            //<editor-fold desc="rendering: <...>">
            {
                poseStack.translate(0.5, 0, 0.5);//west/east +90 north/south -90
                poseStack.mulPose(Axis.YP.rotationDegrees(blockEntity.getBlockState().getValue(LaptopBlock.FACING) == Direction.EAST || blockEntity.getBlockState().getValue(LaptopBlock.FACING) == Direction.WEST ? direction + 90 : direction - 90));
                poseStack.translate(-0.5, 0, -0.5);
                poseStack.translate(0, 0.0625, 0.25);
                poseStack.mulPose(Axis.XP.rotationDegrees(blockEntity.getScreenAngle(partialTick) + 180));
                poseStack.mulPose(Axis.XP.rotationDegrees(180));

                BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                BakedModel ibakedmodel = mc.getBlockRenderer().getBlockModel(state);
                poseStack.pushPose();
                dispatcher.renderSingleBlock(state, poseStack, bufferSource, packedLight, packedOverlay);//.renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.cutout()), state, ibakedmodel, 1, 1, 1, packedLight, packedOverlay);
                poseStack.popPose();
            }
            //</editor-fold>
            poseStack.popPose();
        //</editor-fold>
        poseStack.popPose();
    }
}