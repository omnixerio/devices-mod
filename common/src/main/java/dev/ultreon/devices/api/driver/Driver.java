package dev.ultreon.devices.api.driver;

import java.util.UUID;

public interface Driver extends AutoCloseable {
    default String getName() {
        return "Unknown Driver (Vendor: " + getVendorId() + ", Product: " + getProductId() + ")";
    }

    UUID getVendorId();

    UUID getProductId();

    void init() throws DriverException;

    void close() throws DriverException;

    boolean isInitialized();
}
