package dev.ultreon.devices.init;

import dev.ultreon.devices.entity.renderer.SeatEntityRenderer;
import dev.ultreon.mods.xinexlib.platform.Services;

public class DeviceEntityRenderers {
    static {
        Services.PLATFORM.client().entityRenderers().register(DeviceEntities.SEAT::get, SeatEntityRenderer::new);
    }

    public static void register() {

    }
}
