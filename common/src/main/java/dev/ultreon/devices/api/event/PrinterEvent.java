package dev.ultreon.devices.api.event;

import dev.ultreon.devices.api.print.IPrint;
import dev.ultreon.devices.block.entity.DeviceBlockEntity;
import dev.ultreon.devices.block.entity.PrinterBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PrinterEvent extends DeviceEvent {
    @NotNull PrinterBlockEntity getPrinter();

    @Nullable IPrint getPrint();

    @Override
    default @NotNull DeviceBlockEntity getDeviceBlockEntity() {
        return getPrinter();
    }
}
