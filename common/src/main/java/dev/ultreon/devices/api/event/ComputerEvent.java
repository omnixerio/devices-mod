package dev.ultreon.devices.api.event;

import dev.ultreon.devices.block.entity.computer.ComputerBlockEntity;
import dev.ultreon.devices.block.entity.DeviceBlockEntity;
import org.jetbrains.annotations.NotNull;

public interface ComputerEvent extends DeviceEvent {
    @NotNull ComputerBlockEntity getComputerBlockEntity();

    @Override
    default @NotNull DeviceBlockEntity getDeviceBlockEntity() {
        return getComputerBlockEntity();
    }
}
