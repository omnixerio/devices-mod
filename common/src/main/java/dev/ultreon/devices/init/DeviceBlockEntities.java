package dev.ultreon.devices.init;

import dev.ultreon.devices.OmnixerioDevicesCommon;
import dev.ultreon.devices.block.entity.*;
import dev.ultreon.devices.platform.Services;
import dev.ultreon.devices.platform.services.BlockEntitySupplier;
import dev.ultreon.devices.platform.services.RegistrySupplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;

@SuppressWarnings({"ConstantConditions", "unchecked", "SuspiciousToArrayCall"})
public class DeviceBlockEntities {
    public static final RegistrySupplier<BlockEntityType<PaperBlockEntity>> PAPER = register("paper", PaperBlockEntity::new, DeviceBlocks.PAPER);
    public static final RegistrySupplier<BlockEntityType<LaptopBlockEntity>> LAPTOP = register("laptop", LaptopBlockEntity::new, DeviceBlocks.getAllLaptops().toArray(RegistrySupplier[]::new));
    public static final RegistrySupplier<BlockEntityType<MacMaxXBlockEntity>> MAC_MAX_X = register("mac_max_x", MacMaxXBlockEntity::new, DeviceBlocks.MAC_MAX_X);
    public static final RegistrySupplier<BlockEntityType<PrinterBlockEntity>> PRINTER = register("printer", PrinterBlockEntity::new, DeviceBlocks.getAllPrinters().toArray(RegistrySupplier[]::new));
    public static final RegistrySupplier<BlockEntityType<RouterBlockEntity>> ROUTER = register("router", RouterBlockEntity::new, DeviceBlocks.getAllRouters().toArray(RegistrySupplier[]::new));
    public static final RegistrySupplier<BlockEntityType<OfficeChairBlockEntity>> OFFICE_CHAIR = register("office_chair", OfficeChairBlockEntity::new, DeviceBlocks.getAllOfficeChairs().toArray(RegistrySupplier[]::new));

    @SafeVarargs
    private static <T extends BlockEntity> RegistrySupplier<BlockEntityType<T>> register(
            String name,
            BlockEntitySupplier<T> entityFactory,
            RegistrySupplier<? extends Block>... blocks
    ) {
        return Services.PLATFORM.registerBlockEntity(name, entityFactory, Set.of(blocks));
    }

    public static void register() {
        OmnixerioDevicesCommon.LOGGER.info("Registering BlockEntityTypes for {}", OmnixerioDevicesCommon.MOD_ID);
    }
}
