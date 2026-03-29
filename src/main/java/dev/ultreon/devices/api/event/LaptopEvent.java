package dev.ultreon.devices.api.event;

import dev.ultreon.devices.core.Laptop;
import net.neoforged.bus.api.Event;

public class LaptopEvent extends Event {
    private final Laptop laptop;

    public LaptopEvent(Laptop laptop) {
        this.laptop = laptop;
    }

    public Laptop getLaptop() {
        return laptop;
    }
}
