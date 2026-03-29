package dev.ultreon.devices.init;

import dev.ultreon.devices.UltreonDevicesCommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class DeviceTags {
    public static void register() {
        Items.register();
    }

    public static class Items {
        public static final TagKey<Item> INTERNAL_STORAGE = create("internal_storage");
        public static final TagKey<Item> LAPTOPS = create("laptops");

        private static TagKey<Item> create(String path) {
            return TagKey.create(Registries.ITEM, UltreonDevicesCommon.id(path));
        }

        public static void register() {

        }
    }
}
