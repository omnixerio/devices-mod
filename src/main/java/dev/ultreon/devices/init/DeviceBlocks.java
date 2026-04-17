package dev.ultreon.devices.init;

import com.google.common.collect.Lists;
import dev.ultreon.devices.OmnixerioDevicesCommon;
import dev.ultreon.devices.block.*;
import dev.ultreon.devices.util.DyeableRegistration;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.List;
import java.util.function.Function;

public class DeviceBlocks {
    private static <T extends Block> T register(String name, Function<BlockBehaviour.Properties, T> blockFactory, BlockBehaviour.Properties settings, boolean shouldRegisterItem) {
        // Create a registry key for the block
        ResourceKey<Block> blockKey = keyOfBlock(name);
        // Create the block instance
        T block = blockFactory.apply(settings.setId(blockKey));

        // Sometimes, you may not want to register an item for the block.
        // Eg: if it's a technical block like `minecraft:moving_piston` or `minecraft:end_gateway`
        if (shouldRegisterItem) {
            // Items need to be registered with a different type of registry key, but the ID
            // can be the same.
            ResourceKey<Item> itemKey = keyOfItem(name);

            BlockItem blockItem = new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
            Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
        }

        return Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
    }


    private static ResourceKey<Block> keyOfBlock(String name) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(OmnixerioDevicesCommon.MOD_ID, name));
    }

    private static ResourceKey<Item> keyOfItem(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(OmnixerioDevicesCommon.MOD_ID, name));
    }
    public static void register() {
    }

    public static final DyeableRegistration<LaptopBlock> LAPTOPS = new DyeableRegistration<>() {
        public LaptopBlock register(DyeColor color) {
            return DeviceBlocks.register(color.getName() + "_laptop", (properties) -> new LaptopBlock(color, properties), BlockBehaviour.Properties.of().strength(2.5f).noOcclusion(), true);
        }
    };

    public static final MacMaxXBlock MAC_MAX_X = register("mac_max_x", MacMaxXBlock::new, BlockBehaviour.Properties.of().strength(2.5f).noOcclusion(), true);
    public static final MacMaxXBlockPart MAC_MAX_X_PART = register("mac_max_x_part", MacMaxXBlockPart::new, BlockBehaviour.Properties.of().strength(2.5f).noOcclusion(), false);
    public static final PaperBlock PAPER = register("paper", PaperBlock::new, BlockBehaviour.Properties.of().strength(0.5f).noOcclusion(), true);

    public static final DyeableRegistration<PrinterBlock> PRINTERS = new DyeableRegistration<PrinterBlock>() {
        @Override
        public PrinterBlock register(DyeColor color) {
            return DeviceBlocks.register(color.getName() + "_printer", properties -> new PrinterBlock(color, properties), BlockBehaviour.Properties.of().strength(2.5f).noOcclusion(), true);
        }
    };

    public static final DyeableRegistration<RouterBlock> ROUTERS = new DyeableRegistration<>() {
        public RouterBlock register(DyeColor color) {
            return DeviceBlocks.register(color.getName() + "_router", properties -> new RouterBlock(color, properties), BlockBehaviour.Properties.of().strength(2.5f).noOcclusion(), true);
        }
    };

    public static final DyeableRegistration<OfficeChairBlock> OFFICE_CHAIRS = new DyeableRegistration<>() {
        public OfficeChairBlock register(DyeColor color) {
            return DeviceBlocks.register(color.getName() + "_office_chair", properties -> new OfficeChairBlock(color, properties), BlockBehaviour.Properties.of().strength(2.5f).noOcclusion(), true);
        }
    };

    public static List<LaptopBlock> getAllLaptops() {
        return Lists.newArrayList(LAPTOPS);
    }

    public static List<PrinterBlock> getAllPrinters() {
        return Lists.newArrayList(PRINTERS);
    }

    public static List<RouterBlock> getAllRouters() {
        return Lists.newArrayList(ROUTERS);
    }

    public static List<OfficeChairBlock> getAllOfficeChairs() {
        return Lists.newArrayList(OFFICE_CHAIRS);
    }
}
