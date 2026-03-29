package dev.ultreon.devices.network.task;

import dev.ultreon.devices.UltreonDevicesCommon;
import dev.ultreon.devices.api.app.Notification;
import dev.ultreon.mods.xinexlib.network.Networker;
import dev.ultreon.mods.xinexlib.network.packet.PacketToClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * @author MrCrayfish
 */
public record NotificationPacket(Tag notificationTag) implements CustomPacketPayload {
    public static final Type<NotificationPacket> TYPE = new Type<>(UltreonDevicesCommon.id("notification"));
    public static final StreamCodec<FriendlyByteBuf, NotificationPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.tagCodec(NbtAccounter::unlimitedHeap), NotificationPacket::notificationTag,
            NotificationPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
