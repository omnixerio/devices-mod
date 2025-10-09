package dev.ultreon.devices.api.driver;

import dev.ultreon.devices.core.network.NetworkState;

public interface NetworkDriver extends Driver {
    String getNetworkName();

    NetworkState getNetworkState();

    default boolean isWireless() {
        return false;
    }

    boolean isConnected();

    void refresh();
}
