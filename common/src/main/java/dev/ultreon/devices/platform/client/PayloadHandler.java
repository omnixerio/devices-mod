package dev.ultreon.devices.platform.client;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface PayloadHandler<T extends CustomPacketPayload, C extends PayloadContext> {
    void handle(T payload, C context);
}
