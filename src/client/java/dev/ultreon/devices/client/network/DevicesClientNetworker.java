package dev.ultreon.devices.client.network;

import dev.ultreon.devices.DeviceConfig;
import dev.ultreon.devices.UltreonDevicesCommon;
import dev.ultreon.devices.client.ClientLaptop;
import dev.ultreon.devices.core.laptop.common.S2CUpdatePacket;
import dev.ultreon.devices.debug.DebugLog;
import dev.ultreon.devices.network.task.NotificationPacket;
import dev.ultreon.devices.network.task.SyncApplicationPacket;
import dev.ultreon.devices.network.task.SyncConfigPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class DevicesClientNetworker {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(S2CUpdatePacket.TYPE, (payload, _) -> {
            Optional<CompoundTag> compound = payload.nbt().asCompound();
            if (compound.isEmpty()) return;

            Optional<long[]> uuidLongs = compound.get().getLongArray("uuid");
            Optional<String> type = compound.get().getString("type");
            Optional<CompoundTag> data = compound.get().getCompound("data");

            if (uuidLongs.isEmpty() || type.isEmpty() || data.isEmpty()) return;

            long[] longs = uuidLongs.get();
            UUID uuid = new UUID(longs[0], longs[1]);
            Minecraft.getInstance().execute(() -> ClientLaptop.laptops.get(uuid).handlePacket(type.get(), data.get()));
        });
        ClientPlayNetworking.registerGlobalReceiver(NotificationPacket.TYPE, (payload, _) -> {
            Tag tag = payload.notificationTag();
            Optional<CompoundTag> compound = tag.asCompound();
            if (compound.isEmpty()) return;

            UltreonDevicesCommon.showNotification(compound.get());
        });
        ClientPlayNetworking.registerGlobalReceiver(SyncConfigPacket.TYPE, (payload, _) -> {
            Tag tag = payload.syncData();
            Optional<CompoundTag> syncData = tag.asCompound();
            if (syncData.isEmpty()) return;

            DeviceConfig.readSyncTag(syncData.get());
        });
        ClientPlayNetworking.registerGlobalReceiver(SyncApplicationPacket.TYPE, (payload, _) -> {
            Tag tag = payload.syncData();
            Optional<CompoundTag> syncData = tag.asCompound();
            if (syncData.isEmpty()) return;

            DeviceConfig.readSyncTag(syncData.get());
        });
    }
}
