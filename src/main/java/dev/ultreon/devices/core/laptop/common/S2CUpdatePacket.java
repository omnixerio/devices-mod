package dev.ultreon.devices.core.laptop.common;

import dev.ultreon.devices.UltreonDevicesCommon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.UUID;

public record S2CUpdatePacket(Tag nbt) implements CustomPacketPayload {
    public static final Type<S2CUpdatePacket> TYPE = new Type<>(UltreonDevicesCommon.id("s2c_update"));
    public static final StreamCodec<FriendlyByteBuf, S2CUpdatePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.tagCodec(NbtAccounter::unlimitedHeap), S2CUpdatePacket::nbt,
            S2CUpdatePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public S2CUpdatePacket(UUID laptop, String type, CompoundTag nbt) {
        var tag = new CompoundTag();
        tag.putLongArray("uuid", new long[]{laptop.getMostSignificantBits(), laptop.getLeastSignificantBits()});
        tag.putString("type", type);
        tag.put("data", nbt);
        this(tag);
    }
}
