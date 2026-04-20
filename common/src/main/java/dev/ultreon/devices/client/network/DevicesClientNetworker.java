package dev.ultreon.devices.client.network;

import dev.ultreon.devices.DeviceConfig;
import dev.ultreon.devices.OmnixerioDevicesCommon;
import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.api.task.TaskManager;
import dev.ultreon.devices.client.ClientLaptop;
import dev.ultreon.devices.core.laptop.common.ClientboundUpdatePacket;
import dev.ultreon.devices.network.task.NotificationPacket;
import dev.ultreon.devices.network.task.ClientboundResponsePacket;
import dev.ultreon.devices.network.task.SyncApplicationPacket;
import dev.ultreon.devices.network.task.SyncConfigPacket;
import dev.ultreon.devices.object.AppInfo;
import dev.ultreon.devices.platform.client.ClientServices;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DevicesClientNetworker {
    public static void init() {
        ClientServices.PLATFORM.registerClientboundPlay(ClientboundUpdatePacket.TYPE, (payload, _) -> {
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
        ClientServices.PLATFORM.registerClientboundPlay(NotificationPacket.TYPE, (payload, _) -> {
            Tag tag = payload.notificationTag();
            Optional<CompoundTag> compound = tag.asCompound();
            if (compound.isEmpty()) return;

            OmnixerioDevicesCommon.showNotification(compound.get());
        });
        ClientServices.PLATFORM.registerClientboundPlay(SyncConfigPacket.TYPE, (payload, _) -> {
            Tag tag = payload.syncData();
            Optional<CompoundTag> syncData = tag.asCompound();
            if (syncData.isEmpty()) return;

            DeviceConfig.readSyncTag(syncData.get());
        });
        ClientServices.PLATFORM.registerClientboundPlay(SyncApplicationPacket.TYPE, (payload, _) -> {
            List<AppInfo> tag = payload.allowedApps;
            OmnixerioDevicesCommon.setAllowedApps(tag);
        });
        ClientServices.PLATFORM.registerClientboundPlay(ClientboundResponsePacket.TYPE, (payload, context) -> {
            boolean successful = payload.successful();
            Task request = TaskManager.getTaskAndRemove(payload.id());
            if (successful) request.setSuccessful();
            request.prepareResponse(payload.tag());
            request.callback(payload.tag());
            request.complete();
        });
    }
}
