package dev.ultreon.devices.core;

import dev.ultreon.devices.object.AppInfo;

public record PermissionRequest(
        String reason,
        Permission permission,
        AppInfo app
) {

}
