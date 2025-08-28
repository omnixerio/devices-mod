<<<<<<<< HEAD:fabric/src/main/java/dev/ultreon/devices/fabric/client/FabricRenderRegistry.java
package com.ultreon.devices.fabric.client;
========
package dev.ultreon.devices.client;
>>>>>>>> origin/wip/port-xinexlib:fabric/src/main/java/dev/ultreon/devices/client/FabricRenderRegistry.java

import com.ultreon.devices.client.RenderRegistry;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

public class FabricRenderRegistry extends RenderRegistry {
    @Override
    public void onRegister(Block block, RenderType renderType) {
        BlockRenderLayerMap.INSTANCE.putBlock(block, renderType);
    }
}
