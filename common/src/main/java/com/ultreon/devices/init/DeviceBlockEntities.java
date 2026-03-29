package com.ultreon.devices.init;

import com.ultreon.devices.Devices;
import com.ultreon.devices.block.entity.*;
import dev.ultreon.mods.xinexlib.registrar.Registrar;
import dev.ultreon.mods.xinexlib.registrar.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;

@SuppressWarnings("ConstantConditions")
public class DeviceBlockEntities {
    private static final Registrar<BlockEntityType<?>> REGISTER = Devices.REGISTRIES.get().getRegistrar(Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<PaperBlockEntity>, BlockEntityType<?>> PAPER = REGISTER.register("paper", () -> new BlockEntityType<>(PaperBlockEntity::new, Set.of(DeviceBlocks.PAPER.get())));
    public static final RegistrySupplier<BlockEntityType<LaptopBlockEntity>, BlockEntityType<?>> LAPTOP = REGISTER.register("laptop", () -> new BlockEntityType<>(LaptopBlockEntity::new, Set.of(DeviceBlocks.getAllLaptops().toArray(new Block[]{}))));
    public static final RegistrySupplier<BlockEntityType<MacMaxXBlockEntity>, BlockEntityType<?>> MAC_MAX_X = REGISTER.register("mac_max_x", () -> new BlockEntityType<>(MacMaxXBlockEntity::new, Set.of(DeviceBlocks.MAC_MAX_X.get())));
    public static final RegistrySupplier<BlockEntityType<PrinterBlockEntity>, BlockEntityType<?>> PRINTER = REGISTER.register("printer", () -> new BlockEntityType<>(PrinterBlockEntity::new, Set.of(DeviceBlocks.getAllPrinters().toArray(new Block[]{}))));
    public static final RegistrySupplier<BlockEntityType<RouterBlockEntity>, BlockEntityType<?>> ROUTER = REGISTER.register("router", () -> new BlockEntityType<>(RouterBlockEntity::new, Set.of(DeviceBlocks.getAllRouters().toArray(new Block[]{}))));
    public static final RegistrySupplier<BlockEntityType<OfficeChairBlockEntity>, BlockEntityType<?>> SEAT = REGISTER.register("seat", () -> new BlockEntityType<>(OfficeChairBlockEntity::new, Set.of(DeviceBlocks.getAllOfficeChairs().toArray(new Block[]{}))));

    public static void register() {
   //    Marker
    }
}
