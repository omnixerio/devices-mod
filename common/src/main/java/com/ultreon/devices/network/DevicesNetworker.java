package com.ultreon.devices.network;

import com.ultreon.devices.core.laptop.common.C2SUpdatePacket;
import com.ultreon.devices.core.laptop.common.S2CUpdatePacket;
import com.ultreon.devices.network.task.NotificationPacket;
import com.ultreon.devices.network.task.SyncApplicationPacket;
import com.ultreon.devices.network.task.SyncBlockPacket;
import com.ultreon.devices.network.task.SyncConfigPacket;
import dev.ultreon.mods.xinexlib.network.Networker;
import dev.ultreon.mods.xinexlib.platform.XinexPlatform;

public class DevicesNetworker {
    public static final Networker INSTANCE = XinexPlatform.createNetworker("main", networkRegistry -> {
        networkRegistry.registerServer("c2s_update", C2SUpdatePacket.class, C2SUpdatePacket::new);
        networkRegistry.registerServer("sync_block", SyncBlockPacket.class, SyncBlockPacket::new);

        networkRegistry.registerClient("s2c_update", S2CUpdatePacket.class, S2CUpdatePacket::new);
        networkRegistry.registerClient("notification", NotificationPacket.class, NotificationPacket::new);
        networkRegistry.registerClient("sync_config", SyncConfigPacket.class, SyncConfigPacket::new);
        networkRegistry.registerClient("sync_apps", SyncApplicationPacket.class, SyncApplicationPacket::new);
    });

    public static void init() {

    }
}
