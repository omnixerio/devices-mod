package dev.ultreon.devices.init;

import dev.ultreon.devices.UltreonDevices;
import dev.ultreon.devices.block.entity.*;
import dev.ultreon.devices.block.entity.computer.LaptopBlockEntity;
import dev.ultreon.devices.block.entity.computer.MacMaxXBlockEntity;
import dev.ultreon.mods.xinexlib.registrar.Registrar;
import dev.ultreon.mods.xinexlib.registrar.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

@SuppressWarnings("ConstantConditions")
public class DeviceBlockEntities {
    private static final Registrar<BlockEntityType<?>> REGISTER = UltreonDevices.REGISTRIES.get().getRegistrar(Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<PaperBlockEntity>, BlockEntityType<?>> PAPER = REGISTER.register("paper", () -> BlockEntityType.Builder.of(PaperBlockEntity::new, DeviceBlocks.PAPER.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<LaptopBlockEntity>, BlockEntityType<?>> LAPTOP = REGISTER.register("laptop", () -> BlockEntityType.Builder.of(LaptopBlockEntity::new, DeviceBlocks.getAllLaptops().toArray(new Block[]{})).build(null));
    public static final RegistrySupplier<BlockEntityType<MacMaxXBlockEntity>, BlockEntityType<?>> MAC_MAX_X = REGISTER.register("mac_max_x", () -> BlockEntityType.Builder.of(MacMaxXBlockEntity::new, DeviceBlocks.MAC_MAX_X.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<PrinterBlockEntity>, BlockEntityType<?>> PRINTER = REGISTER.register("printer", () -> BlockEntityType.Builder.of(PrinterBlockEntity::new, DeviceBlocks.getAllPrinters().toArray(new Block[]{})).build(null));
    public static final RegistrySupplier<BlockEntityType<RouterBlockEntity>, BlockEntityType<?>> ROUTER = REGISTER.register("router", () -> BlockEntityType.Builder.of(RouterBlockEntity::new, DeviceBlocks.getAllRouters().toArray(new Block[]{})).build(null));
    public static final RegistrySupplier<BlockEntityType<OfficeChairBlockEntity>, BlockEntityType<?>> SEAT = REGISTER.register("seat", () -> BlockEntityType.Builder.of(OfficeChairBlockEntity::new, DeviceBlocks.getAllOfficeChairs().toArray(new Block[]{})).build(null));

    public static void register() {
        REGISTER.load();
    }
}
