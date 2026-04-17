package dev.ultreon.devices.init;

import dev.ultreon.devices.OmnixerioDevicesCommon;
import dev.ultreon.devices.block.LaptopBlock;
import dev.ultreon.devices.block.OfficeChairBlock;
import dev.ultreon.devices.block.PrinterBlock;
import dev.ultreon.devices.block.RouterBlock;
import dev.ultreon.devices.item.FlashDriveItem;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

public class DeviceCreativeTabs {
    public static final ResourceKey<CreativeModeTab> CUSTOM_CREATIVE_TAB_KEY = ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(OmnixerioDevicesCommon.MOD_ID, "creative_tab")
    );
    public static final CreativeModeTab DEVICE_TAB = FabricCreativeModeTab.builder()
            .title(Component.literal("Omnixerio Devices Mod"))
            .displayItems((parameters, output) -> {
                for (LaptopBlock laptop : DeviceBlocks.LAPTOPS) {
                    output.accept(laptop);
                }
                output.accept(DeviceBlocks.MAC_MAX_X);
                for (PrinterBlock printer : DeviceBlocks.PRINTERS) {
                    output.accept(printer);
                }
                for (RouterBlock router : DeviceBlocks.ROUTERS) {
                    output.accept(router);
                }
                for (OfficeChairBlock office_chair : DeviceBlocks.OFFICE_CHAIRS) {
                    output.accept(office_chair);
                }
                for (FlashDriveItem flashDrive : DeviceItems.FLASH_DRIVE) {
                    output.accept(flashDrive);
                }
                output.accept(DeviceItems.COMPONENT_CPU);
                output.accept(DeviceItems.COMPONENT_SOLID_STATE_DRIVE);
                output.accept(DeviceItems.COMPONENT_GPU);
                output.accept(DeviceItems.COMPONENT_RAM);
                output.accept(DeviceItems.COMPONENT_HARD_DRIVE);
                output.accept(DeviceItems.COMPONENT_BATTERY);
                output.accept(DeviceItems.COMPONENT_SCREEN);
                output.accept(DeviceItems.COMPONENT_WIFI);
                output.accept(DeviceItems.COMPONENT_CARRIAGE);
                output.accept(DeviceItems.COMPONENT_FLASH_CHIP);
                output.accept(DeviceItems.COMPONENT_CIRCUIT_BOARD);
                output.accept(DeviceItems.COMPONENT_CONTROLLER_UNIT);
                output.accept(DeviceItems.COMPONENT_SMALL_ELECTRIC_MOTOR);
                output.accept(DeviceItems.COMPONENT_MOTHERBOARD);
            })
            .build();

    public static void register() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, CUSTOM_CREATIVE_TAB_KEY, DEVICE_TAB);
    }
}
