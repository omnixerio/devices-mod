package dev.ultreon.devices.network.task;

import dev.ultreon.devices.UltreonDevicesCommon;
import dev.ultreon.devices.block.entity.RouterBlockEntity;
import dev.ultreon.mods.xinexlib.network.Networker;
import dev.ultreon.mods.xinexlib.network.packet.PacketToServer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * @author MrCrayfish
 */
public record SyncBlockPacket(BlockPos pos) implements CustomPacketPayload {
    public static final Type<SyncBlockPacket> TYPE = new Type<>(UltreonDevicesCommon.id("sync_block"));
    public static final StreamCodec<FriendlyByteBuf, SyncBlockPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SyncBlockPacket::pos,
            SyncBlockPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
