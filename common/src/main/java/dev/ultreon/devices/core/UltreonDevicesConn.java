package dev.ultreon.devices.core;

import com.mojang.datafixers.util.Either;
import dev.ultreon.devices.api.device.RemoteDevice;
import dev.ultreon.devices.api.driver.Hardware;
import dev.ultreon.devices.api.hardware.Callbacks;
import dev.ultreon.devices.core.jobs.*;
import dev.ultreon.devices.impl.hardware.HardwarePacket;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface UltreonDevicesConn {
    Callbacks getCallbacks();

    default <R extends HardwarePacket.Response, T extends HardwarePacket<R>> CompletableFuture<R> sendHwPacket(T packet) {
        return packet.send(this);
    }

    default <R extends HardwarePacket.Response, T extends HardwarePacket<R>> CompletableFuture<Void> sendHwPacket(RemoteDevice device, UUID vendorId, UUID productId, Function<HardwareResponse<Void>, CompletableFuture<Void>> callbacks) {
        int i = getJobs().requestId();
        HardwareRequest<HardwareRequest.State> request = new HardwareRequest<>(
                i,
                vendorId,
                productId,
                Either.left(device.getOrigin()),
                HardwareRequest.State.BEGIN_REQUEST
        );
        return sendJob(Jobs.HARDWARE_STATE, request)
                .thenCompose(callbacks)
                .whenComplete((response, throwable) -> {
                    HardwareRequest<HardwareRequest.State> endRequest = new HardwareRequest<>(
                            i,
                            vendorId,
                            productId,
                            Either.left(device.getOrigin()),
                            HardwareRequest.State.END_REQUEST
                    );
                    sendJob(Jobs.HARDWARE_STATE, endRequest).join();
                });
    }

    default <T, R> CompletableFuture<R> sendJob(SimpleJob<T, R> job, T value) {
        return getJobs().sendJob(job, value);
    }

    JobManager getJobs();
}
