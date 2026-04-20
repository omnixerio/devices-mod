package dev.ultreon.devices.init;

import com.google.common.collect.Lists;
import dev.ultreon.devices.item.*;
import dev.ultreon.devices.platform.Services;
import dev.ultreon.devices.platform.services.RegistrySupplier;
import dev.ultreon.devices.util.DyeableRegistration;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.function.Function;

@SuppressWarnings("unused")
public class DeviceItems {
    public static <T extends Item> RegistrySupplier<T> register(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
        return Services.PLATFORM.registerItem(name, itemFactory, settings);
    }

    // Flash drives
    public static final DyeableRegistration<FlashDriveItem> FLASH_DRIVE = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<FlashDriveItem> register(DyeColor color) {
            return DeviceItems.register(color.getName() + "_flash_drive", properties -> new FlashDriveItem(color, properties), new Item.Properties());
        }

    };

    public static final RegistrySupplier<BasicItem> PLASTIC_UNREFINED = register("plastic_unrefined", BasicItem::new, new Item.Properties());
    public static final RegistrySupplier<BasicItem> PLASTIC = register("plastic", BasicItem::new, new Item.Properties());
    public static final RegistrySupplier<BasicItem> PLASTIC_FRAME = register("plastic_frame", BasicItem::new, new Item.Properties());
    public static final RegistrySupplier<BasicItem> WHEEL = register("wheel", BasicItem::new, new Item.Properties());
    public static final RegistrySupplier<Item> GLASS_DUST = register("glass_dust", Item::new, new Item.Properties());
    public static final RegistrySupplier<ComponentItem> COMPONENT_CIRCUIT_BOARD = register("circuit_board", ComponentItem::new, new Item.Properties());
    public static final RegistrySupplier<MotherboardItem> COMPONENT_MOTHERBOARD = register("motherboard", MotherboardItem::new, new Item.Properties());
    public static final RegistrySupplier<ComponentItem> COMPONENT_MOTHERBOARD_FULL = register("motherboard_full", ComponentItem::new, new Item.Properties());
    public static final RegistrySupplier<MotherboardItem> COMPONENT_CPU = register("cpu", MotherboardItem::new, new Item.Properties());
    public static final RegistrySupplier<MotherboardItem> COMPONENT_RAM = register("ram", MotherboardItem::new, new Item.Properties());
    public static final RegistrySupplier<MotherboardItem> COMPONENT_GPU = register("gpu", MotherboardItem::new, new Item.Properties());
    public static final RegistrySupplier<MotherboardItem> COMPONENT_WIFI = register("wifi", MotherboardItem::new, new Item.Properties());
    public static final RegistrySupplier<ComponentItem> COMPONENT_HARD_DRIVE = register("hard_drive", ComponentItem::new, new Item.Properties());
    public static final RegistrySupplier<ComponentItem> COMPONENT_FLASH_CHIP = register("flash_chip", ComponentItem::new, new Item.Properties());
    public static final RegistrySupplier<ComponentItem> COMPONENT_SOLID_STATE_DRIVE = register("solid_state_drive", ComponentItem::new, new Item.Properties());
    public static final RegistrySupplier<ComponentItem> COMPONENT_BATTERY = register("battery", ComponentItem::new, new Item.Properties());
    public static final RegistrySupplier<ComponentItem> COMPONENT_SCREEN = register("screen", ComponentItem::new, new Item.Properties());
    public static final RegistrySupplier<ComponentItem> COMPONENT_CONTROLLER_UNIT = register("controller_unit", ComponentItem::new, new Item.Properties());
    public static final RegistrySupplier<ComponentItem> COMPONENT_SMALL_ELECTRIC_MOTOR = register("small_electric_motor", ComponentItem::new, new Item.Properties());
    public static final RegistrySupplier<ComponentItem> COMPONENT_CARRIAGE = register("carriage", ComponentItem::new, new Item.Properties());


    public static final RegistrySupplier<EthernetCableItem> ETHERNET_CABLE = register("ethernet_cable", EthernetCableItem::new, new Item.Properties().stacksTo(1));


    public static @NonNull FlashDriveItem getFlashDriveByColor(DyeColor color) {
        return FLASH_DRIVE.of(color);
    }

    public static ArrayList<RegistrySupplier<FlashDriveItem>> getAllFlashDrives() {
        return Lists.newArrayList(FLASH_DRIVE);
    }

    public static void register() {

    }
}
