package dev.ultreon.devices.neoforge.platform.client;

import dev.ultreon.devices.platform.services.RegistrySupplier;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.function.Supplier;

public class BlockEntityRendererEntry<T extends BlockEntity, S extends BlockEntityRenderState> {
    public final Supplier<BlockEntityType<T>> blockEntityType;
    public final BlockEntityRendererProvider<T, S> blockEntityRenderer;

    public BlockEntityRendererEntry(Supplier<BlockEntityType<T>> blockEntityType, BlockEntityRendererProvider<T, S> blockEntityRenderer) {
        this.blockEntityType = blockEntityType;
        this.blockEntityRenderer = blockEntityRenderer;
    }

    public void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(blockEntityType.get(), blockEntityRenderer);
    }
}
