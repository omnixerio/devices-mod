package dev.ultreon.devices.core.jobs;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SimpleJobBuilder<T, R> {
    private BiConsumer<T, RegistryFriendlyByteBuf> dataSend = (data, buf) -> {};
    private Function<RegistryFriendlyByteBuf, T> dataReceive = buf -> null;
    private BiFunction<T, ServerPlayer, R> reply = (data, player) -> null;
    private BiConsumer<R, RegistryFriendlyByteBuf> replySend = (reply, buf) -> {};
    private Function<RegistryFriendlyByteBuf, R> replyReceive = buf -> null;

    public SimpleJobBuilder<T, R> sendData(BiConsumer<T, RegistryFriendlyByteBuf> send) {
        dataSend = send;
        return this;
    }

    public SimpleJobBuilder<T, R> receiveData(Function<RegistryFriendlyByteBuf, T> receive) {
        dataReceive = receive;
        return this;
    }

    public SimpleJobBuilder<T, R> reply(BiFunction<T, ServerPlayer, R> reply) {
        this.reply = reply;
        return this;
    }

    public SimpleJobBuilder<T, R> sendReply(BiConsumer<R, RegistryFriendlyByteBuf> send) {
        this.replySend = send;
        return this;
    }

    public SimpleJobBuilder<T, R> receiveReply(Function<RegistryFriendlyByteBuf, R> receive) {
        replyReceive = receive;
        return this;
    }

    public SimpleJob<T, R> build() {
        return new SimpleJob<>(reply, replySend, dataSend, dataReceive, replyReceive);
    }
}