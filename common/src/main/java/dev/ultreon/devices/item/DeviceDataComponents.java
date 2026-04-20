package dev.ultreon.devices.item;

import dev.ultreon.devices.platform.Services;
import dev.ultreon.devices.platform.services.RegistrySupplier;
import net.minecraft.core.component.DataComponentType;

import java.util.function.Supplier;

public class DeviceDataComponents {
    public static final RegistrySupplier<DataComponentType<EthernetConnection>> ETHERNET_CONNECTION = register("ethernet_connection", () -> DataComponentType.<EthernetConnection>builder().persistent(EthernetConnection.CODEC).build());
    public static final RegistrySupplier<DataComponentType<DriveComponent>> DRIVE = register("drive_component", () -> DataComponentType.<DriveComponent>builder().persistent(DriveComponent.CODEC).build());
    public static final RegistrySupplier<DataComponentType<MotherboardComponents>> MOTHERBOARD_COMPONENTS = register("motherboard_components", () -> DataComponentType.<MotherboardComponents>builder().persistent(MotherboardComponents.CODEC).build());

    private static <T> RegistrySupplier<DataComponentType<T>> register(String name, Supplier<DataComponentType<T>> type) {
        return Services.PLATFORM.registerDataComponent(name, type);
    }

    public static void register() {

    }
}
