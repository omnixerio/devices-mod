package dev.ultreon.devices.api.event;

import dev.ultreon.devices.block.entity.computer.LaptopBlockEntity;
import org.jetbrains.annotations.NotNull;

public interface LaptopEvent extends ComputerEvent {
    @Override
    @NotNull LaptopBlockEntity getComputerBlockEntity();
}
