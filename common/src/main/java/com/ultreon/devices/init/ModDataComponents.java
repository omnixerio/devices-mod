package com.ultreon.devices.init;

import com.ultreon.devices.OmnixerioDevicesMod;
import com.ultreon.devices.Reference;
import com.ultreon.devices.item.data.EthernetConnection;
import com.ultreon.devices.item.data.ExternalDriveComponent;
import com.ultreon.devices.item.data.FlashDriveComponent;
import com.ultreon.devices.item.data.MotherboardComponents;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;

import java.util.function.Supplier;

public class ModDataComponents {
    private static final Registrar<DataComponentType<?>> REGISTER = OmnixerioDevicesMod.REGISTRIES.get().get(Registries.DATA_COMPONENT_TYPE);

    public static final RegistrySupplier<DataComponentType<FlashDriveComponent>> FLASH_DRIVE = register("flash_drive", () -> DataComponentType.<FlashDriveComponent>builder().persistent(FlashDriveComponent.CODEC).build());
    public static final RegistrySupplier<DataComponentType<ExternalDriveComponent>> EXTERNAL_DRIVE = register("external_drive", () -> DataComponentType.<ExternalDriveComponent>builder().persistent(ExternalDriveComponent.CODEC).build());
    public static final RegistrySupplier<DataComponentType<EthernetConnection>> ETHERNET_CONNECTION = register("ethernet_connection", () -> DataComponentType.<EthernetConnection>builder().persistent(EthernetConnection.CODEC).build());
    public static final RegistrySupplier<DataComponentType<MotherboardComponents>> MOTHERBOARD_COMPONENTS = register("motherboard_components", () -> DataComponentType.<MotherboardComponents>builder().persistent(MotherboardComponents.CODEC).build());

    private static <T extends DataComponentType<?>> RegistrySupplier<T> register(String id, Supplier<T> supplier) {
        return REGISTER.register(OmnixerioDevicesMod.id(id), supplier);
    }

    public static void register() {

    }
}
