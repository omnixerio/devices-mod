package com.ultreon.devices.network.clientbound;

import com.ultreon.devices.OmnixerioDevicesMod;
import com.ultreon.devices.api.app.Notification;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

/**
 * @author MrCrayfish
 */
public record S2CNotificationPacket(
        CompoundTag notificationTag
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<S2CNotificationPacket> TYPE = new CustomPacketPayload.Type<>(OmnixerioDevicesMod.id("clientbound/notification"));
    public static final StreamCodec<FriendlyByteBuf, S2CNotificationPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, S2CNotificationPacket::notificationTag,
            S2CNotificationPacket::new
    );

    public static S2CNotificationPacket create(Notification notification) {
        return new S2CNotificationPacket(notification.toTag());
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
