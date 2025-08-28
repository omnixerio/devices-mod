package com.ultreon.devices.init;

import com.ultreon.devices.Devices;
import com.ultreon.devices.entity.renderer.SeatEntityRenderer;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.registries.Registrar;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;

public class DeviceEntityRenderers {
    static {
        EntityRendererRegistry.register(DeviceEntities.SEAT::get, SeatEntityRenderer::new);
    }

    public static void register() {

    }
}
