package dev.ultreon.devices.api.event;

import dev.ultreon.devices.block.entity.RouterBlockEntity;
import dev.ultreon.devices.core.network.NetworkDevice;
import dev.ultreon.devices.core.network.Router;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class RouterBeaconEvent implements RouterEvent {
    private final Router router;
    private final RouterBlockEntity routerBlockEntity;
    private final Map<UUID, NetworkDevice> networkDevices;

    public RouterBeaconEvent(Router router, RouterBlockEntity routerBlockEntity, Map<UUID, NetworkDevice> networkDevices) {
        this.router = router;
        this.routerBlockEntity = routerBlockEntity;
        this.networkDevices = Collections.unmodifiableMap(networkDevices);
    }

    @Override
    public @NotNull Router getRouter() {
        return router;
    }

    public Map<UUID, NetworkDevice> getNetworkDevices() {
        return networkDevices;
    }

    @Override
    public @NotNull RouterBlockEntity getRouterBlockEntity() {
        return routerBlockEntity;
    }
}
