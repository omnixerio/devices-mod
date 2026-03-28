package com.ultreon.devices.init;

import com.ultreon.devices.Devices;
import com.ultreon.devices.block.entity.*;
import dev.ultreon.mods.xinexlib.registrar.Registrar;
import dev.ultreon.mods.xinexlib.registrar.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

@SuppressWarnings("ConstantConditions")
public class DeviceBlockEntities {
    private static final Registrar<BlockEntityType<?>> REGISTER = Devices.REGISTRIES.get().getRegistrar(Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<PaperBlockEntity>, BlockEntityType<?>> PAPER = REGISTER.register(Devices.id("paper"), () -> new BlockEntityType<>(PaperBlockEntity::new, DeviceBlocks.PAPER.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<LaptopBlockEntity>, BlockEntityType<?>> LAPTOP = REGISTER.register(Devices.id("laptop"), () -> new BlockEntityType<>(LaptopBlockEntity::new, DeviceBlocks.getAllLaptops().toArray(new Block[]{})).build(null));
    public static final RegistrySupplier<BlockEntityType<MacMaxXBlockEntity>, BlockEntityType<?>> MAC_MAX_X = REGISTER.register(Devices.id("mac_max_x"), () -> new BlockEntityType<>(MacMaxXBlockEntity::new, DeviceBlocks.MAC_MAX_X.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<PrinterBlockEntity>, BlockEntityType<?>> PRINTER = REGISTER.register(Devices.id("printer"), () -> new BlockEntityType<>(PrinterBlockEntity::new, DeviceBlocks.getAllPrinters().toArray(new Block[]{})).build(null));
    public static final RegistrySupplier<BlockEntityType<RouterBlockEntity>, BlockEntityType<?>> ROUTER = REGISTER.register(Devices.id("router"), () -> new BlockEntityType<>(RouterBlockEntity::new, DeviceBlocks.getAllRouters().toArray(new Block[]{})).build(null));
    public static final RegistrySupplier<BlockEntityType<OfficeChairBlockEntity>, BlockEntityType<?>> SEAT = REGISTER.register(Devices.id("seat"), () -> new BlockEntityType<>(OfficeChairBlockEntity::new, DeviceBlocks.getAllOfficeChairs().toArray(new Block[]{})).build(null));

    public static void register() {
   //    Marker
    }
}
