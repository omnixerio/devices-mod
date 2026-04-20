package dev.ultreon.devices.init;

import com.google.common.collect.Lists;
import dev.ultreon.devices.block.*;
import dev.ultreon.devices.platform.Services;
import dev.ultreon.devices.platform.services.RegistrySupplier;
import dev.ultreon.devices.util.DyeableRegistration;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class DeviceBlocks {
    private static <T extends Block> RegistrySupplier<T> register(String name, Function<BlockBehaviour.Properties, T> blockFactory, Supplier<BlockBehaviour.Properties> settings, boolean shouldRegisterItem) {
        return Services.PLATFORM.registerBlock(name, blockFactory, settings, shouldRegisterItem);
    }

    public static void register() {
    }

    public static final DyeableRegistration<LaptopBlock> LAPTOPS = new DyeableRegistration<>() {
        public RegistrySupplier<LaptopBlock> register(DyeColor color) {
            return DeviceBlocks.register(color.getName() + "_laptop", (properties) -> new LaptopBlock(color, properties), () -> BlockBehaviour.Properties.of().strength(2.5f).noOcclusion(), true);
        }
    };

    public static final RegistrySupplier<MacMaxXBlock> MAC_MAX_X = register("mac_max_x", MacMaxXBlock::new, () -> BlockBehaviour.Properties.of().strength(2.5f).noOcclusion(), true);
    public static final RegistrySupplier<MacMaxXBlockPart> MAC_MAX_X_PART = register("mac_max_x_part", MacMaxXBlockPart::new, () -> BlockBehaviour.Properties.of().strength(2.5f).noOcclusion(), false);
    public static final RegistrySupplier<PaperBlock> PAPER = register("paper", PaperBlock::new, () -> BlockBehaviour.Properties.of().strength(0.5f).noOcclusion(), true);

    public static final DyeableRegistration<PrinterBlock> PRINTERS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<PrinterBlock> register(DyeColor color) {
            return DeviceBlocks.register(color.getName() + "_printer", properties -> new PrinterBlock(color, properties), () -> BlockBehaviour.Properties.of().strength(2.5f).noOcclusion(), true);
        }
    };

    public static final DyeableRegistration<RouterBlock> ROUTERS = new DyeableRegistration<>() {
        public RegistrySupplier<RouterBlock> register(DyeColor color) {
            return DeviceBlocks.register(color.getName() + "_router", properties -> new RouterBlock(color, properties), () -> BlockBehaviour.Properties.of().strength(2.5f).noOcclusion(), true);
        }
    };

    public static final DyeableRegistration<OfficeChairBlock> OFFICE_CHAIRS = new DyeableRegistration<>() {
        public RegistrySupplier<OfficeChairBlock> register(DyeColor color) {
            return DeviceBlocks.register(color.getName() + "_office_chair", properties -> new OfficeChairBlock(color, properties), () -> BlockBehaviour.Properties.of().strength(2.5f).noOcclusion(), true);
        }
    };

    public static List<RegistrySupplier<LaptopBlock>> getAllLaptops() {
        return Lists.newArrayList(LAPTOPS);
    }

    public static ArrayList<RegistrySupplier<PrinterBlock>> getAllPrinters() {
        return Lists.newArrayList(PRINTERS);
    }

    public static ArrayList<RegistrySupplier<RouterBlock>> getAllRouters() {
        return Lists.newArrayList(ROUTERS);
    }

    public static ArrayList<RegistrySupplier<OfficeChairBlock>> getAllOfficeChairs() {
        return Lists.newArrayList(OFFICE_CHAIRS);
    }
}
