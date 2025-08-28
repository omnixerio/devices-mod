package dev.ultreon.devices.network.task;

import dev.ultreon.devices.Devices;
import dev.ultreon.devices.api.app.Notification;
import dev.ultreon.mods.xinexlib.network.Networker;
import dev.ultreon.mods.xinexlib.network.packet.PacketToClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;

/// @author MrCrayfish
public class NotificationPacket implements PacketToClient<NotificationPacket> {
    private final CompoundTag notificationTag;

    public NotificationPacket(RegistryFriendlyByteBuf buf) {
        notificationTag = buf.readNbt();
    }

    public NotificationPacket(Notification notification) {
        this.notificationTag = notification.toTag();
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeNbt(notificationTag);
    }

    @Override
    public void handle(Networker networker) {
        Devices.showNotification(notificationTag);
    }
}
