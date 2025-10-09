package dev.ultreon.devices.impl.driver;

import dev.ultreon.devices.api.app.OperatingSystem;
import dev.ultreon.devices.api.driver.*;
import dev.ultreon.devices.client.UltreonDevicesClient;
import dev.ultreon.devices.core.network.NetworkState;
import dev.ultreon.devices.core.network.WiFiNetwork;
import dev.ultreon.devices.core.network.WifiStrength;
import dev.ultreon.devices.impl.hardware.gwifi.GWiFiPacket;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class GenericWifiDriver implements WifiDriver {
    public static final UUID VENDOR_ID = KnownVendorIDs.UltreonStudios;
    public static final UUID PRODUCT_ID = KnownProductIDs.GWiFi;

    private final Hardware<GWiFiPacket.GWiFiResponse, GWiFiPacket> hardware;

    private String connectedSSID = null;
    private WifiStrength strength = WifiStrength.NONE;
    private NetworkState state = NetworkState.DISCONNECTED;
    private boolean initialized;

    public GenericWifiDriver(Hardware<GWiFiPacket.GWiFiResponse, GWiFiPacket> hardware) {
        this.hardware = hardware;
    }

    @Override
    public String getConnectedSSID() {
        return connectedSSID;
    }

    @Override
    public CompletableFuture<Boolean> connect(String ssid) {
        state = NetworkState.CONNECTING;
        return hardware.send(new GWiFiPacket(GWiFiPacket.Type.CONNECT), UltreonDevicesClient.getInstance()).thenApply(packet -> {
            if (!(packet instanceof GWiFiPacket.Connect(NetworkState newState))) {
                return false;
            }
            if (newState != NetworkState.CONNECTED) {
                state = newState;
                return false;
            }
            onConnected(ssid);
            return false;
        });
    }

    @Override
    public CompletableFuture<Void> disconnect() {
        state = NetworkState.DISCONNECTING;
        return hardware.send(new GWiFiPacket(GWiFiPacket.Type.DISCONNECT), UltreonDevicesClient.getInstance()).thenApply(packet -> {
            if (packet instanceof GWiFiPacket.Disconnect) disconnected();
            return null;
        });
    }

    @Override
    public CompletableFuture<List<WiFiNetwork>> scan() {
        return hardware.send(new GWiFiPacket(GWiFiPacket.Type.SCAN), UltreonDevicesClient.getInstance()).thenApply(packet -> {
            if (packet instanceof GWiFiPacket.Scan) return ((GWiFiPacket.Scan) packet).names();
            return Collections.emptyList();
        });
    }

    @Override
    public NetworkState getNetworkState() {
        return state;
    }

    @Override
    public boolean isConnected() {
        return connectedSSID != null;
    }

    @Override
    public void refresh() {
        // NO-OP
    }

    @Override
    public UUID getVendorId() {
        return VENDOR_ID;
    }

    @Override
    public UUID getProductId() {
        return PRODUCT_ID;
    }

    @Override
    public void init(Hardware<?, ?> hardware) throws DriverException {
        initialized = true;
        this.hardware.send(new GWiFiPacket(GWiFiPacket.Type.INITIALIZE, (Runnable) this::disconnected, (Consumer<Integer>) this::onLevel), UltreonDevicesClient.getInstance()).join();
    }

    private void onLevel(@Nullable Integer level) {
        strength = WifiStrength.fromLevel(level);
    }

    private void disconnected() {
        connectedSSID = null;
        state = NetworkState.DISCONNECTED;
        strength = WifiStrength.NONE;
        initialized = false;
    }

    private void onConnected(String ssid) {
        connectedSSID = ssid;
        state = NetworkState.CONNECTED;
        strength = WifiStrength.LOW;
    }

    @Override
    public void close() throws DriverException {
        connectedSSID = null;
        state = NetworkState.DISCONNECTED;
        strength = WifiStrength.NONE;
        hardware.send(new GWiFiPacket(GWiFiPacket.Type.CLOSE), UltreonDevicesClient.getInstance()).join();
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void load(OperatingSystem system, Hardware<?, ?> hardware) {
        if (hardware instanceof GenericWifiDriver) {
            try {
                init(hardware);
            } catch (DriverException e) {
                system.logError("Failed to initialize GWiFi driver, likely an incompatible device.", e);
                try {
                    close();
                } catch (DriverException ex) {
                    system.logError("Failed to close GWiFi driver.", ex);
                }

                initialized = false;
            }
        }
    }
}
