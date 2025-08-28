package dev.ultreon.devices.api.event;

import dev.ultreon.devices.block.entity.DeviceBlockEntity;
import dev.ultreon.devices.block.entity.RouterBlockEntity;
import dev.ultreon.devices.core.network.Router;
import org.jetbrains.annotations.NotNull;

public interface RouterEvent extends DeviceEvent {
    @NotNull Router getRouter();

    @NotNull RouterBlockEntity getRouterBlockEntity();

    default @NotNull DeviceBlockEntity getDeviceBlockEntity() {
        return getRouterBlockEntity();
    }
}
