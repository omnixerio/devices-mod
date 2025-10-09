package dev.ultreon.devices.core.network;

import dev.ultreon.devices.DeviceConfig;
import dev.ultreon.devices.UltreonDevices;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum WifiStrength {
    LOW, MED, HIGH, NONE;

    public static @NotNull WifiStrength fromLevel(Integer level) {
        if (level == null || level <= 0) return NONE;

        if (level < DeviceConfig.SIGNAL_RANGE.getAsInt() / 4) return HIGH;
        if (level < DeviceConfig.SIGNAL_RANGE.getAsInt() / 2) return MED;
        return LOW;
    }

    public static WifiStrength fromOrdinal(int i) {
        if (i < 0 || i >= values().length) {
            UltreonDevices.LOGGER.warn("Invalid ordinal for WifiStrength: {}", i);
            return NONE;
        }
        return values()[i];
    }
}
