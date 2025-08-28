package dev.ultreon.devices.api.io;

public record FSResponse<T>(boolean success, int status, T data, String message) {
}
