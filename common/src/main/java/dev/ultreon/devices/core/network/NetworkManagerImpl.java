package dev.ultreon.devices.core.network;

import dev.ultreon.devices.DeviceConfig;
import dev.ultreon.devices.UltreonDevices;
import dev.ultreon.devices.api.NetworkManager;
import dev.ultreon.devices.api.app.OperatingSystem;
import dev.ultreon.devices.api.driver.WifiDriver;
import dev.ultreon.devices.api.task.TaskManager;
import dev.ultreon.devices.core.ComputerScreen;
import dev.ultreon.devices.core.network.task.TaskPing;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

public class NetworkManagerImpl implements NetworkManager {
    private final OperatingSystem system;
    private int pingTimer;
    private WifiStrength strength;
    private boolean pinging;

    public NetworkManagerImpl(OperatingSystem system) {
        this.system = system;
        strength = WifiStrength.NONE;
    }

    public CompletableFuture<NetworkConnection> connect(String ssid) {
        WifiDriver[] drivers = system.getDrivers(WifiDriver.class);
        if (drivers.length > 0) {
            return CompletableFuture.supplyAsync(() -> {
                for (WifiDriver driver : drivers) {
                    if (driver.connect(ssid).join()) {
                        return new NetworkConnection(ssid, driver);
                    }
                }

                return null;
            }, UltreonDevices.NETWORK_EXECUTOR);
        }

        return CompletableFuture.failedFuture(new IllegalStateException("No Wifi drivers found!"));
    }

    public void tick() {
        if (++pingTimer >= DeviceConfig.PING_RATE.get() && !pinging) {
            pinging = true;
            runPingTask().thenAccept(network -> {
                if (network != null) {
                    strength = network.strength();
                } else {
                    strength = WifiStrength.NONE;
                }
                pinging = false;
            }).exceptionally(throwable -> {
                system.logError("Failed to ping network!", throwable);
                pinging = false;
                return null;
            });
            pingTimer = 0;
        }
    }

    private CompletableFuture<WiFiNetwork> runPingTask() {
        try {
            WifiDriver[] drivers = system.getDrivers(WifiDriver.class);
            for (WifiDriver driver : drivers) {
                if (driver.isConnected())
                    return driver.ping();
            }
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    public WifiStrength getStrength() {
        return strength;
    }

    public boolean isConnected() {
        return strength != WifiStrength.NONE;
    }

    public CompletableFuture<List<WiFiNetwork>> scan() {
        List<WiFiNetwork> ssids = new CopyOnWriteArrayList<>();
        WifiDriver[] drivers = system.getDrivers(WifiDriver.class);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (WifiDriver driver : drivers) {
            futures.add(driver.scan().thenAccept(ssids::addAll));
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenApply(v -> ssids);
    }
}
