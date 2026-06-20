package com.ultreon.devices.network.clientbound;

import com.ultreon.devices.OmnixerioDevicesMod;
import com.ultreon.devices.api.task.Task;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record S2CResponsePacket(
        int id,
        boolean successful,
        String taskName,
        CompoundTag responseData
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<S2CResponsePacket> TYPE = new CustomPacketPayload.Type<>(OmnixerioDevicesMod.id("clientbound/response"));
    public static final StreamCodec<FriendlyByteBuf, S2CResponsePacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, S2CResponsePacket::id,
            ByteBufCodecs.BOOL, S2CResponsePacket::successful,
            ByteBufCodecs.STRING_UTF8, S2CResponsePacket::taskName,
            ByteBufCodecs.COMPOUND_TAG, S2CResponsePacket::responseData,
            S2CResponsePacket::new
    );

    public static S2CResponsePacket create(int id, Task task) {
        CompoundTag response = new CompoundTag();
        task.prepareResponse(response);
        return new S2CResponsePacket(id, task.isSucessful(), task.getName(), response);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
