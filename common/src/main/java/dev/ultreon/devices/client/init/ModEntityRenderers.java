package dev.ultreon.devices.client.init;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.ultreon.devices.entity.renderer.SeatEntityRenderer;
import dev.ultreon.devices.init.ModEntities;

public class ModEntityRenderers {
    static {
        EntityRendererRegistry.register(ModEntities.SEAT::get, SeatEntityRenderer::new);
    }

    public static void register() {

    }
}
