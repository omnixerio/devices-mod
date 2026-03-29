package com.ultreon.devices.init;

import com.ultreon.devices.Devices;
import com.ultreon.devices.block.*;
import com.ultreon.devices.util.DyeableRegistration;
import dev.ultreon.mods.xinexlib.registrar.Registrar;
import dev.ultreon.mods.xinexlib.registrar.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.List;
import java.util.stream.Stream;

public class DeviceBlocks {
    private static final Registrar<Block> REGISTER = Devices.REGISTRIES.get().getRegistrar(Registries.BLOCK);

    public static void register() {
    }

    public static final DyeableRegistration<Block> LAPTOPS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<LaptopBlock, Block> register(Registrar<Block> registrar, DyeColor color) {
            return registrar.register(color.getName() + "_laptop", () -> new LaptopBlock(color, Devices.id(color.getName() + "_laptop")));
        }

        @Override
        protected Registrar<Block> autoInit() {
            return REGISTER;
        }
    };

    public static final RegistrySupplier<MacMaxXBlock, Block> MAC_MAX_X = REGISTER.register("mac_max_x", () -> new MacMaxXBlock(BlockBehaviour.Properties.of().strength(2.5f).noOcclusion()));
    public static final RegistrySupplier<MacMaxXBlockPart, Block> MAC_MAX_X_PART = REGISTER.register("mac_max_x_part", () -> new MacMaxXBlockPart(BlockBehaviour.Properties.of().strength(2.5f).noOcclusion()));

    public static final DyeableRegistration<Block> PRINTERS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<PrinterBlock, Block> register(Registrar<Block> registrar, DyeColor color) {
            return registrar.register(color.getName() + "_printer", () -> new PrinterBlock(color, Devices.id(color.getName() + "_printer")));
        }

        @Override
        protected Registrar<Block> autoInit() {
            return REGISTER;
        }
    };

    public static final DyeableRegistration<Block> ROUTERS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<RouterBlock, Block> register(Registrar<Block> registrar, DyeColor color) {
            return registrar.register(color.getName() + "_router", () -> new RouterBlock(color, Devices.id(color.getName() + "_router")));
        }

        @Override
        protected Registrar<Block> autoInit() {
            return REGISTER;
        }
    };

    public static final DyeableRegistration<Block> OFFICE_CHAIRS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<OfficeChairBlock, Block> register(Registrar<Block> registrar, DyeColor color) {
            return registrar.register(color.getName() + "_office_chair", () -> new OfficeChairBlock(color, Devices.id(color.getName() + "_office_chair")));
        }

        @Override
        protected Registrar<Block> autoInit() {
            return REGISTER;
        }
    };


    public static final RegistrySupplier<PaperBlock, Block> PAPER = REGISTER.register("paper", () -> new PaperBlock(BlockBehaviour.Properties.of().strength(0.5f).noOcclusion()));


    public static Stream<Block> getAllBlocks() {
        return REGISTER.registry().stream();
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
