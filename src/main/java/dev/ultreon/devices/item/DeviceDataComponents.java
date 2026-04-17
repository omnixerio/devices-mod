package dev.ultreon.devices.item;

import dev.ultreon.devices.OmnixerioDevicesCommon;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

public class DeviceDataComponents {
    public static final DataComponentType<EthernetConnection> ETHERNET_CONNECTION = register("ethernet_connection", DataComponentType.<EthernetConnection>builder().persistent(EthernetConnection.CODEC).build());
    public static final DataComponentType<DriveComponent> DRIVE = register("drive_component", DataComponentType.<DriveComponent>builder().persistent(DriveComponent.CODEC).build());
    public static final DataComponentType<MotherboardComponents> MOTHERBOARD_COMPONENTS = register("motherboard_components", DataComponentType.<MotherboardComponents>builder().persistent(MotherboardComponents.CODEC).build());

    private static <T> DataComponentType<T> register(String name, DataComponentType<T> type) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, OmnixerioDevicesCommon.id(name), type);
    }

    public static void register() {

    }
}
