package dev.ultreon.devices;

import dev.ultreon.quantum.GamePlatform;
import dev.ultreon.quantum.util.Env;

import java.util.function.Supplier;

public class EnvExecutor {
    private EnvExecutor() {
        throw new AssertionError("Utility class");
    }

    public static void runInEnv(Env env, Supplier<Runnable> runnable) {
        if (GamePlatform.get().getEnv() == env) {
            runnable.get().run();
        }
    }

    public static <T> T getInEnv(Env env, Supplier<Supplier<T>> supplier) {
        if (GamePlatform.get().getEnv() == env) {
            return supplier.get().get();
        }
        return null;
    }

    public static <T> T getInEnvSpecific(Supplier<Supplier<T>> client, Supplier<Supplier<T>> server) {
        if (GamePlatform.get().getEnv() == Env.CLIENT) {
            return client.get().get();
        } else {
            return server.get().get();
        }
    }
}
