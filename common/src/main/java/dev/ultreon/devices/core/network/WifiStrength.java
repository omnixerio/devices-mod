package dev.ultreon.devices.core.network;

import org.jetbrains.annotations.Nullable;

public enum WifiStrength {
    LOW, MED, HIGH, NONE;

    public static @Nullable WifiStrength fromLevel(Integer level) {
        if (level == null || level <= 0) return NONE;

        if (level < 20) return LOW;
        else if (level < 70) return MED;
        else return HIGH;
    }
}
