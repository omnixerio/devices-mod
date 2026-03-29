package dev.ultreon.devices.init;

import dev.ultreon.devices.UltreonDevicesCommon;
import dev.ultreon.devices.block.*;
import dev.ultreon.devices.util.DyeableRegistration;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.stream.Stream;

public class DeviceBlocks {
    private static final DeferredRegister<Block> REGISTER = DeferredRegister.create(Registries.BLOCK, UltreonDevicesCommon.MOD_ID);

    public static void register() {
    }

    public static final DyeableRegistration<Block> LAPTOPS = new DyeableRegistration<>(REGISTER) {
        public DeferredHolder<Block, LaptopBlock> register(DeferredRegister<Block> registrar, DyeColor color) {
            return registrar.register(color.getName() + "_laptop", () -> new LaptopBlock(color, UltreonDevicesCommon.id(color.getName() + "_laptop")));
        }
    };

    public static final DeferredHolder<Block, MacMaxXBlock> MAC_MAX_X = REGISTER.register("mac_max_x", () -> new MacMaxXBlock(BlockBehaviour.Properties.of().strength(2.5f).noOcclusion()));
    public static final DeferredHolder<Block, MacMaxXBlockPart> MAC_MAX_X_PART = REGISTER.register("mac_max_x_part", () -> new MacMaxXBlockPart(BlockBehaviour.Properties.of().strength(2.5f).noOcclusion()));
    public static final DeferredHolder<Block, PaperBlock> PAPER = REGISTER.register("paper", () -> new PaperBlock(BlockBehaviour.Properties.of().strength(0.5f).noOcclusion()));

    public static final DyeableRegistration<Block> PRINTERS = new DyeableRegistration<>(REGISTER) {
        @Override
        public DeferredHolder<Block, PrinterBlock> register(DeferredRegister<Block> registrar, DyeColor color) {
            return registrar.register(color.getName() + "_printer", () -> new PrinterBlock(color, UltreonDevicesCommon.id(color.getName() + "_printer")));
        }
    };

    public static final DyeableRegistration<Block> ROUTERS = new DyeableRegistration<>(REGISTER) {
        public DeferredHolder<Block, RouterBlock> register(DeferredRegister<Block> registrar, DyeColor color) {
            return registrar.register(color.getName() + "_router", () -> new RouterBlock(color, UltreonDevicesCommon.id(color.getName() + "_router")));
        }
    };

    public static final DyeableRegistration<Block> OFFICE_CHAIRS = new DyeableRegistration<>(REGISTER) {
        public DeferredHolder<Block, OfficeChairBlock> register(DeferredRegister<Block> registrar, DyeColor color) {
            return registrar.register(color.getName() + "_office_chair", () -> new OfficeChairBlock(color, UltreonDevicesCommon.id(color.getName() + "_office_chair")));
        }
    };

    public static Stream<Block> getAllBlocks() {
        return REGISTER.getRegistry().get().stream();
    }

    public static List<LaptopBlock> getAllLaptops() {
        return getAllBlocks().filter(block -> block instanceof LaptopBlock).map(block -> (LaptopBlock) block).toList();
    }

    public static List<PrinterBlock> getAllPrinters() {
        return getAllBlocks().filter(block -> block instanceof PrinterBlock).map(block -> (PrinterBlock) block).toList();
    }

    public static List<RouterBlock> getAllRouters() {
        return getAllBlocks().filter(block -> block instanceof RouterBlock).map(block -> (RouterBlock) block).toList();
    }

    public static List<OfficeChairBlock> getAllOfficeChairs() {
        return getAllBlocks().filter(block -> block instanceof OfficeChairBlock).map(block -> (OfficeChairBlock) block).toList();
    }
}
