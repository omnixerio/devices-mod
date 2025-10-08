package dev.ultreon.devices.event;

import dev.ultreon.devices.Devices;
import net.minecraft.resources.ResourceLocation;

public interface InitializationEvent extends DevicesModEvent {
    record AppRegistrationEvent(Devices devices) implements InitializationEvent {
        public void registerApplication(ResourceLocation appId, Devices.ApplicationSupplier app) {
            Devices.getInstance().registerApplication(appId, app);
        }
    }
}
