package dev.ultreon.devices.init;

import dev.ultreon.devices.UltreonDevicesCommon;
import dev.ultreon.devices.ModDeviceTypes;
import dev.ultreon.devices.item.*;
import dev.ultreon.devices.util.DyeableRegistration;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class DeviceItems {
    private static final DeferredRegister<Item> REGISTER = DeferredRegister.create(Registries.ITEM, UltreonDevicesCommon.MOD_ID);

    // Laptops
    public static final DyeableRegistration<Item> LAPTOPS = new DyeableRegistration<>(REGISTER) {
        @Override
        public DeferredHolder<Item, ? extends Item> register(DeferredRegister<Item> DeferredRegister, DyeColor color) {
            return DeferredRegister.register(color.getName() + "_laptop", () -> new ColoredDeviceItem(DeviceBlocks.LAPTOPS.of(color).get(), new Item.Properties().setId(ResourceKey.create(Registries.ITEM, UltreonDevicesCommon.id(color.getName() + "_laptop"))), color, ModDeviceTypes.COMPUTER));
        }

    };

    // Custom Computers
    public static final DeferredHolder<Item, DeviceItem> MAC_MAX_X = REGISTER.register("mac_max_x", () -> new DeviceItem(DeviceBlocks.MAC_MAX_X.get(), new Item.Properties(), ModDeviceTypes.COMPUTER) {
        @NotNull
        public Component getDescription() {
            MutableComponent normalName = Component.translatable("block.devices.mac_max_x");
            if (ModList.get().isLoaded("emojiful")) {
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
    public static final DyeableRegistration<Item> PRINTERS = new DyeableRegistration<>(REGISTER) {
        @Override
        public DeferredHolder<Item, ? extends Item> register(DeferredRegister<Item> DeferredRegister, DyeColor color) {
            return DeferredRegister.register(color.getName() + "_printer", () -> new ColoredDeviceItem(DeviceBlocks.PRINTERS.of(color).get(), new Item.Properties().setId(
                    ResourceKey.create(Registries.ITEM, UltreonDevicesCommon.id(color.getName() + "_printer"))
            ), color, ModDeviceTypes.PRINTER));
        }

    };

    // Routers
    public static final DyeableRegistration<Item> ROUTERS = new DyeableRegistration<>(REGISTER) {
        @Override
        public DeferredHolder<Item, ? extends Item> register(DeferredRegister<Item> DeferredRegister, DyeColor color) {
            return DeferredRegister.register(color.getName() + "_router", () -> new ColoredDeviceItem(DeviceBlocks.ROUTERS.of(color).get(), new Item.Properties().setId(
                    ResourceKey.create(Registries.ITEM, UltreonDevicesCommon.id(color.getName() + "_router"))
            ), color, ModDeviceTypes.ROUTER));
        }

    };

    // Office Chairs
    public static final DyeableRegistration<Item> OFFICE_CHAIRS = new DyeableRegistration<>(REGISTER) {
        @Override
        public DeferredHolder<Item, ? extends Item> register(DeferredRegister<Item> DeferredRegister, DyeColor color) {
            return DeferredRegister.register(color.getName() + "_office_chair", () -> new ColoredDeviceItem(DeviceBlocks.OFFICE_CHAIRS.of(color).get(), new Item.Properties().setId(
                    ResourceKey.create(Registries.ITEM, UltreonDevicesCommon.id(color.getName() + "_office_chair"))
            ), color, ModDeviceTypes.SEAT));
        }

    };

    // Flash drives
    public static final DyeableRegistration<Item> FLASH_DRIVE = new DyeableRegistration<>(REGISTER) {
        @Override
        public DeferredHolder<Item, ? extends Item> register(DeferredRegister<Item> DeferredRegister, DyeColor color) {
            return DeferredRegister.register(color.getName() + "_flash_drive", () -> new FlashDriveItem(color, new Item.Properties().setId(
                    ResourceKey.create(Registries.ITEM, UltreonDevicesCommon.id(color.getName() + "_flash_drive"))
            )));
        }

    };

    public static final DeferredHolder<Item, BlockItem> PAPER = REGISTER.register("paper", () -> new BlockItem(DeviceBlocks.PAPER.get(), new Item.Properties()));

    public static final DeferredHolder<Item, BasicItem> PLASTIC_UNREFINED = REGISTER.register("plastic_unrefined", () -> new BasicItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, UltreonDevicesCommon.id("plastic_unrefined")))));
    public static final DeferredHolder<Item, BasicItem> PLASTIC = REGISTER.register("plastic", () -> new BasicItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, UltreonDevicesCommon.id("plastic")))));
    public static final DeferredHolder<Item, BasicItem> PLASTIC_FRAME = REGISTER.register("plastic_frame", () -> new BasicItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, UltreonDevicesCommon.id("plastic_frame")))));
    public static final DeferredHolder<Item, BasicItem> WHEEL = REGISTER.register("wheel", () -> new BasicItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, UltreonDevicesCommon.id("wheel")))));
    public static final DeferredHolder<Item, Item> GLASS_DUST = REGISTER.register("glass_dust", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, UltreonDevicesCommon.id("glass_dust")))));

    public static final DeferredHolder<Item, ComponentItem> COMPONENT_CIRCUIT_BOARD = REGISTER.register("circuit_board", () -> new ComponentItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, UltreonDevicesCommon.id("circuit_board")))));
    public static final DeferredHolder<Item, ComponentItem> COMPONENT_MOTHERBOARD = REGISTER.register("motherboard", () -> new MotherboardItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, UltreonDevicesCommon.id("motherboard")))));
    public static final DeferredHolder<Item, ComponentItem> COMPONENT_MOTHERBOARD_FULL = REGISTER.register("motherboard_full", () -> new ComponentItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, UltreonDevicesCommon.id("motherboard_full")))));
    public static final DeferredHolder<Item, ComponentItem> COMPONENT_CPU = REGISTER.register("cpu", () -> new MotherboardItem.Component(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, UltreonDevicesCommon.id("cpu")))));
    public static final DeferredHolder<Item, ComponentItem> COMPONENT_RAM = REGISTER.register("ram", () -> new MotherboardItem.Component(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, UltreonDevicesCommon.id("ram")))));
    public static final DeferredHolder<Item, ComponentItem> COMPONENT_GPU = REGISTER.register("gpu", () -> new MotherboardItem.Component(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, UltreonDevicesCommon.id("gpu")))));
    public static final DeferredHolder<Item, ComponentItem> COMPONENT_WIFI = REGISTER.register("wifi", () -> new MotherboardItem.Component(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, UltreonDevicesCommon.id("wifi")))));
    public static final DeferredHolder<Item, ComponentItem> COMPONENT_HARD_DRIVE = REGISTER.register("hard_drive", () -> new ComponentItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, UltreonDevicesCommon.id("hard_drive")))));
    public static final DeferredHolder<Item, ComponentItem> COMPONENT_FLASH_CHIP = REGISTER.register("flash_chip", () -> new ComponentItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, UltreonDevicesCommon.id("flash_chip")))));
    public static final DeferredHolder<Item, ComponentItem> COMPONENT_SOLID_STATE_DRIVE = REGISTER.register("solid_state_drive", () -> new ComponentItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, UltreonDevicesCommon.id("solid_state_drive")))));
    public static final DeferredHolder<Item, ComponentItem> COMPONENT_BATTERY = REGISTER.register("battery", () -> new ComponentItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, UltreonDevicesCommon.id("battery")))));
    public static final DeferredHolder<Item, ComponentItem> COMPONENT_SCREEN = REGISTER.register("screen", () -> new ComponentItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, UltreonDevicesCommon.id("screen")))));
    public static final DeferredHolder<Item, ComponentItem> COMPONENT_CONTROLLER_UNIT = REGISTER.register("controller_unit", () -> new ComponentItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, UltreonDevicesCommon.id("controller_unit")))));
    public static final DeferredHolder<Item, ComponentItem> COMPONENT_SMALL_ELECTRIC_MOTOR = REGISTER.register("small_electric_motor", () -> new ComponentItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, UltreonDevicesCommon.id("small_electric_motor")))));
    public static final DeferredHolder<Item, ComponentItem> COMPONENT_CARRIAGE = REGISTER.register("carriage", () -> new ComponentItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, UltreonDevicesCommon.id("carriage")))));

    public static final DeferredHolder<Item, EthernetCableItem> ETHERNET_CABLE = REGISTER.register("ethernet_cable", EthernetCableItem::new);


    public static Stream<Item> getAllItems() {
        return REGISTER.getRegistry().get().stream();
    }

    public static @NonNull FlashDriveItem getFlashDriveByColor(DyeColor color) {
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
