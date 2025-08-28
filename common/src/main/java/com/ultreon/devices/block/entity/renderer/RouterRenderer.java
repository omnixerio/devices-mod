package com.ultreon.devices.block.entity.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.ultreon.devices.block.PrinterBlock;
import com.ultreon.devices.block.RouterBlock;
import com.ultreon.devices.block.entity.RouterBlockEntity;
import com.ultreon.devices.core.network.NetworkDevice;
import com.ultreon.devices.core.network.Router;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.Objects;

/**
 * @author MrCrayfish
 */
public record RouterRenderer(
        BlockEntityRendererProvider.Context context) implements BlockEntityRenderer<RouterBlockEntity> {

    @Override
    public void render(RouterBlockEntity blockEntity, float partialTick, @NotNull PoseStack pose, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = Objects.requireNonNull(blockEntity.getLevel()).getBlockState(blockEntity.getBlockPos());
        if (state.getBlock() != blockEntity.getBlock()) return;

        if (blockEntity.isDebug()) {
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(770, 771, 1, 0);
//            RenderSystem.disableLighting();
            //            RenderSystem.disableTexture();
//            RenderSystem.enableAlpha();
            pose.pushPose();
            {
                pose.translate(blockEntity.getBlockPos().getX(), blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ());
                Router router = blockEntity.getRouter();
                BlockPos routerPos = router.getPos();

                Vec3 linePositions = getLineStartPosition(state);
                final double startLineX = linePositions.x;
                final double startLineY = linePositions.y;
                final double startLineZ = linePositions.z;

                Tesselator tesselator = Tesselator.getInstance();
                BufferBuilder buffer = tesselator.getBuilder();

                final Collection<NetworkDevice> DEVICES = router.getConnectedDevices(Minecraft.getInstance().level);
                DEVICES.forEach(networkDevice -> {
                    BlockPos devicePos = networkDevice.getPos();

                    Objects.requireNonNull(devicePos, "Connection device has no position, weird.");

                    RenderSystem.lineWidth(14F);
                    buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
                    buffer.vertex(startLineX, startLineY, startLineZ).color(0f, 0f, 0f, 0.5f).endVertex();
                    buffer.vertex((devicePos.getX() - routerPos.getX()) + 0.5f, (devicePos.getY() - routerPos.getY()), (devicePos.getZ() - routerPos.getZ()) + 0.5f).color(1f, 1f, 1f, 0.35f).endVertex();
                    tesselator.end();

                    RenderSystem.lineWidth(4F);
                    buffer.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR);
                    buffer.vertex(startLineX, startLineY, startLineZ).color(0f, 0f, 0f, 0.5f).endVertex();
                    buffer.vertex((devicePos.getX() - routerPos.getX()) + 0.5f, (devicePos.getY() - routerPos.getY()), (devicePos.getZ() - routerPos.getZ()) + 0.5f).color(0f, 1f, 0f, 0.5f).endVertex();
                    tesselator.end();
                });
            }
            pose.popPose();
            RenderSystem.disableBlend();
//            RenderSystem.disableAlpha();
//            RenderSystem.enableLighting();
       //     RenderSystem.enableTexture();
        }
    }

    private Vec3 getLineStartPosition(BlockState state) {
        float lineX = 0.5f;
        float lineY = 0.1f;
        float lineZ = 0.5f;

        if (state.getValue(RouterBlock.VERTICAL)) {
            Quaternionf rotation = state.getValue(PrinterBlock.FACING).getRotation();
            rotation.mul(new Quaternionf((float) (14 * 0.0625), 0.5f, (float) (14 * 0.0625), 0.5f));
            Vector3f fixedPosition = new Vector3f(rotation.x, rotation.y, rotation.z);
            lineX = fixedPosition.x();
            lineY = 0.35f;
            lineZ = fixedPosition.z();
        }

        return new Vec3(lineX, lineY, lineZ);
    }
}
