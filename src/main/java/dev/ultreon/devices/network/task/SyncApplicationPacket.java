package dev.ultreon.devices.network.task;

import com.google.common.collect.ImmutableList;
import dev.ultreon.devices.OmnixerioDevicesCommon;
import dev.ultreon.devices.api.ApplicationManager;
import dev.ultreon.devices.object.AppInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * @author MrCrayfish
 */
public class SyncApplicationPacket implements CustomPacketPayload {
    public static final Type<SyncApplicationPacket> TYPE = new Type<>(OmnixerioDevicesCommon.id("sync_application"));
    public static final StreamCodec<FriendlyByteBuf, SyncApplicationPacket> STREAM_CODEC = StreamCodec.of(
            (output, value) -> value.write(output),
            SyncApplicationPacket::new
    );
    public final List<AppInfo> allowedApps;

    public SyncApplicationPacket(FriendlyByteBuf buf) {
        int size = buf.readInt();
        ImmutableList.Builder<AppInfo> builder = ImmutableList.builder();
        for (int i = 0; i < size; i++) {
            String appId = buf.readUtf();
            AppInfo info = ApplicationManager.getApplication(Identifier.tryParse(appId));
            if (info != null) {
                builder.add(info);
            } else {
                OmnixerioDevicesCommon.LOGGER.error("Missing application '{}'", appId);
            }
        }

        allowedApps = builder.build();
    }

    public SyncApplicationPacket(List<AppInfo> allowedApps) {
        this.allowedApps = allowedApps;
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(allowedApps.size());
        for (AppInfo appInfo : allowedApps) {
            buffer.writeIdentifier(appInfo.getId());
        }
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
