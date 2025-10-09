package dev.ultreon.devices.core.jobs;

import dev.ultreon.devices.UltreonDevices;
import dev.ultreon.mods.xinexlib.registrar.Registrar;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public interface Job<T, R> {
    void writeData(RegistryFriendlyByteBuf buf, T data);

    @SuppressWarnings({"rawtypes", "unchecked"})
    static <T> T read(RegistryFriendlyByteBuf buf) {
        Registrar<Job<T, ?>> job = (Registrar<Job<T,?>>) (Registrar) UltreonDevices.getRegistries().job();
        Job<T, ?> job1 = job.registry().byId(buf.readInt());
        if (job1 == null) throw new DecoderException("Job with id " + buf.readInt() + " does not exist");
        return job1.readData(buf);
    }

    T readData(RegistryFriendlyByteBuf buf);

    Reply<R> process(T data, ServerPlayer player);

    Reply<R> createReply();

    interface Reply<T> {
        void write(RegistryFriendlyByteBuf buf);
        Reply<T> read(RegistryFriendlyByteBuf buf);
        T data();
    }
}
