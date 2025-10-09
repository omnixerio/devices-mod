package dev.ultreon.devices.api.driver;

import dev.ultreon.devices.api.app.OperatingSystem;

import java.util.UUID;

/**
 * Represents a driver interface that facilitates communication between hardware
 * devices and an operating system. This interface provides methods to initialize,
 * load, monitor, and close a driver.
 */
public interface Driver extends AutoCloseable {
    /**
     * Retrieves the name of the driver. The default implementation returns a string
     * representation indicating that the driver is an unknown driver, along with
     * its associated vendor and product IDs.
     *
     * @return the name of the driver as a string, including vendor and product identifiers
     */
    default String getName() {
        return "Unknown Driver (Vendor: " + getVendorId() + ", Product: " + getProductId() + ")";
    }

    /**
     * Retrieves the unique vendor identifier associated with the driver or hardware.
     *
     * @return the UUID representing the vendor ID
     */
    UUID getVendorId();

    /**
     * Retrieves the unique product identifier associated with the driver or hardware.
     *
     * @return the UUID representing the product ID
     */
    UUID getProductId();

    /**
     * Initializes the driver with the given hardware instance. This method sets up
     * the necessary communication and prepares the driver to operate with the
     * specified hardware device. If the initialization process fails, a
     * {@link DriverException} will be thrown.
     *
     * @param hardware the hardware instance that the driver will manage and operate with
     * @throws DriverException if an error occurs during the initialization process
     */
    void init(Hardware<?, ?> hardware) throws DriverException;

    /**
     * Closes the driver and releases any resources or connections associated with it.
     * This method is used to perform cleanup operations such as resetting the state,
     * terminating active connections, and preparing the driver for safe disposal.
     *
     * @throws DriverException if an error occurs during the closure of the driver
     */
    void close() throws DriverException;

    /**
     * Checks if the driver has been successfully initialized.
     *
     * @return true if the driver is initialized, false otherwise
     */
    boolean isInitialized();

    /**
     * Loads the driver onto the specified operating system and hardware. This operation
     * may perform initialization or configuration steps required for the driver to
     * interact with the hardware and operating system properly.
     *
     * @param system   the operating system to load the driver onto
     * @param hardware the hardware device that the driver is intended to handle
     */
    void load(OperatingSystem system, Hardware<?, ?> hardware);
}
