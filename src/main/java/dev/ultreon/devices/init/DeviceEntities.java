package dev.ultreon.devices.init;

import dev.ultreon.devices.UltreonDevicesCommon;
import dev.ultreon.devices.entity.SeatEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DeviceEntities {
    private static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(Registries.ENTITY_TYPE, UltreonDevicesCommon.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<SeatEntity>> SEAT = REGISTER.register("seat", () -> EntityType.Builder.<SeatEntity>of(SeatEntity::new, MobCategory.MISC).sized(0.5f, 1.975f).clientTrackingRange(10).noSummon().build(ResourceKey.create(Registries.ENTITY_TYPE, UltreonDevicesCommon.id("seat"))));

    public static void register(IEventBus modBus) {
        REGISTER.register(modBus);
    }
}
