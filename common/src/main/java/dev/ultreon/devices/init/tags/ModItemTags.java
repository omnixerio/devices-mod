package dev.ultreon.devices.init.tags;

import dev.ultreon.devices.OmnixerioDevicesMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ModItemTags {
    public static final TagKey<Item> INTERNAL_STORAGE = create("internal_storage");
    public static final TagKey<Item> LAPTOPS = create("laptops");
    public static final TagKey<Item> PRINTERS = create("printers");
    public static final TagKey<Item> FLASH_DRIVES = create("flash_drives");
    public static final TagKey<Item> ROUTERS = create("routers");

    private static TagKey<Item> create(String name) {
        return TagKey.create(Registries.ITEM, OmnixerioDevicesMod.id(name));
    }

    public static void init() {

    }
}
