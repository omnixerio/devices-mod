package dev.ultreon.devices.init;

import dev.ultreon.devices.Devices;
import dev.ultreon.devices.block.entity.*;
import dev.architectury.registry.registries.Registrar;
import dev.ultreon.libs.registries.v0.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

@SuppressWarnings("ConstantConditions")
public class DeviceBlockEntities {
    private static final Registrar<BlockEntityType<?>> REGISTER = Devices.REGISTRIES.get().get(Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<PaperBlockEntity>> PAPER = REGISTER.register(Devices.id("paper"), () -> BlockEntityType.Builder.of(PaperBlockEntity::new, DeviceBlocks.PAPER.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<LaptopBlockEntity>> LAPTOP = REGISTER.register(Devices.id("laptop"), () -> BlockEntityType.Builder.of(LaptopBlockEntity::new, DeviceBlocks.getAllLaptops().toArray(new Block[]{})).build(null));
    public static final RegistrySupplier<BlockEntityType<MacMaxXBlockEntity>> MAC_MAX_X = REGISTER.register(Devices.id("mac_max_x"), () -> BlockEntityType.Builder.of(MacMaxXBlockEntity::new, DeviceBlocks.MAC_MAX_X.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<PrinterBlockEntity>> PRINTER = REGISTER.register(Devices.id("printer"), () -> BlockEntityType.Builder.of(PrinterBlockEntity::new, DeviceBlocks.getAllPrinters().toArray(new Block[]{})).build(null));
    public static final RegistrySupplier<BlockEntityType<RouterBlockEntity>> ROUTER = REGISTER.register(Devices.id("router"), () -> BlockEntityType.Builder.of(RouterBlockEntity::new, DeviceBlocks.getAllRouters().toArray(new Block[]{})).build(null));
    public static final RegistrySupplier<BlockEntityType<OfficeChairBlockEntity>> SEAT = REGISTER.register(Devices.id("seat"), () -> BlockEntityType.Builder.of(OfficeChairBlockEntity::new, DeviceBlocks.getAllOfficeChairs().toArray(new Block[]{})).build(null));

    public static void register() {
   //    Marker
    }
}
