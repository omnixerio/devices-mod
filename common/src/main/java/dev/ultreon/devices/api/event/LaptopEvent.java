package dev.ultreon.devices.api.event;

import dev.ultreon.devices.block.entity.LaptopBlockEntity;
import org.jetbrains.annotations.NotNull;

public interface LaptopEvent extends ComputerEvent {
    @Override
    @NotNull LaptopBlockEntity getComputerBlockEntity();
}
