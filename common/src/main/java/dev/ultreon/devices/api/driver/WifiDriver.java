package dev.ultreon.devices.api.driver;

import dev.ultreon.devices.api.app.OperatingSystem;
import dev.ultreon.devices.core.network.WiFiNetwork;
import dev.ultreon.devices.core.network.WifiStrength;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface WifiDriver extends NetworkDriver {
    String getConnectedSSID();

    @Override
    default String getNetworkName() {
        return "WiFi " + getConnectedSSID();
    }

    CompletableFuture<Boolean> connect(String ssid);

    @Override
    default boolean isWireless() {
        return true;
    }

    CompletableFuture<Void> disconnect();

    CompletableFuture<List<WiFiNetwork>> scan();

    CompletableFuture<WiFiNetwork> ping();
}
