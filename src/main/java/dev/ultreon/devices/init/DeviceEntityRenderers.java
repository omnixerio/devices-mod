package dev.ultreon.devices.init;

import dev.ultreon.devices.entity.renderer.SeatEntityRenderer;
import dev.ultreon.mods.xinexlib.platform.XinexPlatform;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class DeviceEntityRenderers {
    public static void register(IEventBus modBus) {
        modBus.addListener(EntityRenderersEvent.RegisterRenderers.class, DeviceEntityRenderers::onRegisrterRenderers);
    }

    private static void onRegisrterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(DeviceEntities.SEAT.get(), SeatEntityRenderer::new);
    }
}
