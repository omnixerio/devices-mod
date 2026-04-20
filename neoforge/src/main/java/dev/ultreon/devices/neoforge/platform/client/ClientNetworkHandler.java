package dev.ultreon.devices.neoforge.platform.client;

import dev.ultreon.devices.platform.client.ClientPayloadContext;
import dev.ultreon.devices.platform.client.PayloadHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.UnknownNullability;

public class ClientNetworkHandler<T extends CustomPacketPayload> {
    private final CustomPacketPayload.Type<T> type;
    private final PayloadHandler<T, ClientPayloadContext> handler;

    public ClientNetworkHandler(CustomPacketPayload.Type<T> type, PayloadHandler<T, ClientPayloadContext> handler) {
        this.type = type;
        this.handler = handler;
    }

    public void register(RegisterClientPayloadHandlersEvent event) {
        event.register(type, (payload, context) -> handler.handle(payload, new ClientPayloadContext((LocalPlayer) context.player(), Minecraft.getInstance())));
    }
}
