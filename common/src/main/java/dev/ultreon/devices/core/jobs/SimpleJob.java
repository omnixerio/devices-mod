package dev.ultreon.devices.core.jobs;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SimpleJob<T, R> implements Job<T, R> {
    private final BiFunction<T, ServerPlayer, R> reply;
    private final BiConsumer<T, RegistryFriendlyByteBuf> dataSend;
    private final Function<RegistryFriendlyByteBuf, T> dataReceive;
    private final BiConsumer<R, RegistryFriendlyByteBuf> replySend;
    private final Function<RegistryFriendlyByteBuf, R> replyReceive;

    SimpleJob(BiFunction<T, ServerPlayer, R> reply, BiConsumer<R, RegistryFriendlyByteBuf> replySend, BiConsumer<T, RegistryFriendlyByteBuf> dataSend, Function<RegistryFriendlyByteBuf, T> dataReceive, Function<RegistryFriendlyByteBuf, R> replyReceive) {
        this.reply = reply;
        this.replySend = replySend;
        this.dataSend = dataSend;
        this.dataReceive = dataReceive;
        this.replyReceive = replyReceive;
    }

    @Override
    public void writeData(RegistryFriendlyByteBuf buf, T data) {
        dataSend.accept(data, buf);
    }

    @Override
    public T readData(RegistryFriendlyByteBuf buf) {
        return dataReceive.apply(buf);
    }

    @Override
    public Reply<R> process(T data, ServerPlayer player) {
        return new Reply<>(reply.apply(data, player), this);
    }

    @Override
    public Reply<R> createReply() {
        return new Reply<>(this);
    }

    public static class Reply<T> implements Job.Reply<T> {
        private T data;
        private final SimpleJob<?, T> job;

        public Reply(SimpleJob<?, T> job) {
            this.job = job;
        }

        public Reply(T data, SimpleJob<?, T> job) {
            this.data = data;
            this.job = job;
        }

        @Override
        public void write(RegistryFriendlyByteBuf buf) {
            if (data != null) job.replySend.accept(data, buf);
        }

        @Override
        public Reply<T> read(RegistryFriendlyByteBuf buf) {
            if (data == null) data = job.replyReceive.apply(buf);
            return this;
        }

        @Override
        public T data() {
            return data;
        }
    }
}
