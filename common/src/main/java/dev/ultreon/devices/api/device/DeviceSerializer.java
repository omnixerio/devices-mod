package dev.ultreon.devices.api.device;

import dev.ultreon.devices.UltreonDevices;
import net.minecraft.network.RegistryFriendlyByteBuf;

public interface DeviceSerializer {
    static DeviceSerializer fromId(int id) {
        return UltreonDevices.getRegistries().deviceSerializer().registry().byId(id);
    }

    void encode(RegistryFriendlyByteBuf buf, DeviceOrigin deviceOrigin);
    default void encodeFull(RegistryFriendlyByteBuf buf, DeviceOrigin deviceOrigin) {
        int id = UltreonDevices.getRegistries().deviceSerializer().registry().getId(this);
        buf.writeInt(id);
        encode(buf, deviceOrigin);
    }

    DeviceOrigin decode(RegistryFriendlyByteBuf buf);
    default DeviceOrigin decodeFull(RegistryFriendlyByteBuf buf) {
        int id = buf.readInt();
        return decode(buf);
    }

    void encodeRemote(RegistryFriendlyByteBuf buf, RemoteDeviceOrigin deviceOrigin);
    default void encodeFullRemote(RegistryFriendlyByteBuf buf, RemoteDeviceOrigin deviceOrigin) {
        int id = UltreonDevices.getRegistries().deviceSerializer().registry().getId(this);
    }

    RemoteDeviceOrigin decodeRemote(RegistryFriendlyByteBuf buf);
    default RemoteDeviceOrigin decodeFullRemote(RegistryFriendlyByteBuf buf) {
        int id = buf.readInt();
        return decodeRemote(buf);
    }

    default int getId() {
        return UltreonDevices.getRegistries().deviceSerializer().registry().getId(this);
    }
}
