package dev.ultreon.devices.core.network;

import dev.ultreon.devices.block.entity.NetworkDeviceBlockEntity;
import dev.ultreon.devices.core.Device;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class NetworkDevice extends Device {
    private NetworkDevice() {
        super();
    }

    public NetworkDevice(NetworkDeviceBlockEntity device) {
        super(device);
    }

    public NetworkDevice(@NotNull UUID id, @NotNull String name, @NotNull Router router) {
        super(id, name);
    }

    public boolean isConnected(Level level) {
        if (pos == null) {
            return false;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof NetworkDeviceBlockEntity device) {
            Router router = device.getRouter();
            return router != null && router.getId().equals(this.getId());
        }
        return false;
    }

    @Nullable
    @Override
    public NetworkDeviceBlockEntity getDevice(@NotNull Level level) {
        if (pos == null)
            return null;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof NetworkDeviceBlockEntity device) {
            return device;
        }
        return null;
    }

    @Override
    public CompoundTag toTag(boolean includePos) {
        CompoundTag tag = super.toTag(includePos);
        if (includePos && pos != null) {
            tag.putLong("pos", pos.asLong());
        }
        return tag;
    }

    public static NetworkDevice fromTag(CompoundTag tag) {
        NetworkDevice device = new NetworkDevice();
        device.id = UUID.fromString(tag.getString("id"));
        device.name = tag.getString("name");

        if (tag.contains("pos", Tag.TAG_LONG)) {
            device.pos = BlockPos.of(tag.getLong("pos"));
        }
        return device;
    }
}
