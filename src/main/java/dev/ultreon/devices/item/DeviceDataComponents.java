package dev.ultreon.devices.item;

import dev.ultreon.devices.UltreonDevicesCommon;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DeviceDataComponents {
    private static final DeferredRegister<DataComponentType<?>> REGISTER = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, UltreonDevicesCommon.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EthernetConnection>> ETHERNET_CONNECTION = REGISTER.register("ethernet_connection", () -> DataComponentType.<EthernetConnection>builder().persistent(EthernetConnection.CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DriveComponent>> DRIVE = REGISTER.register("drive_component", () -> DataComponentType.<DriveComponent>builder().persistent(DriveComponent.CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MotherboardComponents>> MOTHERBOARD_COMPONENTS = REGISTER.register("motherboard_components", () -> DataComponentType.<MotherboardComponents>builder().persistent(MotherboardComponents.CODEC).build());

    public static void register(IEventBus modBus) {
        REGISTER.register(modBus);
    }
}
