package com.ultreon.devices.init;

import com.ultreon.devices.Devices;
import com.ultreon.devices.ModDeviceTypes;
import com.ultreon.devices.item.*;
import com.ultreon.devices.util.DyeableRegistration;
import dev.ultreon.mods.xinexlib.registrar.Registrar;
import dev.ultreon.mods.xinexlib.registrar.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class DeviceItems {
    private static final Registrar<Item> REGISTER = Devices.REGISTRIES.get().get(Registries.ITEM);

    // Laptops
    public static final DyeableRegistration<Item> LAPTOPS = new DyeableRegistration<Item>() {
        @Override
        public RegistrySupplier<Item, Item> register(Registrar<Item> registrar, DyeColor color) {
            return registrar.register(Devices.id(color.getName() + "_laptop"), () -> new ColoredDeviceItem(DeviceBlocks.LAPTOPS.of(color).get(), new Item.Properties(), color, ModDeviceTypes.COMPUTER));
        }

        @Override
        protected Registrar<Item> autoInit() {
            return REGISTER;
        }
    };

    // Custom Computers
    public static final RegistrySupplier<BlockItem> MAC_MAX_X = REGISTER.register(Devices.id("mac_max_x"), () -> new DeviceItem(DeviceBlocks.MAC_MAX_X.get(), new Item.Properties(), ModDeviceTypes.COMPUTER) {
        @NotNull
        @Override
        public Component getDescription() {
            MutableComponent normalName = Component.translatable("block.devices.mac_max_x");
            if (Platform.isModLoaded("emojiful")) {
                return Component.translatable("block.devices.mac_max_x_emoji");
            }
            return normalName;
        }

        @NotNull
        @Override
        public Component getName(@NotNull ItemStack stack) {
            return getDescription();
        }
    });

    // Printers
    public static final DyeableRegistration<Item> PRINTERS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<Item> register(Registrar<Item> registrar, DyeColor color) {
            return registrar.register(Devices.id(color.getName() + "_printer"), () -> new ColoredDeviceItem(DeviceBlocks.PRINTERS.of(color).get(), new Item.Properties(), color, ModDeviceTypes.PRINTER));
        }

        @Override
        protected Registrar<Item> autoInit() {
            return REGISTER;
        }
    };

    // Routers
    public static final DyeableRegistration<Item> ROUTERS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<Item> register(Registrar<Item> registrar, DyeColor color) {
            return registrar.register(Devices.id(color.getName() + "_router"), () -> new ColoredDeviceItem(DeviceBlocks.ROUTERS.of(color).get(), new Item.Properties(), color, ModDeviceTypes.ROUTER));
        }

        @Override
        protected Registrar<Item> autoInit() {
            return REGISTER;
        }
    };

    // Office Chairs
    public static final DyeableRegistration<Item> OFFICE_CHAIRS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<Item> register(Registrar<Item> registrar, DyeColor color) {
            return registrar.register(Devices.id(color.getName() + "_office_chair"), () -> new ColoredDeviceItem(DeviceBlocks.OFFICE_CHAIRS.of(color).get(), new Item.Properties(), color, ModDeviceTypes.SEAT));
        }

        @Override
        protected Registrar<Item> autoInit() {
            return REGISTER;
        }
    };

    // Flash drives
    public static final DyeableRegistration<Item> FLASH_DRIVE = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<Item> register(Registrar<Item> registrar, DyeColor color) {
            return registrar.register(Devices.id(color.getName() + "_flash_drive"), () -> new FlashDriveItem(color));
        }

        @Override
        protected Registrar<Item> autoInit() {
            return REGISTER;
        }
    };

    public static final RegistrySupplier<BlockItem> PAPER = REGISTER.register(Devices.id("paper"), () -> new BlockItem(DeviceBlocks.PAPER.get(), new Item.Properties()));

    public static final RegistrySupplier<BasicItem> PLASTIC_UNREFINED = REGISTER.register(Devices.id("plastic_unrefined"), () -> new BasicItem(new Item.Properties()));
    public static final RegistrySupplier<BasicItem> PLASTIC = REGISTER.register(Devices.id("plastic"), () -> new BasicItem(new Item.Properties()));
    public static final RegistrySupplier<BasicItem> PLASTIC_FRAME = REGISTER.register(Devices.id("plastic_frame"), () -> new BasicItem(new Item.Properties()));
    public static final RegistrySupplier<BasicItem> WHEEL = REGISTER.register(Devices.id("wheel"), () -> new BasicItem(new Item.Properties()));
    public static final RegistrySupplier<Item> GLASS_DUST = REGISTER.register(Devices.id("glass_dust"), () -> new Item(new Item.Properties()));

    public static final RegistrySupplier<ComponentItem> COMPONENT_CIRCUIT_BOARD = REGISTER.register(Devices.id("circuit_board"), () -> new ComponentItem(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem> COMPONENT_MOTHERBOARD = REGISTER.register(Devices.id("motherboard"), () -> new MotherboardItem(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem> COMPONENT_MOTHERBOARD_FULL = REGISTER.register(Devices.id("motherboard_full"), () -> new ComponentItem(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem> COMPONENT_CPU = REGISTER.register(Devices.id("cpu"), () -> new MotherboardItem.Component(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem> COMPONENT_RAM = REGISTER.register(Devices.id("ram"), () -> new MotherboardItem.Component(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem> COMPONENT_GPU = REGISTER.register(Devices.id("gpu"), () -> new MotherboardItem.Component(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem> COMPONENT_WIFI = REGISTER.register(Devices.id("wifi"), () -> new MotherboardItem.Component(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem> COMPONENT_HARD_DRIVE = REGISTER.register(Devices.id("hard_drive"), () -> new ComponentItem(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem> COMPONENT_FLASH_CHIP = REGISTER.register(Devices.id("flash_chip"), () -> new ComponentItem(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem> COMPONENT_SOLID_STATE_DRIVE = REGISTER.register(Devices.id("solid_state_drive"), () -> new ComponentItem(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem> COMPONENT_BATTERY = REGISTER.register(Devices.id("battery"), () -> new ComponentItem(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem> COMPONENT_SCREEN = REGISTER.register(Devices.id("screen"), () -> new ComponentItem(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem> COMPONENT_CONTROLLER_UNIT = REGISTER.register(Devices.id("controller_unit"), () -> new ComponentItem(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem> COMPONENT_SMALL_ELECTRIC_MOTOR = REGISTER.register(Devices.id("small_electric_motor"), () -> new ComponentItem(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem> COMPONENT_CARRIAGE = REGISTER.register(Devices.id("carriage"), () -> new ComponentItem(new Item.Properties()));

    public static final RegistrySupplier<EthernetCableItem> ETHERNET_CABLE = REGISTER.register(Devices.id("ethernet_cable"), EthernetCableItem::new);
    

    public static Stream<Item> getAllItems() {
        return REGISTER.getIds().stream().map(REGISTER::get);
    }

    @Nullable
    public static FlashDriveItem getFlashDriveByColor(DyeColor color) {
        return (FlashDriveItem) FLASH_DRIVE.of(color).get();
    }

    public static List<FlashDriveItem> getAllFlashDrives() {
        return getAllItems()
                .filter(item -> item.asItem() instanceof FlashDriveItem)
                .map(item -> (FlashDriveItem) item.asItem())
                .toList();
    }

    public static List<ColoredDeviceItem> getAllLaptops() {
        return getAllItems()
                .filter(item -> item.asItem() instanceof ColoredDeviceItem)
                .map(item -> (ColoredDeviceItem) item.asItem())
                .filter(item -> item.getDeviceType() == ModDeviceTypes.COMPUTER)
                .toList();
    }

    public static List<ColoredDeviceItem> getAllPrinters() {
        return getAllItems()
                .filter(item -> item.asItem() instanceof ColoredDeviceItem)
                .map(item -> (ColoredDeviceItem) item.asItem())
                .filter(item -> item.getDeviceType() == ModDeviceTypes.PRINTER)
                .toList();
    }

    public static List<ColoredDeviceItem> getAllRouters() {
        return getAllItems()
                .filter(item -> item.asItem() instanceof ColoredDeviceItem)
                .map(item -> (ColoredDeviceItem) item.asItem())
                .filter(item -> item.getDeviceType() == ModDeviceTypes.ROUTER)
                .toList();
    }

    public static void register() {

    }
}
