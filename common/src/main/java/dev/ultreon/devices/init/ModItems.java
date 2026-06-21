package dev.ultreon.devices.init;

import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.ultreon.devices.ModDeviceTypes;
import dev.ultreon.devices.OmnixerioDevicesMod;
import dev.ultreon.devices.item.*;
import dev.ultreon.devices.util.DyeableRegistration;
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
import java.util.function.Function;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class ModItems {
    private static final Registrar<Item> REGISTER = OmnixerioDevicesMod.REGISTRIES.get().get(Registries.ITEM);

    // Laptops
    public static final DyeableRegistration<Item> LAPTOPS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<Item> register(Registrar<Item> registrar, DyeColor color) {
            return registrar.register(OmnixerioDevicesMod.id(color.getName() + "_laptop"), () -> new ColoredDeviceItem(ModBlocks.LAPTOPS.of(color).get(), new Item.Properties(), color, ModDeviceTypes.COMPUTER));
        }

        @Override
        protected Registrar<Item> autoInit() {
            return REGISTER;
        }
    };

    // Custom Computers
    public static final RegistrySupplier<BlockItem> MAC_MAX_X = register("mac_max_x", new Item.Properties(), properties -> new DeviceItem(ModBlocks.MAC_MAX_X.get(), new Item.Properties(), ModDeviceTypes.COMPUTER) {
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
            return ModItems.register(color.getName() + "_printer", new Item.Properties(), properties -> new ColoredDeviceItem(ModBlocks.PRINTERS.of(color).get(), properties, color, ModDeviceTypes.PRINTER));
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
            return ModItems.register(color.getName() + "_router", new Item.Properties(), properties -> new ColoredDeviceItem(ModBlocks.ROUTERS.of(color).get(), properties, color, ModDeviceTypes.ROUTER));
        }

        @Override
        protected Registrar<Item> autoInit() {
            return REGISTER;
        }
    };

    // Routers
    public static final DyeableRegistration<Item> CLOCKS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<Item> register(Registrar<Item> registrar, DyeColor color) {
            return ModItems.register(color.getName() + "_clock", new Item.Properties(), properties -> new ColoredDeviceItem(ModBlocks.CLOCKS.of(color).get(), properties, color, ModDeviceTypes.CLOCK));
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
            return ModItems.register(color.getName() + "_office_chair", new Item.Properties(), properties -> new ColoredDeviceItem(ModBlocks.OFFICE_CHAIRS.of(color).get(), properties, color, ModDeviceTypes.SEAT));
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
            return ModItems.register(color.getName() + "_flash_drive", new Item.Properties(), properties -> new FlashDriveItem(properties, color));
        }

        @Override
        protected Registrar<Item> autoInit() {
            return REGISTER;
        }
    };

    public static final RegistrySupplier<BlockItem> PAPER = register("paper", new Item.Properties(), properties -> new BlockItem(ModBlocks.PAPER.get(), properties));

    public static final RegistrySupplier<BasicItem> PLASTIC_UNREFINED = register("plastic_unrefined", new Item.Properties(), BasicItem::new);
    public static final RegistrySupplier<BasicItem> PLASTIC = register("plastic", new Item.Properties(), BasicItem::new);
    public static final RegistrySupplier<BasicItem> PLASTIC_FRAME = register("plastic_frame", new Item.Properties(), BasicItem::new);
    public static final RegistrySupplier<BasicItem> WHEEL = register("wheel", new Item.Properties(), BasicItem::new);
    public static final RegistrySupplier<Item> GLASS_DUST = register("glass_dust", new Item.Properties(), Item::new);

    public static final RegistrySupplier<ComponentItem> COMPONENT_CIRCUIT_BOARD = register("circuit_board", new Item.Properties(), ComponentItem::new);
    public static final RegistrySupplier<ComponentItem> COMPONENT_MOTHERBOARD = register("motherboard", new Item.Properties(), MotherboardItem::new);
    public static final RegistrySupplier<ComponentItem> COMPONENT_MOTHERBOARD_FULL = register("motherboard_full", new Item.Properties(), ComponentItem::new);
    public static final RegistrySupplier<ComponentItem> COMPONENT_CPU = register("cpu", new Item.Properties(), MotherboardItem.Component::new);
    public static final RegistrySupplier<ComponentItem> COMPONENT_RAM = register("ram", new Item.Properties(), MotherboardItem.Component::new);
    public static final RegistrySupplier<ComponentItem> COMPONENT_GPU = register("gpu", new Item.Properties(), MotherboardItem.Component::new);
    public static final RegistrySupplier<ComponentItem> COMPONENT_WIFI = register("wifi", new Item.Properties(), MotherboardItem.Component::new);
    public static final RegistrySupplier<ComponentItem> COMPONENT_HARD_DRIVE = register("hard_drive", new Item.Properties(), ComponentItem::new);
    public static final RegistrySupplier<ComponentItem> COMPONENT_FLASH_CHIP = register("flash_chip", new Item.Properties(), ComponentItem::new);
    public static final RegistrySupplier<ComponentItem> COMPONENT_SOLID_STATE_DRIVE = register("solid_state_drive", new Item.Properties(), ComponentItem::new);
    public static final RegistrySupplier<ComponentItem> COMPONENT_BATTERY = register("battery", new Item.Properties(), ComponentItem::new);
    public static final RegistrySupplier<ComponentItem> COMPONENT_SCREEN = register("screen", new Item.Properties(), ComponentItem::new);
    public static final RegistrySupplier<ComponentItem> COMPONENT_CONTROLLER_UNIT = register("controller_unit", new Item.Properties(), ComponentItem::new);
    public static final RegistrySupplier<ComponentItem> COMPONENT_SMALL_ELECTRIC_MOTOR = register("small_electric_motor", new Item.Properties(), ComponentItem::new);
    public static final RegistrySupplier<ComponentItem> COMPONENT_CARRIAGE = register("carriage", new Item.Properties(), ComponentItem::new);

    public static final RegistrySupplier<EthernetCableItem> ETHERNET_CABLE = register("ethernet_cable", new Item.Properties(), EthernetCableItem::new);
    
    private static <T extends Item> RegistrySupplier<T> register(String id, Item.Properties properties, Function<Item.Properties, T> factory) {
        return REGISTER.register(OmnixerioDevicesMod.id(id), () -> factory.apply(properties));
    }
    
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
