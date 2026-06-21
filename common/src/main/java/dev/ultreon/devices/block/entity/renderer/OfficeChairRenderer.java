package dev.ultreon.devices.block.entity.renderer;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.ultreon.devices.block.OfficeChairBlock;
import dev.ultreon.devices.block.entity.OfficeChairBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;

public class OfficeChairRenderer implements BlockEntityRenderer<OfficeChairBlockEntity> {
    private Minecraft mc = Minecraft.getInstance();

    public OfficeChairRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(OfficeChairBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        BlockPos pos = blockEntity.getBlockPos();
        BlockState tempState = blockEntity.getLevel().getBlockState(pos);
        if(!(tempState.getBlock() instanceof OfficeChairBlock))
        {
            return;
        }

        var x = pos.getX();
        var y = pos.getY();
        var z = pos.getZ();

        poseStack.pushPose();
        {
           // poseStack.translate(x, y, z);

            poseStack.translate(0.5, 0, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(-blockEntity.getRotation() + 180));
            poseStack.translate(-0.5, 0, -0.5);

            BlockState state = tempState.setValue(OfficeChairBlock.FACING, Direction.NORTH).setValue(OfficeChairBlock.TYPE, OfficeChairBlock.Type.SEAT);

            Lighting.setupForFlatItems();
            //GlStateManager.enableTexture2D();

            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

            //Tessellator tessellator = Tessellator.getInstance();

            //BufferBuilder buffer = tessellator.getBuffer();
            //buffer.begin(7, DefaultVertexFormats.BLOCK);
            //buffer.setTranslation(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());

            BlockRenderDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
            blockrendererdispatcher.renderSingleBlock(state, poseStack, bufferSource, packedLight, packedOverlay);

            Lighting.setupFor3DItems();
        }
        poseStack.popPose();
    }
}