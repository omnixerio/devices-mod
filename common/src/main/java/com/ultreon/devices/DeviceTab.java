package com.ultreon.devices;

import com.ultreon.devices.init.DeviceItems;
import dev.ultreon.mods.xinexlib.platform.XinexPlatform;
import dev.ultreon.mods.xinexlib.registrar.RegistrySupplier;
import dev.ultreon.mods.xinexlib.tabs.CreativeModeTabBuilder;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class DeviceTab {
    public static CreativeModeTab create() {
        Devices.LOGGER.info("Creating Creative Tab...");
        CreativeModeTabBuilder creativeModeTabBuilder = XinexPlatform.creativeTabBuilder();
        creativeModeTabBuilder.displayItems((itemDisplayParameters, output) -> {
            for (RegistrySupplier<?, Item> laptop : DeviceItems.LAPTOPS) {
                output.accept(laptop.get());
            }
            output.accept(DeviceItems.MAC_MAX_X.get());
            for (RegistrySupplier<?, Item> printer : DeviceItems.PRINTERS) {
                output.accept(printer.get());
            }
            for (RegistrySupplier<?, Item> router : DeviceItems.ROUTERS) {
                output.accept(router.get());
            }
            for (RegistrySupplier<?, Item> office_chair : DeviceItems.OFFICE_CHAIRS) {
                output.accept(office_chair.get());
            }
            for (RegistrySupplier<?, Item> flashdrive : DeviceItems.FLASH_DRIVE) {
                output.accept(flashdrive.get());
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
        return creativeModeTabBuilder.build();
    }
}
