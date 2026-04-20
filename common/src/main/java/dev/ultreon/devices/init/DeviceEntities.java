package dev.ultreon.devices.init;

import dev.ultreon.devices.OmnixerioDevicesCommon;
import dev.ultreon.devices.entity.SeatEntity;
import dev.ultreon.devices.platform.Services;
import dev.ultreon.devices.platform.services.RegistrySupplier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

public class DeviceEntities {
    public static final Supplier<EntityType<SeatEntity>> SEAT = register(
            "seat",
            EntityType.Builder.<SeatEntity>of(SeatEntity::new, MobCategory.MISC)
                    .sized(0.5f, 1.975f)
                    .clientTrackingRange(10)
                    .noSummon()
    );

    private static <T extends Entity> RegistrySupplier<EntityType<T>> register(String name, EntityType.Builder<T> builder) {
        return Services.PLATFORM.registerEntityType(name, builder);
    }

    public static void register() {
        OmnixerioDevicesCommon.LOGGER.info("Registering EntityTypes for {}", OmnixerioDevicesCommon.MOD_ID);
    }

    public static void registerAttributes() {

    }
}
