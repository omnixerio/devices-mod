package dev.ultreon.devices.api.driver;

import dev.ultreon.devices.core.UltreonDevicesConn;
import dev.ultreon.devices.impl.hardware.HardwarePacket;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The Hardware interface represents a virtual hardware component within Ultreon Devices.
 * It provides methods for initialization, shutdown, communication via hardware packets, and
 * retrieval of unique identifiers for the hardware's vendor and product.
 *
 * @param <R> the type of response expected from a packet sent to the hardware component
 * @param <T> the type of packet used to communicate with the hardware component
 */
public interface Hardware<R extends HardwarePacket.Response, T extends HardwarePacket<R>> {
    /**
     * Initializes the virtual hardware component asynchronously. This method prepares the hardware
     * for operation by performing any necessary setup tasks, such as establishing
     * connections or configuring the hardware state.
     * <p>
     * This method acts as a hardware component within the virtual world of Minecraft.
     * Often connected to a device (block entity or item) within the game world.
     *
     * @return a CompletableFuture that completes when the initialization process finishes,
     * or completes exceptionally if an error occurs during initialization
     */
    CompletableFuture<Void> init();

    /**
     * Shuts down the hardware component asynchronously. This method performs any necessary
     * cleanup or teardown tasks to safely terminate the virtual hardware's operation and release
     * allocated resources.
     *
     * @return a CompletableFuture that completes when the shutdown process finishes,
     * or completes exceptionally if an error occurs during shutdown
     */
    CompletableFuture<Void> shutdown();

    /**
     * Sends a hardware packet to the specified connection and returns a CompletableFuture
     * that completes with the packet's response.
     *
     * @param packet the hardware packet to be sent
     * @param conn   the connection through which the hardware packet is sent
     * @return a CompletableFuture that completes with the response from the hardware packet,
     * or completes exceptionally if the send operation fails
     */
    default CompletableFuture<R> send(T packet, UltreonDevicesConn conn) {
        return packet.send(conn);
    }

    UUID getVendorId();

    UUID getProductId();
}
