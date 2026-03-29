package dev.ultreon.devices.network.task;

import com.google.common.collect.ImmutableList;
import dev.ultreon.devices.UltreonDevicesCommon;
import dev.ultreon.devices.api.ApplicationManager;
import dev.ultreon.devices.object.AppInfo;
import dev.ultreon.mods.xinexlib.network.Networker;
import dev.ultreon.mods.xinexlib.network.packet.PacketToClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;

import java.util.List;

/**
 * @author MrCrayfish
 */
public class SyncApplicationPacket implements PacketToClient<SyncApplicationPacket> {
    private final List<AppInfo> allowedApps;

    public SyncApplicationPacket(FriendlyByteBuf buf) {
        int size = buf.readInt();
        ImmutableList.Builder<AppInfo> builder = ImmutableList.builder();
        for (int i = 0; i < size; i++) {
            String appId = buf.readUtf();
            AppInfo info = ApplicationManager.getApplication(Identifier.tryParse(appId));
            if (info != null) {
                builder.add(info);
            } else {
                UltreonDevicesCommon.LOGGER.error("Missing application '{}'", appId);
            }
        }

        allowedApps = builder.build();
    }

    public SyncApplicationPacket(List<AppInfo> allowedApps) {
        this.allowedApps = allowedApps;
    }

    @Override
    public void handle(Networker connection) {
        UltreonDevicesCommon.setAllowedApps(allowedApps);
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(allowedApps.size());
        for (AppInfo appInfo : allowedApps) {
            buffer.writeIdentifier(appInfo.getId());
        }
    }
}
