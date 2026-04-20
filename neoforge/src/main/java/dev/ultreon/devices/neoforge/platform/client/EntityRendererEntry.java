package dev.ultreon.devices.neoforge.platform.client;

import dev.ultreon.devices.platform.services.RegistrySupplier;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.function.Supplier;

public class EntityRendererEntry<T extends Entity, S extends EntityRenderState> {
    public final Supplier<EntityType<T>> blockEntityType;
    public final EntityRendererProvider<T> blockEntityRenderer;

    public EntityRendererEntry(Supplier<EntityType<T>> blockEntityType, EntityRendererProvider<T> blockEntityRenderer) {
        this.blockEntityType = blockEntityType;
        this.blockEntityRenderer = blockEntityRenderer;
    }

    public void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(blockEntityType.get(), blockEntityRenderer);
    }
}
