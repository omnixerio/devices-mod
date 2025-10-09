package dev.ultreon.devices.api.device;

public interface RemoteDevice {
    RemoteDeviceOrigin getOrigin();

    void close();
}
