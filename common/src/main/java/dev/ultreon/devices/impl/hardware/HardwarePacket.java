package dev.ultreon.devices.impl.hardware;

import dev.ultreon.devices.core.UltreonDevicesConn;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.concurrent.CompletableFuture;

public interface HardwarePacket<T extends HardwarePacket.Response> {
    T process(RegistryFriendlyByteBuf buf);

    CompletableFuture<T> send(UltreonDevicesConn conn);

    interface Response {
        void encode(RegistryFriendlyByteBuf buf);
    }
}
