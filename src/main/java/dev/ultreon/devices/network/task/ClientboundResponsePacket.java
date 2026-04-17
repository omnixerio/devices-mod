package dev.ultreon.devices.network.task;

import dev.ultreon.devices.OmnixerioDevicesCommon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record ClientboundResponsePacket(int id, String request, boolean successful, CompoundTag tag) implements CustomPacketPayload {
    public static final Type<ClientboundResponsePacket> TYPE = new Type<>(OmnixerioDevicesCommon.id("task_response"));
    public static final StreamCodec<FriendlyByteBuf, ClientboundResponsePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClientboundResponsePacket::id,
            ByteBufCodecs.STRING_UTF8, ClientboundResponsePacket::request,
            ByteBufCodecs.BOOL, ClientboundResponsePacket::successful,
            ByteBufCodecs.compoundTagCodec(NbtAccounter::unlimitedHeap), ClientboundResponsePacket::tag,
            ClientboundResponsePacket::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
