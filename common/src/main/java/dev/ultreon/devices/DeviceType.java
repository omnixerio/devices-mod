package dev.ultreon.devices;

import org.jetbrains.annotations.Contract;

public enum DeviceType implements DeviceTypeSupplier {
    COMPUTER, PRINTER, FLASH_DRIVE, ROUTER, SEAT;

    @Override
    @Contract(pure = true, value = "-> this")
    public DeviceType getDeviceType() {
        return this;
    }
}
