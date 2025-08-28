package dev.ultreon.devices.core;

public record PermissionResult(
    boolean granted,
    String reason
) {
    public static final PermissionResult DENIED = new PermissionResult(false, "Permission denied");
    public static final PermissionResult BLOCKED = new PermissionResult(false, "Permission blocked");
    public static final PermissionResult GRANTED = new PermissionResult(true, "No further action required");
}
