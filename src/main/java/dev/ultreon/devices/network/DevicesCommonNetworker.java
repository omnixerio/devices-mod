package dev.ultreon.devices.network;

import dev.ultreon.devices.api.task.TaskManager;
import dev.ultreon.devices.block.entity.RouterBlockEntity;
import dev.ultreon.devices.core.laptop.common.ServerboundUpdatePacket;
import dev.ultreon.devices.core.laptop.common.ClientboundUpdatePacket;
import dev.ultreon.devices.core.laptop.server.ServerLaptop;
import dev.ultreon.devices.network.task.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Optional;
import java.util.UUID;

public class DevicesCommonNetworker {
    public static void init() {
        PayloadTypeRegistry.serverboundPlay().register(ServerboundUpdatePacket.TYPE, ServerboundUpdatePacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SyncBlockPacket.TYPE, SyncBlockPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ServerboundRequestPacket.TYPE, ServerboundRequestPacket.STREAM_CODEC);

        PayloadTypeRegistry.clientboundPlay().register(ClientboundResponsePacket.TYPE, ClientboundResponsePacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ClientboundUpdatePacket.TYPE, ClientboundUpdatePacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(NotificationPacket.TYPE, NotificationPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncConfigPacket.TYPE, SyncConfigPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncApplicationPacket.TYPE, SyncApplicationPacket.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ServerboundUpdatePacket.TYPE, (payload, context) -> {
            CompoundTag tag = payload.tag().asCompound().orElse(new CompoundTag());
            Optional<long[]> uuidArray = tag.getLongArray("uuid");
            Optional<String> type = tag.getString("type");
            Optional<CompoundTag> data = tag.getCompound("data");

            if (uuidArray.isEmpty() || type.isEmpty() || data.isEmpty()) return;

            long[] longs = uuidArray.get();
            UUID uuid = new UUID(longs[0], longs[1]);

            ServerLaptop.laptops.get(uuid).handlePacket(context.player(), type.orElse(""), data.orElse(new CompoundTag()));
        });
        ServerPlayNetworking.registerGlobalReceiver(SyncBlockPacket.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            BlockPos routerPos = payload.pos();

            Level level = player.level();
            BlockEntity blockEntity = level.getChunkAt(routerPos).getBlockEntity(routerPos, LevelChunk.EntityCreationType.IMMEDIATE);
            if (blockEntity instanceof RouterBlockEntity router) {
                router.syncDevicesToClient();
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(ServerboundRequestPacket.TYPE, (payload, context) -> {
            String request = payload.request();
            TaskManager.getTask(request).processRequest(payload.tag(), context.player().level(), context.player());
        });
    }
}
