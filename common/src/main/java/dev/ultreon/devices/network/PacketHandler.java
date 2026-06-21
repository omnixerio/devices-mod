package dev.ultreon.devices.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import dev.ultreon.devices.api.task.TaskManager;
import dev.ultreon.devices.block.entity.RouterBlockEntity;
import dev.ultreon.devices.core.laptop.server.ServerLaptop;
import dev.ultreon.devices.network.clientbound.*;
import dev.ultreon.devices.network.serverbound.C2SRequestPacket;
import dev.ultreon.devices.network.serverbound.C2SSyncBlockPacket;
import dev.ultreon.devices.network.serverbound.C2SUpdatePacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Objects;

public class PacketHandler {
    public static void init() {
        if (Platform.getEnvironment() == Env.CLIENT) {
            ClientPacketHandler.init();
        } else {
            NetworkManager.registerS2CPayloadType(S2CUpdatePacket.TYPE, S2CUpdatePacket.CODEC);
            NetworkManager.registerS2CPayloadType(S2CResponsePacket.TYPE, S2CResponsePacket.CODEC);
            NetworkManager.registerS2CPayloadType(S2CSyncApplicationsPacket.TYPE, S2CSyncApplicationsPacket.CODEC);
            NetworkManager.registerS2CPayloadType(S2CSyncConfigPacket.TYPE, S2CSyncConfigPacket.CODEC);
            NetworkManager.registerS2CPayloadType(S2CNotificationPacket.TYPE, S2CNotificationPacket.CODEC);
        }

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, C2SRequestPacket.TYPE, C2SRequestPacket.CODEC, PacketHandler::onRequestPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, C2SSyncBlockPacket.TYPE, C2SSyncBlockPacket.CODEC, PacketHandler::onSyncBlockPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, C2SUpdatePacket.TYPE, C2SUpdatePacket.CODEC, PacketHandler::onUpdatePacket);
    }

    @Environment(EnvType.CLIENT)
    public static <T extends CustomPacketPayload> void sendToServer(T message) {
        NetworkManager.sendToServer(message);
    }

    public static <T extends CustomPacketPayload> void sendToClient(T messageNotification, ServerPlayer player) { // has to be ServerPlayer if world is not null
        NetworkManager.sendToPlayer(player, messageNotification);
    }

    private static void onRequestPacket(C2SRequestPacket value, NetworkManager.PacketContext context) {
        var id = value.id();
        String name = value.taskName();
        var request = TaskManager.getTask(name);
        var tag = value.requestData();

        //DebugLog.log("RECEIVED from " + ctx.get().getPlayer().getUUID());
        request.processRequest(tag, Objects.requireNonNull(context.getPlayer()).level(), context.getPlayer());
        if (context.getPlayer() instanceof ServerPlayer player)
            NetworkManager.sendToPlayer(player, S2CResponsePacket.create(id, request));
    }

    private static void onSyncBlockPacket(C2SSyncBlockPacket value, NetworkManager.PacketContext context) {
        Level level = Objects.requireNonNull(context.getPlayer()).level();
        BlockEntity blockEntity = level.getChunkAt(value.routerPos()).getBlockEntity(value.routerPos(), LevelChunk.EntityCreationType.IMMEDIATE);
        if (blockEntity instanceof RouterBlockEntity router) {
            router.syncDevicesToClient();
        }
    }

    private static void onUpdatePacket(C2SUpdatePacket value, NetworkManager.PacketContext context) {
        if (context.getEnv().equals(EnvType.SERVER)) {
            ServerLaptop.laptops.get(value.laptop()).handlePacket(context.getPlayer(), value.typeName(), value.data());
        }
    }
}
