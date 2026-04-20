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

public record ServerboundUpdatePacket(Tag tag) implements CustomPacketPayload {
    public static final Type<ServerboundUpdatePacket> TYPE = new Type<>(OmnixerioDevicesCommon.id("c2s_update"));
    public static final StreamCodec<FriendlyByteBuf, ServerboundUpdatePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.tagCodec(NbtAccounter::unlimitedHeap), ServerboundUpdatePacket::tag,
            ServerboundUpdatePacket::new
    );

    public ServerboundUpdatePacket(UUID laptop, String type, CompoundTag nbt) {
        CompoundTag tag = new CompoundTag();
        tag.putLongArray("uuid", new long[]{laptop.getMostSignificantBits(), laptop.getLeastSignificantBits()}); // laptop uuid
        tag.putString("type", type);
        tag.put("data", nbt);
        this(tag);
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
