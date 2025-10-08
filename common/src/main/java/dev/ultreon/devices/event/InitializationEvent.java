package dev.ultreon.devices.event;

import dev.ultreon.devices.UltreonDevices;
import net.minecraft.resources.ResourceLocation;

public interface InitializationEvent extends DevicesModEvent {
    record AppRegistrationEvent(UltreonDevices devices) implements InitializationEvent {
        public void registerApplication(ResourceLocation appId, UltreonDevices.ApplicationSupplier app) {
            UltreonDevices.getInstance().registerApplication(appId, app);
        }
    }
}
