package dev.ultreon.devices.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.ServiceLoader;

@ApiStatus.NonExtendable
public abstract class RenderRegistry {
    private static final ServiceLoader<RenderRegistry> SERVICE_LOADER = ServiceLoader.load(RenderRegistry.class);
    private static final List<RenderRegistry> RENDER_REGISTRIES;

    static {
        RENDER_REGISTRIES = SERVICE_LOADER.stream().map(ServiceLoader.Provider::get).toList();
    }
    
    public abstract void onRegister(Block block, RenderType renderType);

    public static void register(Block block, RenderType renderType) {
        for (RenderRegistry registry : RENDER_REGISTRIES) {
            registry.onRegister(block, renderType);
        }
    }
}
