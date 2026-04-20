package dev.ultreon.devices.platform.client;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public interface IClientPlatformHelper {
    <T extends BlockEntity, S extends BlockEntityRenderState> void registerBlockEntityRenderer(Supplier<BlockEntityType<T>> blockEntityType, BlockEntityRendererProvider<T, S> blockEntityRenderer);

    <T extends Entity> void registerEntityRenderer(Supplier<EntityType<T>> entityType, EntityRendererProvider<T> entityRenderer);

    void sendToServer(CustomPacketPayload syncBlockPacket);

    <T extends CustomPacketPayload> void registerClientboundPlay(CustomPacketPayload.Type<T> type, PayloadHandler<T, ClientPayloadContext> o);
}