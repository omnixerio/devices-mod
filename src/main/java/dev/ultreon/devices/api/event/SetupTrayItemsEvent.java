package dev.ultreon.devices.api.event;

import dev.ultreon.devices.core.Laptop;
import dev.ultreon.devices.object.TrayItem;

import java.util.Collections;
import java.util.List;

public class SetupTrayItemsEvent extends LaptopEvent {
    private final List<TrayItem> trayItems;

    public SetupTrayItemsEvent(Laptop laptop, List<TrayItem> trayItems) {
        super(laptop);
        this.trayItems = trayItems;
    }

    public void addTrayItem(TrayItem trayItem) {
        trayItems.add(trayItem);
    }

    public List<TrayItem> getTrayItems() {
        return Collections.unmodifiableList(trayItems);
    }
}
