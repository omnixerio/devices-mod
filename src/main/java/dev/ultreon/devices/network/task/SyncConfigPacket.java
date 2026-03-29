package dev.ultreon.devices.network.task;

import dev.ultreon.devices.DeviceConfig;
import dev.ultreon.mods.xinexlib.network.Networker;
import dev.ultreon.mods.xinexlib.network.packet.PacketToClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author MrCrayfish
 */
public class SyncConfigPacket implements PacketToClient<SyncConfigPacket> {

    private @NotNull CompoundTag syncData;

    public SyncConfigPacket(CompoundTag syncData) {
        this.syncData = syncData;
    }

    public SyncConfigPacket() {
        this(DeviceConfig.writeSyncTag());
    }

    public SyncConfigPacket(FriendlyByteBuf buf) {
        syncData = Objects.requireNonNull(buf.readNbt());
    }

    @Override
    public void handle(Networker connection) {
        DeviceConfig.readSyncTag(syncData);
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeNbt(syncData);
    }
}
