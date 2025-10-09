package dev.ultreon.devices.api.device;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public interface DeviceOrigin {
    static DeviceOrigin read(RegistryFriendlyByteBuf buf) {
        return DeviceSerializer.fromId(buf.readVarInt()).decode(buf);
    }

    DeviceSerializer getSerializer();
    Device locate(MinecraftServer server, ServerLevel level, ServerPlayer player);
}
