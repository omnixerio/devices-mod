package com.ultreon.devices.item;

import com.ultreon.devices.Devices;
import dev.ultreon.mods.xinexlib.registrar.Registrar;
import dev.ultreon.mods.xinexlib.registrar.RegistrySupplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;

public class DeviceDataComponents {
    private static final Registrar<DataComponentType<?>> REGISTRY = Devices.REGISTRIES.get().getRegistrar(Registries.DATA_COMPONENT_TYPE);

    public static final RegistrySupplier<DataComponentType<EthernetConnection>, DataComponentType<?>> ETHERNET_CONNECTION = REGISTRY.register("ethernet_connection", () -> DataComponentType.<EthernetConnection>builder().persistent(EthernetConnection.CODEC).build());

    public static void register() {

    }
}
