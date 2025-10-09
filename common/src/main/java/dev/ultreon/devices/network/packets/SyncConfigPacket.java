package dev.ultreon.devices.network.packets;

import dev.ultreon.devices.DeviceConfig;
import dev.ultreon.mods.xinexlib.network.Networker;
import dev.ultreon.mods.xinexlib.network.packet.PacketToClient;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.Objects;

/// @author MrCrayfish
public class SyncConfigPacket implements PacketToClient<SyncConfigPacket> {
    public SyncConfigPacket() {

    }

    public SyncConfigPacket(RegistryFriendlyByteBuf buf) {
        DeviceConfig.readSyncTag(Objects.requireNonNull(buf.readNbt()));
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeNbt(DeviceConfig.writeSyncTag());
    }

    @Override
    public void handle(Networker networker) {

    }
}
