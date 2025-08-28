package dev.ultreon.devices.core.network;

import dev.ultreon.mods.xinexlib.event.system.Cancelable;
import org.jetbrains.annotations.NotNull;

public class DeviceConnectEvent implements NetworkEvent, Cancelable {
    private final Router router;
    private final NetworkDevice device;
    private boolean canceled;

    public DeviceConnectEvent(Router router, NetworkDevice networkDevice) {
        this.router = router;
        this.device = networkDevice;
    }

    public @NotNull Router getRouter() {
        return router;
    }

    @Override
    public NetworkDevice getDevice() {
        return device;
    }

    @Override
    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public void cancel() {
        canceled = true;
    }
}
