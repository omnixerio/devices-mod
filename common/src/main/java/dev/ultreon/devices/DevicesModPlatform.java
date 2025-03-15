package dev.ultreon.devices;

import dev.ultreon.devices.object.AppInfo;

public interface DevicesModPlatform {
    String getVersion();

    void updateIcon(AppInfo info, int iconU, int iconV);
}
