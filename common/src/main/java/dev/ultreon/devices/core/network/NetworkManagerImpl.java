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
        if (++pingTimer >= DeviceConfig.PING_RATE.get()) {
            runPingTask();
            pingTimer = 0;
        }
    }

    private void runPingTask() {
        TaskPing task = new TaskPing(ComputerScreen.getPos());
        task.setCallback((tag, success) -> {
            if (success) {
                assert tag != null;
                int strength = tag.getInt("strength");
                switch (strength) {
                    case 2 -> {
                        this.strength = WifiStrength.LOW;
                    }
                    case 1 -> {
                        this.strength = WifiStrength.MED;
                    }
                    case 0 -> {
                        this.strength = WifiStrength.HIGH;
                    }
                    default -> {
                        this.strength = WifiStrength.NONE;
                    }
                }
            }
        });
        TaskManager.sendTask(task);
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
