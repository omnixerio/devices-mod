package dev.ultreon.devices.core.jobs;

import com.mojang.datafixers.util.Either;
import dev.ultreon.devices.api.device.DeviceOrigin;
import dev.ultreon.devices.api.device.RemoteDeviceOrigin;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

public record HardwareRequest<T>(
        int requestId,
        UUID vendorId,
        UUID productId,
        Either<RemoteDeviceOrigin, DeviceOrigin> origin,
        T data
) {
    public static <T> HardwareRequest<T> read(RegistryFriendlyByteBuf buf, Function<RegistryFriendlyByteBuf, T> read) {
        return new HardwareRequest<>(
                buf.readVarInt(),
                buf.readUUID(),
                buf.readUUID(),
                Either.right(DeviceOrigin.read(buf)),
                read.apply(buf)
        );
    }

    public static <T> HardwareRequest<T> readRemote(RegistryFriendlyByteBuf buf, Function<RegistryFriendlyByteBuf, T> read) {
        return new HardwareRequest<>(
                buf.readVarInt(),
                buf.readUUID(),
                buf.readUUID(),
                Either.left(RemoteDeviceOrigin.read(buf)),
                read.apply(buf)
        );
    }

    public void write(RegistryFriendlyByteBuf buf, BiConsumer<RegistryFriendlyByteBuf, T> write) {
        buf.writeVarInt(requestId);
        buf.writeUUID(vendorId);
        buf.writeUUID(productId);
        origin.right().ifPresentOrElse(o -> o.getSerializer().encode(buf, o), () -> {
            throw new IllegalStateException("Device origin is not present");
        });
        write.accept(buf, data);
    }

    public void writeRemote(RegistryFriendlyByteBuf buf, BiConsumer<RegistryFriendlyByteBuf, T> write) {
        buf.writeVarInt(requestId);
        buf.writeUUID(vendorId);
        buf.writeUUID(productId);
        origin.left().ifPresentOrElse(o -> o.getSerializer().encodeRemote(buf, o), () -> {
            throw new IllegalStateException("Remote device origin is not present");
        });
        write.accept(buf, data);
    }

    public enum State {
        BEGIN_REQUEST,
        END_REQUEST,
        BEGIN_CALLBACKS,
        END_CALLBACKS,
    }
}
