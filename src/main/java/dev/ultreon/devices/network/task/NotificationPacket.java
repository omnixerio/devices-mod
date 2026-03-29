package dev.ultreon.devices.network.task;

import dev.ultreon.devices.UltreonDevicesCommon;
import dev.ultreon.devices.api.app.Notification;
import dev.ultreon.mods.xinexlib.network.Networker;
import dev.ultreon.mods.xinexlib.network.packet.PacketToClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;

/**
 * @author MrCrayfish
 */
public class NotificationPacket implements PacketToClient<NotificationPacket> {
    private final CompoundTag notificationTag;

    public NotificationPacket(FriendlyByteBuf buf) {
        notificationTag = buf.readNbt();
    }

    public NotificationPacket(Notification notification) {
        this.notificationTag = notification.toTag();
    }

    @Override
    public void handle(Networker connection) {
        UltreonDevicesCommon.showNotification(notificationTag);
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeNbt(notificationTag);
    }
}
