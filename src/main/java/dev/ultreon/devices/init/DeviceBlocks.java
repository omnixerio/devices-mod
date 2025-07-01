package dev.ultreon.devices.init;

import dev.ultreon.devices.Devices;
import dev.ultreon.devices.DyeColor;
import dev.ultreon.devices.block.*;
import dev.ultreon.devices.util.DyeableRegistration;
import dev.ultreon.libs.registries.v0.RegistrySupplier;
import dev.ultreon.quantum.block.Block;
import dev.ultreon.quantum.registry.Registry;

import java.util.List;
import java.util.stream.Stream;

public class DeviceBlocks {
    private static final Registry<Block> REGISTER = Devices.REGISTRIES.get().get(Registries.BLOCK);

    public static void register() {
    }

    public static final DyeableRegistration<Block> LAPTOPS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<Block> register(Registry<Block> registrar, DyeColor color) {
            return registrar.register(Devices.id(color.getName() + "_laptop"), () -> new LaptopBlock(color));
        }

        @Override
        protected dev.ultreon.quantum.registry.Registry<Block> autoInit() {
            return REGISTER;
        }
    };

    public static final RegistrySupplier<MacMaxXBlock> MAC_MAX_X = REGISTER.register(Devices.id("mac_max_x"), MacMaxXBlock::new);
    public static final RegistrySupplier<MacMaxXBlockPart> MAC_MAX_X_PART = REGISTER.register(Devices.id("mac_max_x_part"), MacMaxXBlockPart::new);

    public static final DyeableRegistration<Block> PRINTERS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<Block> register(Registrar<Block> registrar, DyeColor color) {
            return registrar.register(Devices.id(color.getName() + "_printer"), () -> new PrinterBlock(color));
        }

        @Override
        protected Registrar<Block> autoInit() {
            return REGISTER;
        }
    };

    public static final DyeableRegistration<Block> ROUTERS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<Block> register(Registrar<Block> registrar, DyeColor color) {
            return registrar.register(Devices.id(color.getName() + "_router"), () -> new RouterBlock(color));
        }

        @Override
        protected Registrar<Block> autoInit() {
            return REGISTER;
        }
    };

    public static final DyeableRegistration<Block> OFFICE_CHAIRS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<Block> register(Registrar<Block> registrar, DyeColor color) {
            return registrar.register(Devices.id(color.getName() + "_office_chair"), () -> new OfficeChairBlock(color));
        }

        @Override
        protected Registrar<Block> autoInit() {
            return REGISTER;
        }
    };


    public static final RegistrySupplier<PaperBlock> PAPER = REGISTER.register(Devices.id("paper"), PaperBlock::new);


    public static Stream<Block> getAllBlocks() {
        return REGISTER.getIds().stream().map(REGISTER::get);
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
