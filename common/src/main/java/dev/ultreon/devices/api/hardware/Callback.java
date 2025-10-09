package dev.ultreon.devices.api.hardware;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Callback {
    Object[] call(Object... args);
    Callbacks.Type<?>[] getArgumentTypes();
    Callbacks.Type<?>[] getReturnTypes();

    static <T> Callback createSupplier(Callbacks.Type<T> returnType, Supplier<T> supplier) {
        return new Callback() {
            @Override
            public Object[] call(Object... args) {
                return new Object[] {returnType.cast(supplier.get())};
            }

            @Override
            public Callbacks.Type<?>[] getArgumentTypes() {
                return new Callbacks.Type[] {};
            }

            @Override
            public Callbacks.Type<?>[] getReturnTypes() {
                return new Callbacks.Type[] {returnType};
            }
        };
    }

    static Callback createRunnable(Runnable runnable) {
        return new Callback() {
            @Override
            public Object[] call(Object... args) {
                runnable.run();
                return new Object[0];
            }

            @Override
            public Callbacks.Type<?>[] getArgumentTypes() {
                return new Callbacks.Type[0];
            }

            @Override
            public Callbacks.Type<?>[] getReturnTypes() {
                return new Callbacks.Type[0];
            }
        };
    }

    static <T> Callback createConsumer(Callbacks.Type<T> argType, Consumer<T> consumer) {
        return new Callback() {
            @Override
            public Object[] call(Object... args) {
                consumer.accept(argType.cast(args[0]));
                return new Object[0];
            }

            @Override
            public Callbacks.Type<?>[] getArgumentTypes() {
                return new Callbacks.Type[] {argType};
            }

            @Override
            public Callbacks.Type<?>[] getReturnTypes() {
                return new Callbacks.Type[] {};
            }
        };
    }

    static <T, U> Callback createBiConsumer(Callbacks.Type<T> argType1, Callbacks.Type<U> argType2, BiConsumer<T, U> consumer) {
        return new Callback() {
            @Override
            public Object[] call(Object... args) {
                consumer.accept(argType1.cast(args[0]), argType2.cast(args[1]));
                return new Object[0];
            }

            @Override
            public Callbacks.Type<?>[] getArgumentTypes() {
                return new Callbacks.Type[] {argType1, argType2};
            }

            @Override
            public Callbacks.Type<?>[] getReturnTypes() {
                return new Callbacks.Type[] {};
            }
        };
    }

    static <T, R> Callback createFunction(Callbacks.Type<T> argType, Callbacks.Type<R> returnType, java.util.function.Function<T, R> function) {
        return new Callback() {
            @Override
            public Object[] call(Object... args) {
                return new Object[] {returnType.cast(function.apply(argType.cast(args[0])))};
            }

            @Override
            public Callbacks.Type<?>[] getArgumentTypes() {
                return new Callbacks.Type[] {argType};
            }

            @Override
            public Callbacks.Type<?>[] getReturnTypes() {
                return new Callbacks.Type[] {returnType};
            }
        };
    }

    static <T, U, R> Callback createBiFunction(Callbacks.Type<T> argType1, Callbacks.Type<U> argType2, Callbacks.Type<R> returnType, java.util.function.BiFunction<T, U, R> function) {
        return new Callback() {
            @Override
            public Object[] call(Object... args) {
                return new Object[] {returnType.cast(function.apply(argType1.cast(args[0]), argType2.cast(args[1])))};
            }

            @Override
            public Callbacks.Type<?>[] getArgumentTypes() {
                return new Callbacks.Type[] {argType1, argType2};
            }

            @Override
            public Callbacks.Type<?>[] getReturnTypes() {
                return new Callbacks.Type[] {returnType};
            }
        };
    }
}
