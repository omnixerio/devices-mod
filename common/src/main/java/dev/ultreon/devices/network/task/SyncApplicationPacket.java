package dev.ultreon.devices.network.task;

import com.google.common.collect.ImmutableList;
import dev.ultreon.devices.UltreonDevices;
import dev.ultreon.devices.api.ApplicationManager;
import dev.ultreon.devices.object.AppInfo;
import dev.ultreon.mods.xinexlib.network.Networker;
import dev.ultreon.mods.xinexlib.network.packet.PacketToClient;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/// @author MrCrayfish
public class SyncApplicationPacket implements PacketToClient<SyncApplicationPacket> {
    private final List<AppInfo> allowedApps;

    public SyncApplicationPacket(RegistryFriendlyByteBuf buf) {
        int size = buf.readInt();
        ImmutableList.Builder<AppInfo> builder = ImmutableList.builder();
        for (int i = 0; i < size; i++) {
            String appId = buf.readUtf();
            AppInfo info = ApplicationManager.getApplication(ResourceLocation.tryParse(appId));
            if (info != null) {
                builder.add(info);
            } else {
                UltreonDevices.LOGGER.error("Missing application '{}'", appId);
            }
        }

        allowedApps = builder.build();
    }

    public SyncApplicationPacket(List<AppInfo> allowedApps) {
        this.allowedApps = allowedApps;
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(allowedApps.size());
        for (AppInfo appInfo : allowedApps) {
            buf.writeResourceLocation(appInfo.getId());
        }
    }

    @Override
    public void handle(Networker networker) {
        UltreonDevices.setAllowedApps(allowedApps);
    }
}
