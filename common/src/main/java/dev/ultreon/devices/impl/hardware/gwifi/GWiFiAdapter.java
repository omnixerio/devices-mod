package dev.ultreon.devices.impl.hardware.gwifi;

import dev.ultreon.devices.api.device.RemoteDevice;
import dev.ultreon.devices.api.driver.Hardware;
import dev.ultreon.devices.api.driver.KnownProductIDs;
import dev.ultreon.devices.api.driver.KnownVendorIDs;
import dev.ultreon.devices.core.UltreonDevicesConn;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class GWiFiAdapter implements Hardware<GWiFiPacket.GWiFiResponse, GWiFiPacket> {
    private boolean poweredOn;

    @Override
    public CompletableFuture<Void> init() {
        return CompletableFuture.completedFuture(null).thenRun(() -> poweredOn = true);
    }

    @Override
    public CompletableFuture<Void> shutdown() {
        poweredOn = false;
        return CompletableFuture.completedFuture(null).thenRun(() -> poweredOn = false);
    }

    @Override
    public UUID getVendorId() {
        return KnownVendorIDs.UltreonStudios;
    }

    @Override
    public UUID getProductId() {
        return KnownProductIDs.GWiFi;
    }
}
