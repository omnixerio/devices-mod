package dev.ultreon.devices.api.driver;

import dev.ultreon.devices.core.UltreonDevicesConn;
import dev.ultreon.devices.impl.hardware.HardwarePacket;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Hardware<R extends HardwarePacket.Response, T extends HardwarePacket<R>>  {
    CompletableFuture<Void> init();
    CompletableFuture<Void> shutdown();

    default CompletableFuture<R> send(T packet, UltreonDevicesConn conn) {
        return packet.send(conn);
    }

    UUID getVendorId();
    UUID getProductId();
}
