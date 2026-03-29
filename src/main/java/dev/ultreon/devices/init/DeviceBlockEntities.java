package dev.ultreon.devices.init;

import dev.ultreon.devices.UltreonDevicesCommon;
import dev.ultreon.devices.block.entity.*;
import dev.ultreon.devices.block.entity.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;

@SuppressWarnings("ConstantConditions")
public class DeviceBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, UltreonDevicesCommon.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PaperBlockEntity>> PAPER = REGISTER.register("paper", () -> new BlockEntityType<>(PaperBlockEntity::new, Set.of(DeviceBlocks.PAPER.get())));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LaptopBlockEntity>> LAPTOP = REGISTER.register("laptop", () -> new BlockEntityType<>(LaptopBlockEntity::new, Set.of(DeviceBlocks.getAllLaptops().toArray(new Block[]{}))));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MacMaxXBlockEntity>> MAC_MAX_X = REGISTER.register("mac_max_x", () -> new BlockEntityType<>(MacMaxXBlockEntity::new, Set.of(DeviceBlocks.MAC_MAX_X.get())));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PrinterBlockEntity>> PRINTER = REGISTER.register("printer", () -> new BlockEntityType<>(PrinterBlockEntity::new, Set.of(DeviceBlocks.getAllPrinters().toArray(new Block[]{}))));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RouterBlockEntity>> ROUTER = REGISTER.register("router", () -> new BlockEntityType<>(RouterBlockEntity::new, Set.of(DeviceBlocks.getAllRouters().toArray(new Block[]{}))));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OfficeChairBlockEntity>> SEAT = REGISTER.register("seat", () -> new BlockEntityType<>(OfficeChairBlockEntity::new, Set.of(DeviceBlocks.getAllOfficeChairs().toArray(new Block[]{}))));

    public static void register() {
   //    Marker
    }
}
