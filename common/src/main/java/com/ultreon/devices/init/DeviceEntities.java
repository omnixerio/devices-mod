package com.ultreon.devices.init;

import com.ultreon.devices.Devices;
import com.ultreon.devices.entity.SeatEntity;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class DeviceEntities {
    private static final Registrar<EntityType<?>> REGISTER = Devices.REGISTRIES.get().get(Registries.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<SeatEntity>> SEAT = REGISTER.register(Devices.id("seat"), () -> EntityType.Builder.<SeatEntity>of(SeatEntity::new, MobCategory.MISC).sized(0.5f, 1.975f).clientTrackingRange(10).noSummon().build(Devices.id("seat").toString()));

    public static void register() {

    }
}
