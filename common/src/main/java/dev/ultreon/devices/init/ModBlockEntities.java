package dev.ultreon.devices.init;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.ultreon.devices.OmnixerioDevicesMod;
import dev.ultreon.devices.block.entity.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

@SuppressWarnings("ConstantConditions")
public class ModBlockEntities {
    private static final Registrar<BlockEntityType<?>> REGISTER = OmnixerioDevicesMod.REGISTRIES.get().get(Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<PaperBlockEntity>> PAPER = REGISTER.register(OmnixerioDevicesMod.id("paper"), () -> BlockEntityType.Builder.of(PaperBlockEntity::new, ModBlocks.PAPER.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<LaptopBlockEntity>> LAPTOP = REGISTER.register(OmnixerioDevicesMod.id("laptop"), () -> BlockEntityType.Builder.of(LaptopBlockEntity::new, ModBlocks.getAllLaptops().toArray(new Block[]{})).build(null));
    public static final RegistrySupplier<BlockEntityType<MacMaxXBlockEntity>> MAC_MAX_X = REGISTER.register(OmnixerioDevicesMod.id("mac_max_x"), () -> BlockEntityType.Builder.of(MacMaxXBlockEntity::new, ModBlocks.MAC_MAX_X.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<PrinterBlockEntity>> PRINTER = REGISTER.register(OmnixerioDevicesMod.id("printer"), () -> BlockEntityType.Builder.of(PrinterBlockEntity::new, ModBlocks.getAllPrinters().toArray(new Block[]{})).build(null));
    public static final RegistrySupplier<BlockEntityType<RouterBlockEntity>> ROUTER = REGISTER.register(OmnixerioDevicesMod.id("router"), () -> BlockEntityType.Builder.of(RouterBlockEntity::new, ModBlocks.getAllRouters().toArray(new Block[]{})).build(null));
    public static final RegistrySupplier<BlockEntityType<OfficeChairBlockEntity>> SEAT = REGISTER.register(OmnixerioDevicesMod.id("seat"), () -> BlockEntityType.Builder.of(OfficeChairBlockEntity::new, ModBlocks.getAllOfficeChairs().toArray(new Block[]{})).build(null));
    public static final RegistrySupplier<BlockEntityType<ClockBlockEntity>> CLOCK = REGISTER.register(OmnixerioDevicesMod.id("clock"), () -> BlockEntityType.Builder.of(ClockBlockEntity::new, ModBlocks.getAllClocks().toArray(new Block[]{})).build(null));

    public static void register() {
   //    Marker
    }
}
