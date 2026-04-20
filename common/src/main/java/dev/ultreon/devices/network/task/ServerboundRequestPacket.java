package dev.ultreon.devices.network.task;

import dev.ultreon.devices.OmnixerioDevicesCommon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record ServerboundRequestPacket(int id, String request, CompoundTag tag) implements CustomPacketPayload {
    public static final Type<ServerboundRequestPacket> TYPE = new Type<>(OmnixerioDevicesCommon.id("task_request"));

    public static final StreamCodec<FriendlyByteBuf, ServerboundRequestPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ServerboundRequestPacket::id,
            ByteBufCodecs.STRING_UTF8, ServerboundRequestPacket::request,
            ByteBufCodecs.compoundTagCodec(NbtAccounter::unlimitedHeap), ServerboundRequestPacket::tag,
            ServerboundRequestPacket::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
