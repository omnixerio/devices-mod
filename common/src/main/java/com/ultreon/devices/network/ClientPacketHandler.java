package com.ultreon.devices.network;

import com.ultreon.devices.DeviceConfig;
import com.ultreon.devices.OmnixerioDevicesMod;
import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.api.task.TaskManager;
import com.ultreon.devices.core.laptop.client.ClientLaptop;
import com.ultreon.devices.network.clientbound.*;
import com.ultreon.devices.debug.DebugLog;
import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;

import java.util.Arrays;
import java.util.Objects;

public class ClientPacketHandler {
    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, S2CUpdatePacket.TYPE, S2CUpdatePacket.CODEC, ClientPacketHandler::onUpdate);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, S2CNotificationPacket.TYPE, S2CNotificationPacket.CODEC, ClientPacketHandler::onNotification);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, S2CSyncConfigPacket.TYPE, S2CSyncConfigPacket.CODEC, ClientPacketHandler::onSyncConfig);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, S2CSyncApplicationsPacket.TYPE, S2CSyncApplicationsPacket.CODEC, ClientPacketHandler::onSyncApplications);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, S2CResponsePacket.TYPE, S2CResponsePacket.CODEC, ClientPacketHandler::onTaskResponse);
    }

    private static void onUpdate(S2CUpdatePacket value, NetworkManager.PacketContext context) {
        if (context.getEnv().equals(EnvType.CLIENT)) {
            ClientLaptop.laptops.get(value.laptop()).handlePacket(value.typeName(), value.nbt());
            DebugLog.log("SQUARE: " + Arrays.toString(ClientLaptop.laptops.get(value.laptop()).square));
        }
    }

    private static void onNotification(S2CNotificationPacket value, NetworkManager.PacketContext context) {
        OmnixerioDevicesMod.showNotification(value.notificationTag());
    }

    private static void onSyncConfig(S2CSyncConfigPacket value, NetworkManager.PacketContext context) {
        DeviceConfig.readSyncTag(Objects.requireNonNull(value.configTag()));
    }

    private static void onTaskResponse(S2CResponsePacket value, NetworkManager.PacketContext context) {
        int id = value.id();
        boolean successful = value.successful();

        Task request = TaskManager.getTaskAndRemove(id);
        if (successful) request.setSuccessful();

        request.processResponse(value.responseData());
        request.callback(value.responseData());
    }

    private static void onSyncApplications(S2CSyncApplicationsPacket value, NetworkManager.PacketContext context) {
        OmnixerioDevicesMod.setAllowedApps(value.getAllowedApps());
    }
}
