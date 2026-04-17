package dev.ultreon.devices.network.task;

import dev.ultreon.devices.OmnixerioDevicesCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

/**
 * @author MrCrayfish
 */
public record SyncBlockPacket(BlockPos pos) implements CustomPacketPayload {
    public static final Type<SyncBlockPacket> TYPE = new Type<>(OmnixerioDevicesCommon.id("sync_block"));
    public static final StreamCodec<FriendlyByteBuf, SyncBlockPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SyncBlockPacket::pos,
            SyncBlockPacket::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
