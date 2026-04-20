package dev.ultreon.devices.fabric.platform.client;

import dev.ultreon.devices.platform.client.ClientPayloadContext;
import dev.ultreon.devices.platform.client.IClientPlatformHelper;
import dev.ultreon.devices.platform.client.PayloadHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.UnknownNullability;

import java.util.function.Supplier;

public class FabricClientPlatformHelper implements IClientPlatformHelper {
    @Override
    public <T extends BlockEntity, S extends BlockEntityRenderState> void registerBlockEntityRenderer(@UnknownNullability Supplier<BlockEntityType<T>> blockEntityType, BlockEntityRendererProvider<T, S> blockEntityRenderer) {
        BlockEntityRenderers.register(blockEntityType.get(), blockEntityRenderer);
    }

    @Override
    public <T extends Entity> void registerEntityRenderer(@UnknownNullability Supplier<EntityType<T>> entityType, EntityRendererProvider<T> entityRenderer) {
        EntityRenderers.register(entityType.get(), entityRenderer);
    }

    @Override
    public void sendToServer(CustomPacketPayload syncBlockPacket) {
        ClientPlayNetworking.send(syncBlockPacket);
    }

    @Override
    public <T extends CustomPacketPayload> void registerClientboundPlay(CustomPacketPayload.Type<T> type, PayloadHandler<T, ClientPayloadContext> o) {
        ClientPlayNetworking.registerGlobalReceiver(type, (packet, context) -> o.handle(packet, new ClientPayloadContext(context.player(), context.client())));
    }
}
