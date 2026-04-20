package dev.ultreon.devices.network.task;

import dev.ultreon.devices.OmnixerioDevicesCommon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

/**
 * @author MrCrayfish
 */
public record NotificationPacket(CompoundTag notificationTag) implements CustomPacketPayload {
    public static final Type<NotificationPacket> TYPE = new Type<>(OmnixerioDevicesCommon.id("notification"));
    public static final StreamCodec<FriendlyByteBuf, NotificationPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.compoundTagCodec(NbtAccounter::unlimitedHeap), NotificationPacket::notificationTag,
            NotificationPacket::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
