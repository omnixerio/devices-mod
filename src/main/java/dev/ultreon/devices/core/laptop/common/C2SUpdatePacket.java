package dev.ultreon.devices.core.laptop.common;

import dev.ultreon.devices.UltreonDevicesCommon;
import dev.ultreon.devices.core.laptop.server.ServerLaptop;
import dev.ultreon.mods.xinexlib.network.Networker;
import dev.ultreon.mods.xinexlib.network.packet.PacketToServer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;
import java.util.UUID;

public record C2SUpdatePacket(Tag tag) implements CustomPacketPayload {
    public static final Type<C2SUpdatePacket> TYPE = new Type<>(UltreonDevicesCommon.id("c2s_update"));
    public static final StreamCodec<FriendlyByteBuf, C2SUpdatePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.tagCodec(NbtAccounter::unlimitedHeap), C2SUpdatePacket::tag,
            C2SUpdatePacket::new
    );

    public C2SUpdatePacket(UUID laptop, String type, CompoundTag nbt) {
        CompoundTag tag = new CompoundTag();
        tag.putLongArray("uuid", new long[]{laptop.getMostSignificantBits(), laptop.getLeastSignificantBits()}); // laptop uuid
        tag.putString("type", type);
        tag.put("data", nbt);
        this(tag);
    }

    @Override
    public void handle(Networker connection, ServerPlayer player) {
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeNbt(nbt);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
