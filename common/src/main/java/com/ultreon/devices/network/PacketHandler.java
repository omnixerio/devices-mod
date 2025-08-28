package com.ultreon.devices.network;

import com.ultreon.devices.Devices;
import com.ultreon.devices.core.laptop.common.C2SUpdatePacket;
import com.ultreon.devices.core.laptop.common.S2CUpdatePacket;
import com.ultreon.devices.network.task.*;
import dev.architectury.networking.NetworkChannel;
import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class PacketHandler {
    public static final NetworkChannel INSTANCE = NetworkChannel.create(Devices.id("main_channel"));
    private static int id = 0;

    public static void init() {
        INSTANCE.register(RequestPacket.class, RequestPacket::toBytes, RequestPacket::new, RequestPacket::onMessage);
        INSTANCE.register(ResponsePacket.class, Packet::toBytes, ResponsePacket::new, ResponsePacket::onMessage);
        INSTANCE.register(SyncApplicationPacket.class, Packet::toBytes, SyncApplicationPacket::new, SyncApplicationPacket::onMessage);
        INSTANCE.register(SyncConfigPacket.class, Packet::toBytes, SyncConfigPacket::new, SyncConfigPacket::onMessage);
        INSTANCE.register(SyncBlockPacket.class, Packet::toBytes, SyncBlockPacket::new, SyncBlockPacket::onMessage);
        INSTANCE.register(NotificationPacket.class, Packet::toBytes, NotificationPacket::new, NotificationPacket::onMessage);
        INSTANCE.register(S2CUpdatePacket.class, Packet::toBytes, S2CUpdatePacket::new, S2CUpdatePacket::onMessage);
        INSTANCE.register(C2SUpdatePacket.class, Packet::toBytes, C2SUpdatePacket::new, C2SUpdatePacket::onMessage);
    }

    private static int nextId() {
        return id++;
    }

    @Environment(EnvType.CLIENT)
    public static <T extends Packet<T>> void sendToServer(T message) {
        if (Minecraft.getInstance().getConnection() != null) {
            INSTANCE.sendToServer(message);
        } else {
            Minecraft.getInstance().doRunTask(() ->
            message.onMessage(() -> new NetworkManager.PacketContext() {

                @Override
                public Player getPlayer() {
                    return Minecraft.getInstance().player;
                }

                @Override
                public void queue(Runnable runnable) {

                }

                @Override
                public Env getEnvironment() {
                    return Env.SERVER;
                }
            }));
        }
    }

    public static <T extends Packet<T>> void sendToClient(Packet<T> messageNotification, Player player) { // has to be ServerPlayer if world is not null
        if (player == null || player.level() == null) {
            messageNotification.onMessage(() -> new NetworkManager.PacketContext() {
                @Override
                public Player getPlayer() {
                    return player;
                }

                @Override
                public void queue(Runnable runnable) {

                }

                @Override
                public Env getEnvironment() {
                    return Env.CLIENT;
                }
            });
            return;
        }
        INSTANCE.sendToPlayer((ServerPlayer) player, messageNotification);
        //INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), messageNotification);
    }

    // seems to be unused
    public static <T extends Packet<T>> void sendToDimension(Packet<T> messageNotification, ResourceKey<Level> level) {
        //INSTANCE.sendToServer();
        //INSTANCE.send(PacketDistributor.DIMENSION.with(() -> level), messageNotification);
    }

//    public static <T extends Packet<T>> void.json sendToDimension(Packet<T> messageNotification, Level level) {
//        INSTANCE.send(PacketDistributor.DIMENSION.with(level::dimension), messageNotification);
//    }
//
//    public static <T extends Packet<T>> void.json sendToServer(Packet<T> messageNotification) {
//        INSTANCE.send(PacketDistributor.SERVER.noArg(), messageNotification);
//    }
//
//    public static <T extends Packet<T>> void.json sendToAll(Packet<T> messageNotification) {
//        INSTANCE.send(PacketDistributor.ALL.noArg(), messageNotification);
//    }
//
//    public static <T extends Packet<T>> void.json sendToAllAround(Packet<T> messageNotification, ResourceKey<Level> level, double x, double y, double z, double radius) {
//        INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(x, y, z, radius, level)), messageNotification);
//    }
//
//    public static <T extends Packet<T>> void.json sendToAllAround(Packet<T> messageNotification, Level level, double x, double y, double z, double radius) {
//        INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(x, y, z, radius, level.dimension())), messageNotification);
//    }
//
//    public static <T extends Packet<T>> void.json sendToTrackingChunk(Packet<T> messageNotification, LevelChunk chunk) {
//        INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), messageNotification);
//    }
//
//    public static <T extends Packet<T>> void.json sendToTrackingChunk(Packet<T> messageNotification, Level level, int x, int z) {
//        INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunk(x, z)), messageNotification);
//    }
//
//    public static <T extends Packet<T>> void.json sendToTrackingEntity(Packet<T> messageNotification, Entity entity) {
//        INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), messageNotification);
//    }
//
//    public static <T extends Packet<T>> void.json sendToTrackingEntity(Packet<T> messageNotification, Level level, int entityId) {
//        INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> level.getEntity(entityId)), messageNotification);
//    }
//
//    public static <T extends Packet<T>> void.json sendToTrackingEntityAndSelf(Packet<T> messageNotification, Entity entity) {
//        INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), messageNotification);
//    }
//
//    public static <T extends Packet<T>> void.json sendToTrackingEntityAndSelf(Packet<T> messageNotification, Level level, int entityId) {
//        INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> level.getEntity(entityId)), messageNotification);
//    }
}
