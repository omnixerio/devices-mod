package com.ultreon.devices.network.clientbound;

import com.ultreon.devices.OmnixerioDevicesMod;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record S2CUpdatePacket(
        UUID laptop,
        String typeName,
        CompoundTag nbt
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<S2CUpdatePacket> TYPE = new CustomPacketPayload.Type<>(OmnixerioDevicesMod.id("clientbound/update"));
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CUpdatePacket> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, S2CUpdatePacket::laptop,
            ByteBufCodecs.stringUtf8(64), S2CUpdatePacket::typeName,
            ByteBufCodecs.COMPOUND_TAG, S2CUpdatePacket::nbt,
            S2CUpdatePacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
