package dev.ultreon.devices.init;

import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.ultreon.devices.OmnixerioDevicesMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;

import java.util.ArrayList;
import java.util.List;

public class ModStats {
    private static final DeferredRegister<ResourceLocation> REGISTER = DeferredRegister.create(OmnixerioDevicesMod.MOD_ID, Registries.CUSTOM_STAT);
    private static final List<Runnable> INIIALIZERS = new ArrayList<>();

    public static final RegistrySupplier<ResourceLocation> LAPTOPS_OPENED = makeCustomStat("laptops_opened", StatFormatter.DEFAULT);
    public static final RegistrySupplier<ResourceLocation> PICTURES_PRINTED = makeCustomStat("pictures_printed", StatFormatter.DEFAULT);

    private static RegistrySupplier<ResourceLocation> makeCustomStat(String string, StatFormatter statFormatter) {
        ResourceLocation resourceLocation = OmnixerioDevicesMod.id(string);
        ModStats.INIIALIZERS.add(() -> Stats.CUSTOM.get(resourceLocation, statFormatter));
        return REGISTER.register(string, () -> resourceLocation);
    }

    public static void register() {
        REGISTER.register();

        if (Platform.isFabric()) {
            init();
        }
    }

    public static void init() {
        INIIALIZERS.forEach(Runnable::run);
    }
}
