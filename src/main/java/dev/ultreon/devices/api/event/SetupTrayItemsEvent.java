package dev.ultreon.devices.api.event;

import dev.ultreon.devices.api.TrayItemAdder;
import dev.ultreon.devices.core.Laptop;
import dev.ultreon.devices.object.TrayItem;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;

public interface SetupTrayItemsEvent {
    Event<SetupTrayItemsEvent> EVENT = EventFactory.createArrayBacked(SetupTrayItemsEvent.class, listeners -> (laptop, adder) -> {
        for (var listener : listeners) {
            listener.setupTrayItems(laptop, adder);
        }
    });
    void setupTrayItems(Laptop laptop, TrayItemAdder adder);
}
