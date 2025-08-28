package dev.ultreon.devices.entity.renderer;

import dev.ultreon.mods.xinexlib.Env;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

/// ## Renderer for the seat entity
/// This renderer is used to render the seat entity
///
/// @author [MrCrayfish](https://github.com/MrCrayfish), [Qubilux](https://github.com/XyperCode)
public class SeatEntityRenderer<T extends Entity> extends EntityRenderer<T> {

    public SeatEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}