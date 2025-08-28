package com.ultreon.devices.network.task;

import com.ultreon.devices.DeviceConfig;
import com.ultreon.devices.network.Packet;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author MrCrayfish
 */
public class SyncConfigPacket extends Packet<SyncConfigPacket> {
    public SyncConfigPacket() {

    }

    public SyncConfigPacket(FriendlyByteBuf buf) {
        DeviceConfig.readSyncTag(Objects.requireNonNull(buf.readNbt()));
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(DeviceConfig.writeSyncTag());
    }

    @Override
    public boolean onMessage(Supplier<NetworkManager.PacketContext> ctx) {
        return true;
    }
}
