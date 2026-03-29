package com.ultreon.devices.init;

import com.ultreon.devices.Devices;
import com.ultreon.devices.ModDeviceTypes;
import com.ultreon.devices.item.*;
import com.ultreon.devices.util.DyeableRegistration;
import dev.ultreon.mods.xinexlib.platform.XinexPlatform;
import dev.ultreon.mods.xinexlib.registrar.Registrar;
import dev.ultreon.mods.xinexlib.registrar.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
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
    private static final Registrar<Item> REGISTER = Devices.REGISTRIES.get().getRegistrar(Registries.ITEM);

    // Laptops
    public static final DyeableRegistration<Item> LAPTOPS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<Item, Item> register(Registrar<Item> registrar, DyeColor color) {
            return registrar.register(color.getName() + "_laptop", () -> new ColoredDeviceItem(DeviceBlocks.LAPTOPS.of(color).get(), new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Devices.id(color.getName() + "_laptop"))), color, ModDeviceTypes.COMPUTER));
        }

        @Override
        protected Registrar<Item> autoInit() {
            return REGISTER;
        }
    };

    // Custom Computers
    public static final RegistrySupplier<DeviceItem, Item> MAC_MAX_X = REGISTER.register("mac_max_x", () -> new DeviceItem(DeviceBlocks.MAC_MAX_X.get(), new Item.Properties(), ModDeviceTypes.COMPUTER) {
        @NotNull
        public Component getDescription() {
            MutableComponent normalName = Component.translatable("block.devices.mac_max_x");
            if (XinexPlatform.isModLoaded("emojiful")) {
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
        public RegistrySupplier<ColoredDeviceItem, Item> register(Registrar<Item> registrar, DyeColor color) {
            return registrar.register(color.getName() + "_printer", () -> new ColoredDeviceItem(DeviceBlocks.PRINTERS.of(color).get(), new Item.Properties().setId(
                    ResourceKey.create(Registries.ITEM, Devices.id(color.getName() + "_printer"))
            ), color, ModDeviceTypes.PRINTER));
        }

        @Override
        protected Registrar<Item> autoInit() {
            return REGISTER;
        }
    };

    // Routers
    public static final DyeableRegistration<Item> ROUTERS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<ColoredDeviceItem, Item> register(Registrar<Item> registrar, DyeColor color) {
            return registrar.register(color.getName() + "_router", () -> new ColoredDeviceItem(DeviceBlocks.ROUTERS.of(color).get(), new Item.Properties().setId(
                    ResourceKey.create(Registries.ITEM, Devices.id(color.getName() + "_router"))
            ), color, ModDeviceTypes.ROUTER));
        }

        @Override
        protected Registrar<Item> autoInit() {
            return REGISTER;
        }
    };

    // Office Chairs
    public static final DyeableRegistration<Item> OFFICE_CHAIRS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<ColoredDeviceItem, Item> register(Registrar<Item> registrar, DyeColor color) {
            return registrar.register(color.getName() + "_office_chair", () -> new ColoredDeviceItem(DeviceBlocks.OFFICE_CHAIRS.of(color).get(), new Item.Properties().setId(
                    ResourceKey.create(Registries.ITEM, Devices.id(color.getName() + "_office_chair"))
            ), color, ModDeviceTypes.SEAT));
        }

        @Override
        protected Registrar<Item> autoInit() {
            return REGISTER;
        }
    };

    // Flash drives
    public static final DyeableRegistration<Item> FLASH_DRIVE = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<FlashDriveItem, Item> register(Registrar<Item> registrar, DyeColor color) {
            return registrar.register(color.getName() + "_flash_drive", () -> new FlashDriveItem(color, new Item.Properties().setId(
                    ResourceKey.create(Registries.ITEM, Devices.id(color.getName() + "_flash_drive"))
            )));
        }

        @Override
        protected Registrar<Item> autoInit() {
            return REGISTER;
        }
    };

    public static final RegistrySupplier<BlockItem, Item> PAPER = REGISTER.register("paper", () -> new BlockItem(DeviceBlocks.PAPER.get(), new Item.Properties()));

    public static final RegistrySupplier<BasicItem, Item> PLASTIC_UNREFINED = REGISTER.register("plastic_unrefined", () -> new BasicItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Devices.id("plastic_unrefined")))));
    public static final RegistrySupplier<BasicItem, Item> PLASTIC = REGISTER.register("plastic", () -> new BasicItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Devices.id("plastic")))));
    public static final RegistrySupplier<BasicItem, Item> PLASTIC_FRAME = REGISTER.register("plastic_frame", () -> new BasicItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Devices.id("plastic_frame")))));
    public static final RegistrySupplier<BasicItem, Item> WHEEL = REGISTER.register("wheel", () -> new BasicItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Devices.id("wheel")))));
    public static final RegistrySupplier<Item, Item> GLASS_DUST = REGISTER.register("glass_dust", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Devices.id("glass_dust")))));

    public static final RegistrySupplier<ComponentItem, Item> COMPONENT_CIRCUIT_BOARD = REGISTER.register("circuit_board", () -> new ComponentItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Devices.id("circuit_board")))));
    public static final RegistrySupplier<ComponentItem, Item> COMPONENT_MOTHERBOARD = REGISTER.register("motherboard", () -> new MotherboardItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Devices.id("motherboard")))));
    public static final RegistrySupplier<ComponentItem, Item> COMPONENT_MOTHERBOARD_FULL = REGISTER.register("motherboard_full", () -> new ComponentItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Devices.id("motherboard_full")))));
    public static final RegistrySupplier<ComponentItem, Item> COMPONENT_CPU = REGISTER.register("cpu", () -> new MotherboardItem.Component(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Devices.id("cpu")))));
    public static final RegistrySupplier<ComponentItem, Item> COMPONENT_RAM = REGISTER.register("ram", () -> new MotherboardItem.Component(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Devices.id("ram")))));
    public static final RegistrySupplier<ComponentItem, Item> COMPONENT_GPU = REGISTER.register("gpu", () -> new MotherboardItem.Component(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Devices.id("gpu")))));
    public static final RegistrySupplier<ComponentItem, Item> COMPONENT_WIFI = REGISTER.register("wifi", () -> new MotherboardItem.Component(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Devices.id("wifi")))));
    public static final RegistrySupplier<ComponentItem, Item> COMPONENT_HARD_DRIVE = REGISTER.register("hard_drive", () -> new ComponentItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Devices.id("hard_drive")))));
    public static final RegistrySupplier<ComponentItem, Item> COMPONENT_FLASH_CHIP = REGISTER.register("flash_chip", () -> new ComponentItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Devices.id("flash_chip")))));
    public static final RegistrySupplier<ComponentItem, Item> COMPONENT_SOLID_STATE_DRIVE = REGISTER.register("solid_state_drive", () -> new ComponentItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Devices.id("solid_state_drive")))));
    public static final RegistrySupplier<ComponentItem, Item> COMPONENT_BATTERY = REGISTER.register("battery", () -> new ComponentItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Devices.id("battery")))));
    public static final RegistrySupplier<ComponentItem, Item> COMPONENT_SCREEN = REGISTER.register("screen", () -> new ComponentItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Devices.id("screen")))));
    public static final RegistrySupplier<ComponentItem, Item> COMPONENT_CONTROLLER_UNIT = REGISTER.register("controller_unit", () -> new ComponentItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Devices.id("controller_unit")))));
    public static final RegistrySupplier<ComponentItem, Item> COMPONENT_SMALL_ELECTRIC_MOTOR = REGISTER.register("small_electric_motor", () -> new ComponentItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Devices.id("small_electric_motor")))));
    public static final RegistrySupplier<ComponentItem, Item> COMPONENT_CARRIAGE = REGISTER.register("carriage", () -> new ComponentItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Devices.id("carriage")))));

    public static final RegistrySupplier<EthernetCableItem, Item> ETHERNET_CABLE = REGISTER.register("ethernet_cable", EthernetCableItem::new);
    

    public static Stream<Item> getAllItems() {
        return REGISTER.registry().stream();
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
