package dev.ultreon.devices.network.serverbound;

import dev.ultreon.devices.OmnixerioDevicesMod;
import dev.ultreon.devices.api.task.Task;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record C2SRequestPacket(
        int id,
        String taskName,
        CompoundTag requestData
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<C2SRequestPacket> TYPE = new CustomPacketPayload.Type<>(OmnixerioDevicesMod.id("serverbound/request"));
    public static final StreamCodec<RegistryFriendlyByteBuf, C2SRequestPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, C2SRequestPacket::id,
            ByteBufCodecs.STRING_UTF8, C2SRequestPacket::taskName,
            ByteBufCodecs.COMPOUND_TAG, C2SRequestPacket::requestData,
            C2SRequestPacket::new
    );

    public static C2SRequestPacket create(int id, Task request) {
        String requestName = request.getName();
        CompoundTag requestData = new CompoundTag();
        request.prepareRequest(requestData);
        return new C2SRequestPacket(id, requestName, requestData);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
