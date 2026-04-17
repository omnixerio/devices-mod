package dev.ultreon.devices.core.laptop.common;

import dev.ultreon.devices.OmnixerioDevicesCommon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public record ClientboundUpdatePacket(Tag nbt) implements CustomPacketPayload {
    public static final Type<ClientboundUpdatePacket> TYPE = new Type<>(OmnixerioDevicesCommon.id("s2c_update"));
    public static final StreamCodec<FriendlyByteBuf, ClientboundUpdatePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.tagCodec(NbtAccounter::unlimitedHeap), ClientboundUpdatePacket::nbt,
            ClientboundUpdatePacket::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public ClientboundUpdatePacket(UUID laptop, String type, CompoundTag nbt) {
        var tag = new CompoundTag();
        tag.putLongArray("uuid", new long[]{laptop.getMostSignificantBits(), laptop.getLeastSignificantBits()});
        tag.putString("type", type);
        tag.put("data", nbt);
        this(tag);
    }
}
