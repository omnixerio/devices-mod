package dev.ultreon.devices.api.event.client;

import dev.ultreon.devices.api.TrayItemAdder;
import dev.ultreon.devices.api.app.OperatingSystem;

public class ComputerTrayItemsEvent implements ComputerUIEvent {
    private final OperatingSystem computerScreen;
    private final TrayItemAdder trayItems;

    public ComputerTrayItemsEvent(OperatingSystem computerScreen, TrayItemAdder trayItems) {
        this.computerScreen = computerScreen;
        this.trayItems = trayItems;
    }

    @Override
    public OperatingSystem getOperatingSystem() {
        return computerScreen;
    }

    public TrayItemAdder getTrayItems() {
        return trayItems;
    }
}
