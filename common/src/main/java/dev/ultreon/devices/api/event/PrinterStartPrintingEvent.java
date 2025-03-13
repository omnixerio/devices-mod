package dev.ultreon.devices.api.event;

import dev.ultreon.devices.api.print.IPrint;
import dev.ultreon.devices.block.entity.PrinterBlockEntity;
import org.jetbrains.annotations.NotNull;

public class PrinterStartPrintingEvent implements PrinterEvent {
    private final PrinterBlockEntity printer;
    private final IPrint print;

    public PrinterStartPrintingEvent(@NotNull PrinterBlockEntity printer,
                                     @NotNull IPrint print) {
        this.printer = printer;
        this.print = print;
    }

    @Override
    public @NotNull PrinterBlockEntity getPrinter() {
        return printer;
    }

    public @NotNull IPrint getPrint() {
        return print;
    }
}
