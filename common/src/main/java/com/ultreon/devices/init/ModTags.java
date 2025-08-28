package com.ultreon.devices.init;

import com.ultreon.devices.Devices;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * @author XyperCode
 */
public final class ModTags {
    public static final class Items {
        public static final TagKey<Item> LAPTOPS = createTag("laptops");
        public static final TagKey<Item> PRINTERS = createTag("printers");
        public static final TagKey<Item> FLASH_DRIVES = createTag("flash_drives");
        public static final TagKey<Item> ROUTERS = createTag("routers");

        private static TagKey<Item> createTag(String name) {
            return TagKey.create(Registries.ITEM, Devices.id(name));
        }
    }

    public static final class Blocks {
        public static final TagKey<Block> LAPTOPS = createTag("laptops");
        public static final TagKey<Block> PRINTERS = createTag("printers");
        public static final TagKey<Block> ROUTERS = createTag("routers");

        private static TagKey<Block> createTag(String name) {
            return TagKey.create(Registries.BLOCK, Devices.id(name));
        }
    }

    public static final class BlockEntityTypes {
        public static final TagKey<BlockEntityType<?>> NETWORK_DEVICES = createTag("network_devices");

        private static TagKey<BlockEntityType<?>> createTag(String name) {
            return TagKey.create(Registries.BLOCK_ENTITY_TYPE, Devices.id(name));
        }
    }
}
