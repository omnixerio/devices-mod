package com.ultreon.devices.network.task;

import com.ultreon.devices.DeviceConfig;
import dev.ultreon.mods.xinexlib.network.Networker;
import dev.ultreon.mods.xinexlib.network.packet.PacketToServer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

/**
 * @author MrCrayfish
 */
public class SyncConfigPacket implements PacketToServer<SyncConfigPacket> {
    public SyncConfigPacket() {

    }

    public SyncConfigPacket(FriendlyByteBuf buf) {
        DeviceConfig.readSyncTag(Objects.requireNonNull(buf.readNbt()));
    }

    @Override
    public void handle(Networker connection, ServerPlayer player) {

    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeNbt(DeviceConfig.writeSyncTag());
    }
}
