package dev.ultreon.devices.network;

import dev.ultreon.devices.UltreonDevices;
import dev.ultreon.devices.core.laptop.common.C2SUpdatePacket;
import dev.ultreon.devices.core.laptop.common.S2CUpdatePacket;
import dev.ultreon.devices.network.packets.*;
import dev.ultreon.mods.xinexlib.network.Networker;
import dev.ultreon.mods.xinexlib.network.packet.PacketToClient;
import dev.ultreon.mods.xinexlib.network.packet.PacketToServer;
import dev.ultreon.mods.xinexlib.platform.XinexPlatform;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;

public class PacketHandler {
    private static int id = 0;
    private static Networker networker;

    private PacketHandler() {
        throw new UnsupportedOperationException();
    }

    public static void init() {
        networker = XinexPlatform.createNetworker(UltreonDevices.MOD_ID, networkRegistry -> {
            networkRegistry.registerServer("request", RequestPacket.class, RequestPacket::new);
            networkRegistry.registerClient("response", ResponsePacket.class, ResponsePacket::new);
            networkRegistry.registerClient("sync_application", SyncApplicationPacket.class, SyncApplicationPacket::new);
            networkRegistry.registerClient("sync_config", SyncConfigPacket.class, SyncConfigPacket::new);
            networkRegistry.registerClient("notification", NotificationPacket.class, NotificationPacket::new);
            networkRegistry.registerClient("s2c_update", S2CUpdatePacket.class, S2CUpdatePacket::new);
            networkRegistry.registerServer("c2s_update", C2SUpdatePacket.class, C2SUpdatePacket::new);
        });
    }

    public static <T extends PacketToServer<T>> void sendToServer(T message) {
        if (Minecraft.getInstance().getConnection() != null) {
            networker.sendToServer(message);
        } else {
            throw new IllegalArgumentException("Connection is null");
        }
    }

    public static <T extends PacketToClient<T>> void sendToClient(T messageNotification, ServerPlayer player) { // has to be ServerPlayer if world is not null
        if (player == null) {
            throw new IllegalArgumentException("Player is null");
        }
        networker.sendToClient(messageNotification, player);
    }
}
