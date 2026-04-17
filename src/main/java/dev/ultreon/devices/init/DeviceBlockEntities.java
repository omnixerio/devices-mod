package dev.ultreon.devices.init;

import dev.ultreon.devices.OmnixerioDevicesCommon;
import dev.ultreon.devices.block.entity.*;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

@SuppressWarnings("ConstantConditions")
public class DeviceBlockEntities {
    public static final BlockEntityType<PaperBlockEntity> PAPER = register("paper", PaperBlockEntity::new, DeviceBlocks.PAPER);
    public static final BlockEntityType<LaptopBlockEntity> LAPTOP = register("laptop", LaptopBlockEntity::new, DeviceBlocks.getAllLaptops().toArray(Block[]::new));
    public static final BlockEntityType<MacMaxXBlockEntity> MAC_MAX_X = register("mac_max_x", MacMaxXBlockEntity::new, DeviceBlocks.MAC_MAX_X);
    public static final BlockEntityType<PrinterBlockEntity> PRINTER = register("printer", PrinterBlockEntity::new, DeviceBlocks.getAllPrinters().toArray(new Block[]{}));
    public static final BlockEntityType<RouterBlockEntity> ROUTER = register("router", RouterBlockEntity::new, DeviceBlocks.getAllRouters().toArray(new Block[]{}));
    public static final BlockEntityType<OfficeChairBlockEntity> SEAT = register("seat", OfficeChairBlockEntity::new, DeviceBlocks.getAllOfficeChairs().toArray(new Block[]{}));

    private static <T extends BlockEntity> BlockEntityType<T> register(
            String name,
            FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
            Block... blocks
    ) {
        Identifier id = Identifier.fromNamespaceAndPath(OmnixerioDevicesCommon.MOD_ID, name);
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
    }

    public static void register() {
        OmnixerioDevicesCommon.LOGGER.info("Registering BlockEntityTypes for {}", OmnixerioDevicesCommon.MOD_ID);
    }
}
