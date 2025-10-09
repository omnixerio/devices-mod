package dev.ultreon.devices.core.jobs;

import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.function.BiConsumer;
import java.util.function.Function;

public record HardwareResponse<T>(int requestId, T data) {

    public void write(RegistryFriendlyByteBuf buf, BiConsumer<RegistryFriendlyByteBuf, T> write) {
        buf.writeVarInt(requestId);
        write.accept(buf, data);
    }

    public static <T> HardwareResponse<T> read(RegistryFriendlyByteBuf buf, Function<RegistryFriendlyByteBuf, T> read) {
        return new HardwareResponse<>(buf.readVarInt(), read.apply(buf));
    }
}
