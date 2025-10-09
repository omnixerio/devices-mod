package dev.ultreon.devices.api.device;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;

public interface RemoteDeviceOrigin {
    static RemoteDeviceOrigin read(RegistryFriendlyByteBuf buf) {
        return DeviceSerializer.fromId(buf.readVarInt()).decodeRemote(buf);
    }

    DeviceSerializer getSerializer();
    RemoteDevice locate(Minecraft client, ClientLevel level);
}
