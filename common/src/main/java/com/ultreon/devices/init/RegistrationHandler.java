package com.ultreon.devices.init;

import com.ultreon.devices.client.init.ModEntityRenderers;
import com.ultreon.devices.init.tags.ModBlockEntityTags;
import com.ultreon.devices.init.tags.ModBlockTags;
import com.ultreon.devices.init.tags.ModItemTags;
import dev.architectury.utils.EnvExecutor;
import net.fabricmc.api.EnvType;

/**
 * @author MrCrayfish
 */
public class RegistrationHandler {
    public static void register() {
        ModBlockEntities.register();
        ModBlocks.register();
        ModCreativeTabs.register();
        ModDataComponents.register();
        ModEntities.register();
        ModItems.register();
        ModSounds.register();
        ModRecipeSerializers.register();
        EnvExecutor.runInEnv(EnvType.CLIENT, () -> ModEntityRenderers::register);

        // Tags
        ModItemTags.init();
        ModBlockTags.init();
        ModBlockEntityTags.init();
    }
}
