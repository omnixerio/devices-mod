package com.ultreon.devices.api.event;

import com.ultreon.devices.api.TrayItemAdder;
import com.ultreon.devices.core.Laptop;

public class SetupTrayItemsEvent implements LaptopEvent {
    private final Laptop laptop;
    private final TrayItemAdder trayItems;

    public SetupTrayItemsEvent(Laptop laptop, TrayItemAdder trayItems) {
        this.laptop = laptop;
        this.trayItems = trayItems;
    }

    @Override
    public Laptop getLaptop() {
        return laptop;
    }

    public TrayItemAdder getTrayItems() {
        return trayItems;
    }
}
