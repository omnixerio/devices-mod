package dev.ultreon.devices.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ObjectUtils {
    public static <T> void letNotNullOr(@Nullable T object, Consumer<@NotNull T> nonNull, Runnable nullAction) {
        if (object != null) {
            nonNull.accept(object);
        } else {
            nullAction.run();
        }
    }

    public static <T> void letNotNull(@Nullable T object, Consumer<@NotNull T> nonNull) {
        letNotNullOr(object, nonNull, () -> {
        });
    }
}
