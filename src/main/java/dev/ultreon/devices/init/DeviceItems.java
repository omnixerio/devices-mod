package dev.ultreon.devices.init;

import com.google.common.collect.Lists;
import dev.ultreon.devices.OmnixerioDevicesCommon;
import dev.ultreon.devices.ModDeviceTypes;
import dev.ultreon.devices.item.*;
import dev.ultreon.devices.util.DyeableRegistration;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Function;

@SuppressWarnings("unused")
public class DeviceItems {
    public static <T extends Item> T register(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
        // Create the item key.
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(OmnixerioDevicesCommon.MOD_ID, name));

        // Create the item instance.
        T item = itemFactory.apply(settings.setId(itemKey));

        // Register the item.
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return item;
    }

    // Flash drives
    public static final DyeableRegistration<FlashDriveItem> FLASH_DRIVE = new DyeableRegistration<>() {
        @Override
        public FlashDriveItem register(DyeColor color) {
            return DeviceItems.register(color.getName() + "_flash_drive", properties -> new FlashDriveItem(color, properties), new Item.Properties());
        }

    };

    public static final BasicItem PLASTIC_UNREFINED = register("plastic_unrefined", BasicItem::new, new Item.Properties());
    public static final BasicItem PLASTIC = register("plastic", BasicItem::new, new Item.Properties());
    public static final BasicItem PLASTIC_FRAME = register("plastic_frame", BasicItem::new, new Item.Properties());
    public static final BasicItem WHEEL = register("wheel", BasicItem::new, new Item.Properties());
    public static final Item GLASS_DUST = register("glass_dust", Item::new, new Item.Properties());
    public static final ComponentItem COMPONENT_CIRCUIT_BOARD = register("circuit_board", ComponentItem::new, new Item.Properties());
    public static final ComponentItem COMPONENT_MOTHERBOARD = register("motherboard", MotherboardItem::new, new Item.Properties());
    public static final ComponentItem COMPONENT_MOTHERBOARD_FULL = register("motherboard_full", ComponentItem::new, new Item.Properties());
    public static final MotherboardItem COMPONENT_CPU = register("cpu", MotherboardItem::new, new Item.Properties());
    public static final MotherboardItem COMPONENT_RAM = register("ram", MotherboardItem::new, new Item.Properties());
    public static final MotherboardItem COMPONENT_GPU = register("gpu", MotherboardItem::new, new Item.Properties());
    public static final MotherboardItem COMPONENT_WIFI = register("wifi", MotherboardItem::new, new Item.Properties());
    public static final ComponentItem COMPONENT_HARD_DRIVE = register("hard_drive", ComponentItem::new, new Item.Properties());
    public static final ComponentItem COMPONENT_FLASH_CHIP = register("flash_chip", ComponentItem::new, new Item.Properties());
    public static final ComponentItem COMPONENT_SOLID_STATE_DRIVE = register("solid_state_drive", ComponentItem::new, new Item.Properties());
    public static final ComponentItem COMPONENT_BATTERY = register("battery", ComponentItem::new, new Item.Properties());
    public static final ComponentItem COMPONENT_SCREEN = register("screen", ComponentItem::new, new Item.Properties());
    public static final ComponentItem COMPONENT_CONTROLLER_UNIT = register("controller_unit", ComponentItem::new, new Item.Properties());
    public static final ComponentItem COMPONENT_SMALL_ELECTRIC_MOTOR = register("small_electric_motor", ComponentItem::new, new Item.Properties());
    public static final ComponentItem COMPONENT_CARRIAGE = register("carriage", ComponentItem::new, new Item.Properties());


    public static final EthernetCableItem ETHERNET_CABLE = register("ethernet_cable", EthernetCableItem::new, new Item.Properties().stacksTo(1));


    public static @NonNull FlashDriveItem getFlashDriveByColor(DyeColor color) {
        return FLASH_DRIVE.of(color);
    }

    public static List<FlashDriveItem> getAllFlashDrives() {
        return Lists.newArrayList(FLASH_DRIVE);
    }

    public static void register() {

    }
}
