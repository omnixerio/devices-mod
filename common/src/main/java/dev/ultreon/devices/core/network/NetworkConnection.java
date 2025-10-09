package dev.ultreon.devices.core.network;

import dev.ultreon.devices.api.driver.NetworkDriver;
import dev.ultreon.devices.api.driver.WifiDriver;

public record NetworkConnection(String ssid, NetworkDriver driver) {

}
