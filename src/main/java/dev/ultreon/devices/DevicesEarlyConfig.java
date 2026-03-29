package dev.ultreon.devices;

import dev.ultreon.mods.xinexlib.platform.XinexPlatform;

import java.io.*;

/**
 * Devices mod early configuration.
 * This is used to configure devices mod early, when the forge config isn't loaded yet.
 *
 * @author <a href="https://github.com/XyperCode">XyperCode</a>
 */
public class DevicesEarlyConfig {
    private static final File FILE = XinexPlatform.getConfigDir().resolve("devices-early-config.json").toFile();
    public boolean enableBetaApps = false;
    public boolean enableDebugApps = false;

    /**
     * Loads the devices early config.
     *
     * @return the loaded config instance or a new one if it doesn't exist.
     */
    public static DevicesEarlyConfig load() {
        try (FileReader reader = new FileReader(FILE)) {
            return UltreonDevicesCommon.GSON.fromJson(reader, DevicesEarlyConfig.class);
        } catch (FileNotFoundException e) {
            DevicesEarlyConfig devicesEarlyConfig = new DevicesEarlyConfig();
            devicesEarlyConfig.save();
            return devicesEarlyConfig;
        } catch (IOException e) {
            UltreonDevicesCommon.LOGGER.error("Failed to load devices early config", e);
            return new DevicesEarlyConfig();
        }
    }

    /**
     * Saves the devices early config. This also creates the file if it doesn't exist.
     */
    public void save() {
        try (FileWriter writer = new FileWriter(FILE)) {
            UltreonDevicesCommon.GSON.toJson(this, writer);
        } catch (IOException e) {
            UltreonDevicesCommon.LOGGER.error("Failed to save devices early config", e);
        }
    }
}
