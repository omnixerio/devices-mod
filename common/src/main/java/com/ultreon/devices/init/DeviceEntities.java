package com.ultreon.devices.init;

import com.ultreon.devices.Devices;
import com.ultreon.devices.entity.SeatEntity;
import dev.ultreon.mods.xinexlib.registrar.Registrar;
import dev.ultreon.mods.xinexlib.registrar.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class DeviceEntities {
    private static final Registrar<EntityType<?>> REGISTER = Devices.REGISTRIES.get().getRegistrar(Registries.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<SeatEntity>, EntityType<?>> SEAT = REGISTER.register("seat", () -> EntityType.Builder.<SeatEntity>of(SeatEntity::new, MobCategory.MISC).sized(0.5f, 1.975f).clientTrackingRange(10).noSummon().build(ResourceKey.create(Registries.ENTITY_TYPE, Devices.id("seat"))));

    public static void register() {

    }
}
