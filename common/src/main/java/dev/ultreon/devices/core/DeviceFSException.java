package dev.ultreon.devices.core;

public class DeviceFSException extends RuntimeException {
    public DeviceFSException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeviceFSException(String message) {
        super(message);
    }

    public DeviceFSException(Throwable cause) {
        super(cause);
    }
}
