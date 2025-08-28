package dev.ultreon.devices.quilt.client;

import dev.ultreon.devices.client.RenderRegistry;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

public class QuiltRenderRegistry extends RenderRegistry {
    @Override
    public void onRegister(Block block, RenderType renderType) {
//        BlockRenderLayerMap.INSTANCE.putBlock(block, renderType);
    }
}
