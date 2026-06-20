package com.ultreon.devices.client.init;

import com.ultreon.devices.entity.renderer.SeatEntityRenderer;
import com.ultreon.devices.init.ModEntities;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;

public class ModEntityRenderers {
    static {
        EntityRendererRegistry.register(ModEntities.SEAT::get, SeatEntityRenderer::new);
    }

    public static void register() {

    }
}
