package dev.ultreon.devices.init;

import dev.ultreon.devices.OmnixerioDevicesCommon;
import dev.ultreon.devices.block.LaptopBlock;
import dev.ultreon.devices.block.OfficeChairBlock;
import dev.ultreon.devices.block.PrinterBlock;
import dev.ultreon.devices.block.RouterBlock;
import dev.ultreon.devices.item.FlashDriveItem;
import dev.ultreon.devices.platform.Services;
import dev.ultreon.devices.platform.services.RegistrySupplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

public class DeviceCreativeTabs {
    public static final ResourceKey<CreativeModeTab> CUSTOM_CREATIVE_TAB_KEY = ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(OmnixerioDevicesCommon.MOD_ID, "creative_tab")
    );
    public static final RegistrySupplier<CreativeModeTab> DEVICE_TAB = Services.PLATFORM.registerCreativeModeTab("devices", Component.literal("Omnixerio Devices Mod"), (parameters, output) -> {
        for (RegistrySupplier<LaptopBlock> laptop : DeviceBlocks.LAPTOPS) {
            output.accept(laptop.get());
        }
        output.accept(DeviceBlocks.MAC_MAX_X.get());
        for (RegistrySupplier<PrinterBlock> printer : DeviceBlocks.PRINTERS) {
            output.accept(printer.get());
        }
        for (RegistrySupplier<RouterBlock> router : DeviceBlocks.ROUTERS) {
            output.accept(router.get());
        }
        for (RegistrySupplier<OfficeChairBlock> office_chair : DeviceBlocks.OFFICE_CHAIRS) {
            output.accept(office_chair.get());
        }
        for (RegistrySupplier<FlashDriveItem> flashDrive : DeviceItems.FLASH_DRIVE) {
            output.accept(flashDrive.get());
        }
        output.accept(DeviceItems.COMPONENT_CPU.get());
        output.accept(DeviceItems.COMPONENT_SOLID_STATE_DRIVE.get());
        output.accept(DeviceItems.COMPONENT_GPU.get());
        output.accept(DeviceItems.COMPONENT_RAM.get());
        output.accept(DeviceItems.COMPONENT_HARD_DRIVE.get());
        output.accept(DeviceItems.COMPONENT_BATTERY.get());
        output.accept(DeviceItems.COMPONENT_SCREEN.get());
        output.accept(DeviceItems.COMPONENT_WIFI.get());
        output.accept(DeviceItems.COMPONENT_CARRIAGE.get());
        output.accept(DeviceItems.COMPONENT_FLASH_CHIP.get());
        output.accept(DeviceItems.COMPONENT_CIRCUIT_BOARD.get());
        output.accept(DeviceItems.COMPONENT_CONTROLLER_UNIT.get());
        output.accept(DeviceItems.COMPONENT_SMALL_ELECTRIC_MOTOR.get());
        output.accept(DeviceItems.COMPONENT_MOTHERBOARD.get());
    });

    public static void register() {

    }
}
