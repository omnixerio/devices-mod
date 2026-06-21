package dev.ultreon.devices.init;

import dev.architectury.utils.EnvExecutor;
import dev.ultreon.devices.client.init.ModEntityRenderers;
import dev.ultreon.devices.init.tags.ModBlockEntityTags;
import dev.ultreon.devices.init.tags.ModBlockTags;
import dev.ultreon.devices.init.tags.ModItemTags;
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
        ModStats.register();
        ModSounds.register();
        ModRecipeSerializers.register();
        EnvExecutor.runInEnv(EnvType.CLIENT, () -> ModEntityRenderers::register);

        // Tags
        ModItemTags.init();
        ModBlockTags.init();
        ModBlockEntityTags.init();
    }
}
