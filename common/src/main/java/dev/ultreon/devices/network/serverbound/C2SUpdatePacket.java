package dev.ultreon.devices.network.serverbound;

import dev.ultreon.devices.OmnixerioDevicesMod;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record C2SUpdatePacket(
        UUID laptop,
        String typeName,
        CompoundTag data
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<C2SUpdatePacket> TYPE = new CustomPacketPayload.Type<>(OmnixerioDevicesMod.id("serverbound/update"));
    public static final StreamCodec<RegistryFriendlyByteBuf, C2SUpdatePacket> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, C2SUpdatePacket::laptop,
            ByteBufCodecs.stringUtf8(64), C2SUpdatePacket::typeName,
            ByteBufCodecs.COMPOUND_TAG, C2SUpdatePacket::data,
            C2SUpdatePacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
