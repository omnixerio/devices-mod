package com.ultreon.devices.core.network;

import com.ultreon.devices.DeviceConfig;
import com.ultreon.devices.block.entity.NetworkDeviceBlockEntity;
import com.ultreon.devices.debug.DebugLog;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Author: MrCrayfish
 */
public class Router {
    private final Map<UUID, NetworkDevice> NETWORK_DEVICES = new HashMap<>();

    private int timer;
    private UUID routerId;
    private BlockPos pos;

    public Router(BlockPos pos) {
        this.pos = pos;
    }

    public void tick(Level level) {
        if (++timer >= DeviceConfig.BEACON_INTERVAL.get()) {
            sendBeacon(level);
            timer = 0;
        }
    }

    public boolean addDevice(UUID id, String name) {
        if (NETWORK_DEVICES.size() >= DeviceConfig.MAX_DEVICES.get()) {
            return NETWORK_DEVICES.containsKey(id);
        }
        if (!NETWORK_DEVICES.containsKey(id)) {
            NETWORK_DEVICES.put(id, new NetworkDevice(id, name, this));
        }
        timer = DeviceConfig.BEACON_INTERVAL.get();
        return true;
    }

    public boolean addDevice(NetworkDeviceBlockEntity device) {
        if (NETWORK_DEVICES.size() >= DeviceConfig.MAX_DEVICES.get()) {
            return NETWORK_DEVICES.containsKey(device.getId());
        }
        if (!NETWORK_DEVICES.containsKey(device.getId())) {
            NETWORK_DEVICES.put(device.getId(), new NetworkDevice(device));
        }
        return true;
    }

    public boolean isDeviceRegistered(NetworkDeviceBlockEntity device) {
        return NETWORK_DEVICES.containsKey(device.getId());
    }

    public boolean isDeviceConnected(NetworkDeviceBlockEntity device) {
        return isDeviceRegistered(device) && NETWORK_DEVICES.get(device.getId()).getPos() != null;
    }

    public void removeDevice(NetworkDeviceBlockEntity device) {
        NETWORK_DEVICES.remove(device.getId());
    }

    @Nullable
    public NetworkDeviceBlockEntity getDevice(Level level, UUID id) {
        return NETWORK_DEVICES.containsKey(id) ? NETWORK_DEVICES.get(id).getDevice(level) : null;
    }

    public Collection<NetworkDevice> getNetworkDevices() {
        return NETWORK_DEVICES.values();
    }

    public Collection<NetworkDevice> getConnectedDevices(Level level) {
        sendBeacon(level);
        return NETWORK_DEVICES.values().stream().filter(device -> device.getPos() != null).toList();
    }

    public Collection<NetworkDevice> getConnectedDevices(final Level level, BlockEntityType<?> targetType) {
        final Predicate<NetworkDevice> deviceType = networkDevice -> {
            if (networkDevice.getPos() == null)
                return false;

            BlockEntity blockEntity = level.getBlockEntity(networkDevice.getPos());
            return blockEntity instanceof NetworkDeviceBlockEntity device && targetType.equals(device.getType());

        };
        return getConnectedDevices(level).stream().filter(deviceType).toList();
    }

    public Collection<NetworkDevice> getConnectedDevices(final Level level, TagKey<BlockEntityType<?>> targetType) {
        final Predicate<NetworkDevice> deviceType = networkDevice -> {
            if (networkDevice.getPos() == null)
                return false;

            BlockEntity blockEntity = level.getBlockEntity(networkDevice.getPos());

            if (blockEntity instanceof NetworkDeviceBlockEntity device) {
                return BuiltInRegistries.BLOCK_ENTITY_TYPE.wrapAsHolder(device.getType()).is(targetType);
            }
            return false;
        };
        return getConnectedDevices(level).stream().filter(deviceType).toList();
    }

    private void sendBeacon(Level level) {
        if (level.isClientSide)
            return;

        NETWORK_DEVICES.forEach((uuid, device) -> device.setPos(null));

        int range = DeviceConfig.SIGNAL_RANGE.get();
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    sendBeaconAt(level, x, y, z);
                }
            }
        }
    }

    private void sendBeaconAt(Level level, int x, int y, int z) {
        BlockPos currentPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
        BlockEntity blockEntity = level.getBlockEntity(currentPos);

        if (!(blockEntity instanceof NetworkDeviceBlockEntity device) || !NETWORK_DEVICES.containsKey(device.getId()))
            return;

        if (device.receiveBeacon(this)) {
            ResourceLocation id = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(device.getType());
            NETWORK_DEVICES.get(device.getId()).setPos(currentPos);
        }
    }

    public UUID getId() {
        if (routerId == null) {
            routerId = UUID.randomUUID();
        }
        return routerId;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public CompoundTag toTag(boolean includePos) {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("id", getId());

        ListTag deviceList = new ListTag();
        NETWORK_DEVICES.forEach((id, device) -> deviceList.add(device.toTag(includePos)));
        tag.put("network_devices", deviceList);

        return tag;
    }

    public static Router fromTag(BlockPos pos, CompoundTag tag) {
        Router router = new Router(pos);
        router.routerId = tag.getUUID("id");

        ListTag deviceList = tag.getList("network_devices", 10);
        for (int i = 0; i < deviceList.size(); i++) {
            NetworkDevice device = NetworkDevice.fromTag(deviceList.getCompound(i));
            router.NETWORK_DEVICES.put(device.getId(), device);
        }
        return router;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof Router router))
            return false;
        return router.getId().equals(getId());
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }
}
