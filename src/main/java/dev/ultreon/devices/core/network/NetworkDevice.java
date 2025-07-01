package dev.ultreon.devices.core.network;

import dev.ultreon.devices.block.entity.NetworkDeviceBlockEntity;
import dev.ultreon.devices.core.Device;
import dev.ultreon.quantum.block.entity.BlockEntity;
import dev.ultreon.quantum.world.World;
import net.minecraft.core.BlockVec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.world.World;
import net.minecraft.world.world.block.entity.BlockEntity;
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

    public boolean isConnected(World world) {
        if (vec == null) {
            return false;
        }

        BlockEntity blockEntity = world.getBlockEntity(vec);
        if (blockEntity instanceof NetworkDeviceBlockEntity device) {
            Router router = device.getRouter();
            return router != null && router.getId().equals(this.getId());
        }
        return false;
    }

    @Nullable
    @Override
    public NetworkDeviceBlockEntity getDevice(@NotNull World world) {
        if (vec == null)
            return null;

        BlockEntity blockEntity = world.getBlockEntity(vec);
        if (blockEntity instanceof NetworkDeviceBlockEntity device) {
            return device;
        }
        return null;
    }

    @Override
    public CompoundTag toTag(boolean includeVec) {
        CompoundTag tag = super.toTag(includeVec);
        if (includeVec && vec != null) {
            tag.putLong("vec", vec.asLong());
        }
        return tag;
    }

    public static NetworkDevice fromTag(CompoundTag tag) {
        NetworkDevice device = new NetworkDevice();
        device.id = UUID.fromString(tag.getString("id"));
        device.name = tag.getString("name");

        if (tag.contains("vec", Tag.TAG_LONG)) {
            device.vec = BlockVec.of(tag.getLong("vec"));
        }
        return device;
    }
}
