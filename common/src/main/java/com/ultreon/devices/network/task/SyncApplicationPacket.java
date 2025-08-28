package com.ultreon.devices.network.task;

import com.google.common.collect.ImmutableList;
import com.ultreon.devices.Devices;
import com.ultreon.devices.api.ApplicationManager;
import com.ultreon.devices.network.Packet;
import com.ultreon.devices.object.AppInfo;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author MrCrayfish
 */
public class SyncApplicationPacket extends Packet<SyncApplicationPacket> {
    private final List<AppInfo> allowedApps;

    public SyncApplicationPacket(FriendlyByteBuf buf) {
        int size = buf.readInt();
        ImmutableList.Builder<AppInfo> builder = ImmutableList.builder();
        for (int i = 0; i < size; i++) {
            String appId = buf.readUtf();
            AppInfo info = ApplicationManager.getApplication(ResourceLocation.tryParse(appId));
            if (info != null) {
                builder.add(info);
            } else {
                Devices.LOGGER.error("Missing application '" + appId + "'");
            }
        }

        allowedApps = builder.build();
    }

    public SyncApplicationPacket(List<AppInfo> allowedApps) {
        this.allowedApps = allowedApps;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(allowedApps.size());
        for (AppInfo appInfo : allowedApps) {
            buf.writeResourceLocation(appInfo.getId());
        }
    }

    @Override
    public boolean onMessage(Supplier<NetworkManager.PacketContext> ctx) {
        Devices.setAllowedApps(allowedApps);
        return true;
    }
}
