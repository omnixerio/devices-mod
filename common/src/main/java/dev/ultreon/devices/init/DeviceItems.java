package dev.ultreon.devices.init;

import com.google.common.collect.Streams;
import dev.ultreon.devices.Devices;
import dev.ultreon.devices.ModDeviceTypes;
import dev.ultreon.devices.item.*;
import dev.ultreon.devices.util.DyeableRegistration;
import dev.ultreon.mods.xinexlib.platform.XinexPlatform;
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
    private static final Registrar<Item> REGISTER = Devices.REGISTRIES.get().getRegistrar(Registries.ITEM);

    // Laptops
    public static final DyeableRegistration<ColoredDeviceItem, Item> LAPTOPS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<ColoredDeviceItem, Item> register(Registrar<Item> registrar, DyeColor color) {
            return registrar.register(color.getName() + "_laptop", () -> new ColoredDeviceItem(DeviceBlocks.LAPTOPS.of(color).get(), new Item.Properties(), color, ModDeviceTypes.COMPUTER));
        }

        @Override
        protected Registrar<Item> autoInit() {
            return REGISTER;
        }
    };

    // Custom Computers
    public static final RegistrySupplier<DeviceItem, Item> MAC_MAX_X = REGISTER.register("mac_max_x", () -> new DeviceItem(DeviceBlocks.MAC_MAX_X.get(), new Item.Properties(), ModDeviceTypes.COMPUTER) {
        @NotNull
        @Override
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
    public static final DyeableRegistration<ColoredDeviceItem, Item> PRINTERS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<ColoredDeviceItem, Item> register(Registrar<Item> registrar, DyeColor color) {
            return registrar.register(color.getName() + "_printer", () -> new ColoredDeviceItem(DeviceBlocks.PRINTERS.of(color).get(), new Item.Properties(), color, ModDeviceTypes.PRINTER));
        }

        @Override
        protected Registrar<Item> autoInit() {
            return REGISTER;
        }
    };

    // Routers
    public static final DyeableRegistration<ColoredDeviceItem, Item> ROUTERS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<ColoredDeviceItem, Item> register(Registrar<Item> registrar, DyeColor color) {
            return registrar.register(color.getName() + "_router", () -> new ColoredDeviceItem(DeviceBlocks.ROUTERS.of(color).get(), new Item.Properties(), color, ModDeviceTypes.ROUTER));
        }

        @Override
        protected Registrar<Item> autoInit() {
            return REGISTER;
        }
    };

    // Office Chairs
    public static final DyeableRegistration<ColoredDeviceItem, Item> OFFICE_CHAIRS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<ColoredDeviceItem, Item> register(Registrar<Item> registrar, DyeColor color) {
            return registrar.register(color.getName() + "_office_chair", () -> new ColoredDeviceItem(DeviceBlocks.OFFICE_CHAIRS.of(color).get(), new Item.Properties(), color, ModDeviceTypes.SEAT));
        }

        @Override
        protected Registrar<Item> autoInit() {
            return REGISTER;
        }
    };

    // Flash drives
    public static final DyeableRegistration<FlashDriveItem, Item> FLASH_DRIVE = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<FlashDriveItem, Item> register(Registrar<Item> registrar, DyeColor color) {
            return registrar.register(color.getName() + "_flash_drive", () -> new FlashDriveItem(color));
        }

        @Override
        protected Registrar<Item> autoInit() {
            return REGISTER;
        }
    };

    public static final RegistrySupplier<BlockItem, Item> PAPER = REGISTER.register("paper", () -> new BlockItem(DeviceBlocks.PAPER.get(), new Item.Properties()));

    public static final RegistrySupplier<BasicItem, Item> PLASTIC_UNREFINED = REGISTER.register("plastic_unrefined", () -> new BasicItem(new Item.Properties()));
    public static final RegistrySupplier<BasicItem, Item> PLASTIC = REGISTER.register("plastic", () -> new BasicItem(new Item.Properties()));
    public static final RegistrySupplier<BasicItem, Item> PLASTIC_FRAME = REGISTER.register("plastic_frame", () -> new BasicItem(new Item.Properties()));
    public static final RegistrySupplier<BasicItem, Item> WHEEL = REGISTER.register("wheel", () -> new BasicItem(new Item.Properties()));
    public static final RegistrySupplier<Item, Item> GLASS_DUST = REGISTER.register("glass_dust", () -> new Item(new Item.Properties()));

    public static final RegistrySupplier<ComponentItem, Item> COMPONENT_CIRCUIT_BOARD = REGISTER.register("circuit_board", () -> new ComponentItem(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem, Item> COMPONENT_MOTHERBOARD = REGISTER.register("motherboard", () -> new MotherboardItem(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem, Item> COMPONENT_MOTHERBOARD_FULL = REGISTER.register("motherboard_full", () -> new ComponentItem(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem, Item> COMPONENT_CPU = REGISTER.register("cpu", () -> new MotherboardItem.Component(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem, Item> COMPONENT_RAM = REGISTER.register("ram", () -> new MotherboardItem.Component(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem, Item> COMPONENT_GPU = REGISTER.register("gpu", () -> new MotherboardItem.Component(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem, Item> COMPONENT_WIFI = REGISTER.register("wifi", () -> new MotherboardItem.Component(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem, Item> COMPONENT_HARD_DRIVE = REGISTER.register("hard_drive", () -> new ComponentItem(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem, Item> COMPONENT_FLASH_CHIP = REGISTER.register("flash_chip", () -> new ComponentItem(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem, Item> COMPONENT_SOLID_STATE_DRIVE = REGISTER.register("solid_state_drive", () -> new ComponentItem(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem, Item> COMPONENT_BATTERY = REGISTER.register("battery", () -> new ComponentItem(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem, Item> COMPONENT_SCREEN = REGISTER.register("screen", () -> new ComponentItem(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem, Item> COMPONENT_CONTROLLER_UNIT = REGISTER.register("controller_unit", () -> new ComponentItem(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem, Item> COMPONENT_SMALL_ELECTRIC_MOTOR = REGISTER.register("small_electric_motor", () -> new ComponentItem(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem, Item> COMPONENT_CARRIAGE = REGISTER.register("carriage", () -> new ComponentItem(new Item.Properties()));

    public static final RegistrySupplier<EthernetCableItem, Item> ETHERNET_CABLE = REGISTER.register("ethernet_cable", EthernetCableItem::new);
    

    public static Stream<Item> getAllItems() {
        return Streams.stream(REGISTER).map(RegistrySupplier::get);
    }

    @Nullable
    public static FlashDriveItem getFlashDriveByColor(DyeColor color) {
        return FLASH_DRIVE.of(color).get();
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
        REGISTER.load();
    }
}
