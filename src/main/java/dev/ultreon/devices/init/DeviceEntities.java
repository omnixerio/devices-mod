package dev.ultreon.devices.init;

import dev.ultreon.devices.OmnixerioDevicesCommon;
import dev.ultreon.devices.entity.SeatEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class DeviceEntities {
    public static final EntityType<SeatEntity> SEAT = register(
            "seat",
            EntityType.Builder.<SeatEntity>of(SeatEntity::new, MobCategory.MISC)
                    .sized(0.5f, 1.975f)
                    .clientTrackingRange(10)
                    .noSummon()
    );

    private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(OmnixerioDevicesCommon.MOD_ID, name));
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
    }

    public static void register() {
        OmnixerioDevicesCommon.LOGGER.info("Registering EntityTypes for {}", OmnixerioDevicesCommon.MOD_ID);
    }

    public static void registerAttributes() {

    }
}
