package dev.ultreon.devices.network;

import dev.ultreon.devices.Devices;
import dev.ultreon.devices.core.laptop.common.C2SUpdatePacket;
import dev.ultreon.devices.core.laptop.common.S2CUpdatePacket;
import dev.ultreon.devices.network.task.*;
import dev.ultreon.mods.xinexlib.network.Networker;
import dev.ultreon.mods.xinexlib.network.packet.PacketToClient;
import dev.ultreon.mods.xinexlib.network.packet.PacketToServer;
import dev.ultreon.mods.xinexlib.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;

public class PacketHandler {
    private static int id = 0;
    private static Networker networker;

    private PacketHandler() {
        throw new UnsupportedOperationException();
    }

//    private static CustomPacketPayload.Type<RequestPacket> requestPacket;
//    private static CustomPacketPayload.Type<ResponsePacket> responsePacket;
//    private static CustomPacketPayload.Type<SyncApplicationPacket> syncApplicationPacket;
//    private static CustomPacketPayload.Type<SyncConfigPacket> syncConfigPacket;
//    private static CustomPacketPayload.Type<SyncBlockPacket> syncBlockPacket;
//    private static CustomPacketPayload.Type<NotificationPacket> notificationPacket;
//    private static CustomPacketPayload.Type<S2CUpdatePacket> s2cUpdatePacket;
//    private static CustomPacketPayload.Type<C2SUpdatePacket> c2sUpdatePacket;
//
//    public static CustomPacketPayload.Type<RequestPacket> getRequestPacket() {
//        return requestPacket;
//    }
//
//    public static CustomPacketPayload.Type<ResponsePacket> getResponsePacket() {
//        return responsePacket;
//    }
//
//    public static CustomPacketPayload.Type<SyncApplicationPacket> getSyncApplicationPacket() {
//        return syncApplicationPacket;
//    }
//
//    public static CustomPacketPayload.Type<SyncConfigPacket> getSyncConfigPacket() {
//        return syncConfigPacket;
//    }
//
//    public static CustomPacketPayload.Type<SyncBlockPacket> getSyncBlockPacket() {
//        return syncBlockPacket;
//    }
//
//    public static CustomPacketPayload.Type<NotificationPacket> getNotificationPacket() {
//        return notificationPacket;
//    }
//
//    public static CustomPacketPayload.Type<S2CUpdatePacket> getS2CUpdatePacket() {
//        return s2cUpdatePacket;
//    }
//
//    public static CustomPacketPayload.Type<C2SUpdatePacket> getC2SUpdatePacket() {
//        return c2sUpdatePacket;
//    }

    public static void init() {
//        requestPacket = registerC2S(RequestPacket.class, Packet::toBytes, RequestPacket::new, (T info, Supplier<NetworkManager.PacketContext> info2) -> RequestPacket.onMessage(networker, info));
//        responsePacket = registerS2C(ResponsePacket.class, Packet::toBytes, ResponsePacket::new, ResponsePacket::onMessage);
//        syncApplicationPacket = registerS2C(SyncApplicationPacket.class, Packet::toBytes, SyncApplicationPacket::new, (T info, Supplier<NetworkManager.PacketContext> info2) -> SyncApplicationPacket.onMessage(networker, info));
//        syncConfigPacket = registerS2C(SyncConfigPacket.class, Packet::toBytes, SyncConfigPacket::new, (T info, Supplier<NetworkManager.PacketContext> info2) -> SyncConfigPacket.onMessage(networker, info));
//        syncBlockPacket = registerS2C(SyncBlockPacket.class, Packet::toBytes, SyncBlockPacket::new, SyncBlockPacket::onMessage);
//        notificationPacket = registerS2C(NotificationPacket.class, Packet::toBytes, NotificationPacket::new, (T info, Supplier<NetworkManager.PacketContext> info2) -> NotificationPacket.onMessage(networker, info));
//        s2cUpdatePacket = registerS2C(S2CUpdatePacket.class, Packet::toBytes, S2CUpdatePacket::new, (T info, Supplier<NetworkManager.PacketContext> info2) -> S2CUpdatePacket.onMessage(networker, info));
//        c2sUpdatePacket = registerC2S(C2SUpdatePacket.class, Packet::toBytes, C2SUpdatePacket::new, (T info, Supplier<NetworkManager.PacketContext> info2) -> C2SUpdatePacket.onMessage(networker, info));

        networker = Services.createNetworker(Devices.MOD_ID, networkRegistry -> {
            networkRegistry.registerServer("request", RequestPacket.class, RequestPacket::new);
            networkRegistry.registerClient("response", ResponsePacket.class, ResponsePacket::new);
            networkRegistry.registerClient("sync_application", SyncApplicationPacket.class, SyncApplicationPacket::new);
            networkRegistry.registerClient("sync_config", SyncConfigPacket.class, SyncConfigPacket::new);
            networkRegistry.registerClient("sync_block", SyncBlockPacket.class, SyncBlockPacket::new);
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
