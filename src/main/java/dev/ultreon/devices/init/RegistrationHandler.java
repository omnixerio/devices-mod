package dev.ultreon.devices.init;

import dev.ultreon.devices.item.DeviceDataComponents;
import dev.ultreon.devices.network.DevicesCommonNetworker;
import dev.ultreon.mods.xinexlib.Env;
import dev.ultreon.mods.xinexlib.EnvExecutor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;

/**
 * @author MrCrayfish
 */
public class RegistrationHandler {
    public static void register(IEventBus bus) {
        DeviceDataComponents.register(bus);
        DeviceEntities.register(bus);
        DeviceBlockEntities.register(bus);
        DeviceBlocks.register(bus);
        DeviceTags.register(bus);
        DeviceItems.register(bus);
        DeviceSounds.register(bus);
        DeviceCreativeTabs.register(bus);

        DevicesCommonNetworker.init();

        if (FMLEnvironment.getDist().isClient()) {
            DeviceEntityRenderers.register(bus);
        }
    }
}
