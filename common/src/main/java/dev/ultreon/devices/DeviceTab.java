package dev.ultreon.devices;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredSupplier;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.ultreon.devices.init.ModItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import static dev.ultreon.devices.OmnixerioDevicesMod.id;

public class DeviceTab {
    @SuppressWarnings("UnstableApiUsage")
    public static DeferredSupplier<CreativeModeTab> create() {
        OmnixerioDevicesMod.LOGGER.info("Creating Creative Tab...");
        DeferredSupplier<CreativeModeTab> devicesTabDevice = CreativeTabRegistry.defer(id("devices_tab_device")); //TODO () -> new ItemStack(DeviceBlocks.LAPTOPS.of(DyeColor.RED).get()
        CreativeTabRegistry.modify(devicesTabDevice, (flags, output, canUseGameMasterBlocks) -> {
            for (RegistrySupplier<Item> laptop : ModItems.LAPTOPS) {
                output.accept(laptop.get());
            }
            output.accept(ModItems.MAC_MAX_X.get());
            for (RegistrySupplier<Item> printer : ModItems.PRINTERS) {
                output.accept(printer.get());
            }
            for (RegistrySupplier<Item> router : ModItems.ROUTERS) {
                output.accept(router.get());
            }
            for (RegistrySupplier<Item> office_chair : ModItems.OFFICE_CHAIRS) {
                output.accept(office_chair.get());
            }
            for (RegistrySupplier<Item> flashdrive : ModItems.FLASH_DRIVE) {
                output.accept(flashdrive.get());
            }
            output.accept(ModItems.COMPONENT_CPU.get());
            output.accept(ModItems.COMPONENT_SOLID_STATE_DRIVE.get());
            output.accept(ModItems.COMPONENT_GPU.get());
            output.accept(ModItems.COMPONENT_RAM.get());
            output.accept(ModItems.COMPONENT_HARD_DRIVE.get());
            output.accept(ModItems.COMPONENT_BATTERY.get());
            output.accept(ModItems.COMPONENT_SCREEN.get());
            output.accept(ModItems.COMPONENT_WIFI.get());
            output.accept(ModItems.COMPONENT_CARRIAGE.get());
            output.accept(ModItems.COMPONENT_FLASH_CHIP.get());
            output.accept(ModItems.COMPONENT_CIRCUIT_BOARD.get());
            output.accept(ModItems.COMPONENT_CONTROLLER_UNIT.get());
            output.accept(ModItems.COMPONENT_SMALL_ELECTRIC_MOTOR.get());
            output.accept(ModItems.COMPONENT_MOTHERBOARD.get());
        });
        return devicesTabDevice;
    }
}
