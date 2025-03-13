package dev.ultreon.devices.object;

public record Result<T>(
        String message,
        T data,
        boolean success
) {
    public static <T> Result<T> success(T data) {
        return new Result<>("Success", data, true);
    }

    public static <T> Result<T> failure(String message) {
        return new Result<>(message, null, false);
    }
}
