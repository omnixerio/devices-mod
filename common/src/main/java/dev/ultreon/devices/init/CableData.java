package dev.ultreon.devices.init;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.UUID;

public record CableData(
    BlockPos pos,
    UUID id,
    String name
) {
    public static void write(RegistryFriendlyByteBuf registryFriendlyByteBuf, CableData o) {
        registryFriendlyByteBuf.writeBlockPos(o.pos());
        registryFriendlyByteBuf.writeUUID(o.id());
        registryFriendlyByteBuf.writeUtf(o.name());
    }

    public static CableData read(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        return new CableData(registryFriendlyByteBuf.readBlockPos(), registryFriendlyByteBuf.readUUID(), registryFriendlyByteBuf.readUtf());
    }
}
