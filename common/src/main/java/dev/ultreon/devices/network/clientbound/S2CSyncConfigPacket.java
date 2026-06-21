package dev.ultreon.devices.network.clientbound;

import dev.ultreon.devices.OmnixerioDevicesMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

/**
 * @author MrCrayfish
 */
public record S2CSyncConfigPacket(
        CompoundTag configTag
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<S2CSyncConfigPacket> TYPE = new CustomPacketPayload.Type<>(OmnixerioDevicesMod.id("clientbound/sync_config"));
    public static final StreamCodec<FriendlyByteBuf, S2CSyncConfigPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, S2CSyncConfigPacket::configTag,
            S2CSyncConfigPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
