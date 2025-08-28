<<<<<<<< HEAD:forge/src/main/java/com/ultreon/devices/forge/client/ForgeRenderRegistry.java
package com.ultreon.devices.forge.client;
========
package dev.ultreon.devices.forge.client;
>>>>>>>> origin/wip/port-xinexlib:forge/src/main/java/dev/ultreon/devices/forge/client/ForgeRenderRegistry.java

import dev.ultreon.devices.client.RenderRegistry;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

public class ForgeRenderRegistry extends RenderRegistry {
    @Override
    public void onRegister(Block block, RenderType renderType) {
        ItemBlockRenderTypes.setRenderLayer(block, renderType);
    }
}
