package dev.ultreon.devices.neoforge.platform;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ClientNetworkCodec<T extends CustomPacketPayload> {
    private final CustomPacketPayload.Type<T> type;
    private final StreamCodec<? super RegistryFriendlyByteBuf, T> codec;

    public ClientNetworkCodec(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        this.type = type;
        this.codec = codec;
    }

    public void register(PayloadRegistrar registrar) {
        registrar.playToClient(type, codec);
    }
}
