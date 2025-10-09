package dev.ultreon.devices.api.hardware;

import java.util.concurrent.CompletableFuture;

public class RemoteCallback {
    private final int id;
    private final Callbacks.Type<?>[] argTypes;
    private final Callbacks.Type<?>[] returnTypes;

    public RemoteCallback(int id, Callbacks.Type<?>[] argTypes, Callbacks.Type<?>[] returnTypes) {
        this.id = id;
        this.argTypes = argTypes;
        this.returnTypes = returnTypes;
    }

    public int argCount() {
        return argTypes.length;
    }

    public int returnCount() {
        return returnTypes.length;
    }

    public int getId() {
        return id;
    }

    public CompletableFuture<Callbacks.Value<?>[]> call(Callbacks callbacks, Callbacks.Value<?>[] args) {
        for (int i = 0; i < argTypes.length; i++) {
            if (args[i].id() != argTypes[i].id()) throw new IllegalArgumentException("Argument " + i + " is not of type " + argTypes[i]);
        }

        CompletableFuture<Callbacks.Value<?>[]> future = new CompletableFuture<>();
        callbacks.sendCall(future, id, args);
        return future;
    }
}
