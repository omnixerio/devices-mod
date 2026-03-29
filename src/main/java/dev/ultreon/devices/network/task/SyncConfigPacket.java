package dev.ultreon.devices.network.task;

import dev.ultreon.devices.UltreonDevicesCommon;
import dev.ultreon.mods.xinexlib.network.Networker;
import dev.ultreon.mods.xinexlib.network.packet.PacketToClient;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

/**
 * @author MrCrayfish
 */
public record SyncConfigPacket(Tag syncData) implements CustomPacketPayload {
    public static final Type<SyncConfigPacket> TYPE = new Type<>(UltreonDevicesCommon.id("sync_config"));
    public static final StreamCodec<FriendlyByteBuf, SyncConfigPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.tagCodec(NbtAccounter::unlimitedHeap), SyncConfigPacket::syncData,
            SyncConfigPacket::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
