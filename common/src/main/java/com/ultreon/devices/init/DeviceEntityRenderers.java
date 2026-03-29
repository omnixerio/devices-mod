package com.ultreon.devices.init;

import com.ultreon.devices.entity.renderer.SeatEntityRenderer;
import dev.ultreon.mods.xinexlib.platform.XinexPlatform;

public class DeviceEntityRenderers {
    public static void register() {
        XinexPlatform.client().entityRenderers().register(DeviceEntities.SEAT::get, SeatEntityRenderer::new);
    }
}
