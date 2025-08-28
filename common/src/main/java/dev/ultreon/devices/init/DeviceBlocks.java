package dev.ultreon.devices.init;

import com.google.common.collect.Lists;
import dev.ultreon.devices.Devices;
import dev.ultreon.devices.block.*;
import dev.ultreon.devices.block.computer.LaptopBlock;
import dev.ultreon.devices.block.computer.MacMaxXBlock;
import dev.ultreon.devices.block.computer.MacMaxXBlockPart;
import dev.ultreon.devices.util.DyeableRegistration;
import dev.ultreon.mods.xinexlib.registrar.Registrar;
import dev.ultreon.mods.xinexlib.registrar.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.stream.Stream;

public class DeviceBlocks {
    private static final Registrar<Block> REGISTER = Devices.REGISTRIES.get().getRegistrar(Registries.BLOCK);

    public static void register() {
        REGISTER.load();
    }

    public static final DyeableRegistration<LaptopBlock, Block> LAPTOPS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<LaptopBlock, Block> register(Registrar<Block> registrar, DyeColor color) {
            return registrar.register(color.getName() + "_laptop", () -> new LaptopBlock(color));
        }

        @Override
        protected Registrar<Block> autoInit() {
            return REGISTER;
        }
    };

    public static final RegistrySupplier<MacMaxXBlock, Block> MAC_MAX_X = REGISTER.register("mac_max_x", MacMaxXBlock::new);
    public static final RegistrySupplier<MacMaxXBlockPart, Block> MAC_MAX_X_PART = REGISTER.register("mac_max_x_part", MacMaxXBlockPart::new);

    public static final DyeableRegistration<PrinterBlock, Block> PRINTERS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<PrinterBlock, Block> register(Registrar<Block> registrar, DyeColor color) {
            return registrar.register(color.getName() + "_printer", () -> new PrinterBlock(color));
        }

        @Override
        protected Registrar<Block> autoInit() {
            return REGISTER;
        }
    };

    public static final DyeableRegistration<RouterBlock, Block> ROUTERS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<RouterBlock, Block> register(Registrar<Block> registrar, DyeColor color) {
            return registrar.register(color.getName() + "_router", () -> new RouterBlock(color));
        }

        @Override
        protected Registrar<Block> autoInit() {
            return REGISTER;
        }
    };

    public static final DyeableRegistration<OfficeChairBlock, Block> OFFICE_CHAIRS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<OfficeChairBlock, Block> register(Registrar<Block> registrar, DyeColor color) {
            return registrar.register(color.getName() + "_office_chair", () -> new OfficeChairBlock(color));
        }

        @Override
        protected Registrar<Block> autoInit() {
            return REGISTER;
        }
    };


    public static final RegistrySupplier<PaperBlock, Block> PAPER = REGISTER.register("paper", PaperBlock::new);


    public static Stream<Block> getAllBlocks() {
        return Lists.newArrayList(REGISTER).stream().map(RegistrySupplier::get);
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
