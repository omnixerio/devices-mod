package dev.ultreon.devices.init;

import dev.ultreon.devices.entity.renderer.SeatEntityRenderer;
import dev.ultreon.mods.xinexlib.platform.XinexPlatform;

public class DeviceEntityRenderers {
    static {
        XinexPlatform.client().entityRenderers().register(DeviceEntities.SEAT::get, SeatEntityRenderer::new);
    }

    public static void register() {

    }
}
