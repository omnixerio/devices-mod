package dev.ultreon.devices.init;

import dev.ultreon.devices.item.DeviceDataComponents;
import dev.ultreon.devices.network.DevicesCommonNetworker;

/**
 * @author MrCrayfish
 */
public class RegistrationHandler {
    public static void register() {
        DeviceDataComponents.register();
        DeviceEntities.register();
        DeviceBlockEntities.register();
        DeviceBlocks.register();
        DeviceTags.register();
        DeviceItems.register();
        DeviceSounds.register();
        DeviceCreativeTabs.register();

        DevicesCommonNetworker.init();
    }
}
