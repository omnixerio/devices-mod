package dev.ultreon.devices.init;

import dev.ultreon.devices.Devices;
import dev.ultreon.devices.entity.Seat;
import dev.ultreon.mods.xinexlib.registrar.Registrar;
import dev.ultreon.mods.xinexlib.registrar.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class DeviceEntities {
    private static final Registrar<EntityType<?>> REGISTER = Devices.REGISTRIES.get().getRegistrar(Registries.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<Seat>, EntityType<?>> SEAT = REGISTER.register("seat", () -> EntityType.Builder.<Seat>of(Seat::new, MobCategory.MISC).sized(0.5f, 1.975f).clientTrackingRange(10).noSummon().build(Devices.id("seat").toString()));

    public static void register() {
        REGISTER.load();
    }
}
