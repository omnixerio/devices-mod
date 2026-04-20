package dev.ultreon.devices.api;

import dev.ultreon.devices.object.TrayItem;

import java.util.List;

public class TrayItemAdder {
    private final List<TrayItem> trayItems;

    public TrayItemAdder(List<TrayItem> trayItems) {
        this.trayItems = trayItems;
    }

    public void addTrayItem(TrayItem trayItem) {
        trayItems.add(trayItem);
    }
}
