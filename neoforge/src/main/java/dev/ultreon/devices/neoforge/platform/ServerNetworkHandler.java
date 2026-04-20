package dev.ultreon.devices.neoforge.platform;

import dev.ultreon.devices.neoforge.platform.client.ClientNetworkHandler;
import dev.ultreon.devices.neoforge.platform.client.NeoForgeClientPlatformHelper;
import dev.ultreon.devices.platform.client.ClientPayloadContext;
import dev.ultreon.devices.platform.client.PayloadHandler;
import dev.ultreon.devices.platform.services.ServerPayloadContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ServerNetworkHandler<T extends CustomPacketPayload> {
    private final CustomPacketPayload.Type<T> type;
    private final StreamCodec<? super RegistryFriendlyByteBuf, T> codec;
    private final PayloadHandler<T, ServerPayloadContext> handler;

    public ServerNetworkHandler(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, PayloadHandler<T, ServerPayloadContext> handler) {
        this.type = type;
        this.codec = codec;
        this.handler = handler;
    }

    public void register(PayloadRegistrar event) {
        event.playToServer(type, codec, (payload, context) -> {
            ServerPlayer player = (ServerPlayer) context.player();
            handler.handle(payload, new ServerPayloadContext(player, player.level().getServer()));
        });
    }
}
