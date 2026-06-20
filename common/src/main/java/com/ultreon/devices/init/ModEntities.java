package com.ultreon.devices.init;

import com.ultreon.devices.OmnixerioDevicesMod;
import com.ultreon.devices.entity.SeatEntity;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

public class ModEntities {
    private static final Registrar<EntityType<?>> REGISTER = OmnixerioDevicesMod.REGISTRIES.get().get(Registries.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<SeatEntity>> SEAT = register("seat", () -> EntityType.Builder.<SeatEntity>of(SeatEntity::new, MobCategory.MISC).sized(0.5f, 1.975f).clientTrackingRange(10).noSummon().build(OmnixerioDevicesMod.id("seat").toString()));

    private static <T extends EntityType<?>> RegistrySupplier<T> register(String id, Supplier<T> supplier) {
        return REGISTER.register(OmnixerioDevicesMod.id(id), supplier);
    }

    public static void register() {

    }
}
