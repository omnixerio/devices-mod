package dev.ultreon.devices.api.event;

import dev.ultreon.devices.api.TrayItemAdder;
import dev.ultreon.devices.core.Laptop;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;

public interface LaptopEvent {
    Event<SetupTrayItems> SETUP_TRAY_ITEMS = EventFactory.createLoop();

    interface SetupTrayItems extends LaptopEvent {
        void setupTrayItems(Laptop laptop, TrayItemAdder trayItems);
    }
}
