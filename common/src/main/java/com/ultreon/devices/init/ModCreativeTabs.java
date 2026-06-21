package com.ultreon.devices.init;

import com.ultreon.devices.OmnixerioDevicesMod;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class ModCreativeTabs {
    private static final Registrar<CreativeModeTab> REGISTER = OmnixerioDevicesMod.REGISTRIES.get().get(Registries.CREATIVE_MODE_TAB);
    public static final RegistrySupplier<CreativeModeTab> TAB_DEVICE = register("device", () -> CreativeTabRegistry.create(builder -> {
        builder.icon(() -> new ItemStack(ModBlocks.LAPTOPS.of(DyeColor.RED).get()));
        builder.title(Component.translatable("itemGroup.devices.devices"));
        builder.displayItems((parameters, output) -> {
            ModItems.LAPTOPS.forEach(laptop -> output.accept(laptop.get()));
            output.accept(ModItems.MAC_MAX_X.get());
            ModItems.PRINTERS.forEach(printer -> output.accept(printer.get()));
            ModItems.ROUTERS.forEach(router -> output.accept(router.get()));
        });
    }));
    public static final RegistrySupplier<CreativeModeTab> TAB_UTILITY = register("utility", () -> CreativeTabRegistry.create(builder -> {
        builder.icon(() -> new ItemStack(ModItems.FLASH_DRIVE.of(DyeColor.RED).get()));
        builder.title(Component.translatable("itemGroup.devices.utility"));
        builder.displayItems((parameters, output) -> {
            ModItems.FLASH_DRIVE.forEach(flashdrive -> output.accept(flashdrive.get()));
            ModItems.OFFICE_CHAIRS.forEach(seat -> output.accept(seat.get()));
            output.accept(ModItems.ETHERNET_CABLE.get());
        });
    }));
    public static final RegistrySupplier<CreativeModeTab> TAB_COMPONENTS = register("components", () -> CreativeTabRegistry.create(builder -> {
        builder.icon(() -> new ItemStack(ModItems.COMPONENT_CIRCUIT_BOARD.get()));
        builder.title(Component.translatable("itemGroup.devices.components"));
        builder.displayItems((parameters, output) -> {
            output.accept(ModItems.COMPONENT_CIRCUIT_BOARD.get());
            output.accept(ModItems.COMPONENT_CPU.get());
            output.accept(ModItems.COMPONENT_GPU.get());
            output.accept(ModItems.COMPONENT_RAM.get());
            output.accept(ModItems.COMPONENT_WIFI.get());
            output.accept(ModItems.COMPONENT_HARD_DRIVE.get());
            output.accept(ModItems.COMPONENT_SOLID_STATE_DRIVE.get());
            output.accept(ModItems.COMPONENT_MOTHERBOARD.get());
            output.accept(ModItems.COMPONENT_MOTHERBOARD_FULL.get());
            output.accept(ModItems.COMPONENT_FLASH_CHIP.get());
            output.accept(ModItems.COMPONENT_SCREEN.get());
            output.accept(ModItems.COMPONENT_BATTERY.get());
            output.accept(ModItems.COMPONENT_CARRIAGE.get());
            output.accept(ModItems.COMPONENT_SMALL_ELECTRIC_MOTOR.get());
            output.accept(ModItems.COMPONENT_CONTROLLER_UNIT.get());
        });
    }));
    public static final RegistrySupplier<CreativeModeTab> TAB_INGREDIENTS = register("ingredients", () -> CreativeTabRegistry.create(builder -> {
        builder.icon(() -> new ItemStack(ModItems.PLASTIC_UNREFINED.get()));
        builder.title(Component.translatable("itemGroup.devices.ingredients"));
        builder.displayItems((parameters, output) -> {
            output.accept(ModItems.PLASTIC_UNREFINED.get());
            output.accept(ModItems.PLASTIC.get());
            output.accept(ModItems.PLASTIC_FRAME.get());
            output.accept(ModItems.GLASS_DUST.get());
            output.accept(ModItems.WHEEL.get());
        });
    }));

    private static <T extends CreativeModeTab> RegistrySupplier<T> register(String id, Supplier<T> supplier) {
        return REGISTER.register(OmnixerioDevicesMod.id(id), supplier);
    }

    public static void register() {

    }
}
