package dev.ultreon.devices.init;

import dev.ultreon.devices.DeviceTab;
import dev.ultreon.devices.UltreonDevicesCommon;
import dev.ultreon.mods.xinexlib.registrar.Registrar;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DeviceCreativeTabs {
    private static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, UltreonDevicesCommon.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DEVICE_TAB = REGISTER.register("device_tab", () -> CreativeModeTab.builder()
            .title(Component.literal("Omnixerio Devices Mod"))
            .displayItems((_, output) -> {
                for (DeferredHolder<Item, ? extends Item> laptop : DeviceItems.LAPTOPS) {
                    output.accept(laptop.get());
                }
                output.accept(DeviceItems.MAC_MAX_X.get());
                for (DeferredHolder<Item, ? extends Item> printer : DeviceItems.PRINTERS) {
                    output.accept(printer.get());
                }
                for (DeferredHolder<Item, ? extends Item> router : DeviceItems.ROUTERS) {
                    output.accept(router.get());
                }
                for (DeferredHolder<Item, ? extends Item> office_chair : DeviceItems.OFFICE_CHAIRS) {
                    output.accept(office_chair.get());
                }
                for (DeferredHolder<Item, ? extends Item> flashdrive : DeviceItems.FLASH_DRIVE) {
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
            }).build());
}
