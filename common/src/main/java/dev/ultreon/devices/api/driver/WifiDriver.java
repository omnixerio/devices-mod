package dev.ultreon.devices.api.driver;

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

    CompletableFuture<List<String>> scan();
}
