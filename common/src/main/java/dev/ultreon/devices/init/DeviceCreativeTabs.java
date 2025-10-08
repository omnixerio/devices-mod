package dev.ultreon.devices.init;

import dev.ultreon.devices.Devices;
import dev.ultreon.devices.item.ColoredDeviceItem;
import dev.ultreon.devices.item.FlashDriveItem;
import dev.ultreon.mods.xinexlib.platform.XinexPlatform;
import dev.ultreon.mods.xinexlib.registrar.Registrar;
import dev.ultreon.mods.xinexlib.registrar.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DeviceCreativeTabs {
    private static final Registrar<CreativeModeTab> REGISTER = Devices.REGISTRIES.get().getRegistrar(Registries.CREATIVE_MODE_TAB);

    public static void register() {
        REGISTER.load();
    }

    public static final RegistrySupplier<CreativeModeTab, CreativeModeTab> TAB = REGISTER.register("devices_tab_device", () -> XinexPlatform.creativeTabBuilder()
            .title(Component.translatable("itemGroup.devices.devices_tab_device"))
            .icon(() -> new ItemStack(DeviceBlocks.LAPTOPS.of(DyeColor.RED).get()))
            .displayItems((flags, output) -> {
                for (RegistrySupplier<ColoredDeviceItem, Item> laptop : DeviceItems.LAPTOPS) {
                    output.accept(laptop.get());
                }
                output.accept(DeviceItems.MAC_MAX_X.get());
                for (RegistrySupplier<ColoredDeviceItem, Item> printer : DeviceItems.PRINTERS) {
                    output.accept(printer.get());
                }
                for (RegistrySupplier<ColoredDeviceItem, Item> router : DeviceItems.ROUTERS) {
                    output.accept(router.get());
                }
                for (RegistrySupplier<ColoredDeviceItem, Item> office_chair : DeviceItems.OFFICE_CHAIRS) {
                    output.accept(office_chair.get());
                }
                for (RegistrySupplier<FlashDriveItem, Item> flashdrive : DeviceItems.FLASH_DRIVE) {
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
                output.accept(DeviceItems.ETHERNET_CABLE.get());
                output.accept(DeviceItems.GLASS_DUST.get());
                output.accept(DeviceItems.PLASTIC.get());
                output.accept(DeviceItems.PLASTIC_FRAME.get());
                output.accept(DeviceItems.PLASTIC_UNREFINED.get());
                output.accept(DeviceItems.WHEEL.get());
            })
            .build());

    static {

    }
}
