package com.ultreon.devices.forge.client;

import com.ultreon.devices.client.RenderRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

public class ForgeRenderRegistry extends RenderRegistry {
    @Override
    public void onRegister(Block block, RenderType renderType) {
        RenderTypeRegistry.register(renderType, block);
    }
}
