package com.ultreon.devices.init;

import com.ultreon.devices.item.DeviceDataComponents;
import com.ultreon.devices.network.DevicesNetworker;
import dev.ultreon.mods.xinexlib.Env;
import dev.ultreon.mods.xinexlib.EnvExecutor;

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

        DevicesNetworker.init();

        EnvExecutor.runInEnv(Env.CLIENT, () -> DeviceEntityRenderers::register);
    }
}
