package dev.ultreon.devices.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.NonNull;

public class SeatEntityRenderer<T extends Entity>
        extends EntityRenderer<T, SeatEntityRenderState> {

    public SeatEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NonNull SeatEntityRenderState createRenderState() {
        return new SeatEntityRenderState();
    }

    @Override
    public void submit(@NonNull SeatEntityRenderState state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState camera) {

    }
}