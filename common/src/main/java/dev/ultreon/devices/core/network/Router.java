package dev.ultreon.devices.core.network;

import dev.ultreon.devices.DeviceConfig;
import dev.ultreon.devices.api.event.RouterBeaconEvent;
import dev.ultreon.devices.block.entity.NetworkDeviceBlockEntity;
import dev.ultreon.devices.block.entity.RouterBlockEntity;
import dev.ultreon.devices.init.DeviceBlockEntities;
import dev.ultreon.mods.xinexlib.event.system.EventSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class Router {
    private final Map<UUID, NetworkDevice> networkDevices = new HashMap<>();

    private int timer;
    private UUID routerId;
    private BlockPos pos;

    public Router(BlockPos pos) {
        this.pos = pos;
    }

    public void tick(RouterBlockEntity blockEntity, Level level) {
        if (++timer >= DeviceConfig.BEACON_INTERVAL.get()) {
            sendBeacon(blockEntity, level);
            timer = 0;
        }
    }

    public boolean addDevice(UUID id, String name) {
        if (networkDevices.size() >= DeviceConfig.MAX_DEVICES.get()) {
            return networkDevices.containsKey(id);
        }
        if (!networkDevices.containsKey(id)) {
            NetworkDevice networkDevice = new NetworkDevice(id, name, this);
            if (!EventSystem.MAIN.publish(new DeviceConnectEvent(this, networkDevice)).isCanceled()) {
                networkDevices.put(id, networkDevice);
            }
        }
        timer = DeviceConfig.BEACON_INTERVAL.get();
        return true;
    }

    public boolean addDevice(NetworkDeviceBlockEntity device) {
        if (networkDevices.size() >= DeviceConfig.MAX_DEVICES.get()) {
            return networkDevices.containsKey(device.getId());
        }
        if (!networkDevices.containsKey(device.getId())) {
            NetworkDevice networkDevice = new NetworkDevice(device);
            if (!EventSystem.MAIN.publish(new DeviceConnectEvent(this, networkDevice)).isCanceled()) {
                networkDevices.put(device.getId(), networkDevice);
            }
        }
        return true;
    }

    public boolean isDeviceRegistered(NetworkDeviceBlockEntity device) {
        return networkDevices.containsKey(device.getId());
    }

    public boolean isDeviceConnected(NetworkDeviceBlockEntity device) {
        return isDeviceRegistered(device) && networkDevices.get(device.getId()).getPos() != null;
    }

    public void removeDevice(NetworkDeviceBlockEntity device) {
        networkDevices.remove(device.getId());
    }

    @Nullable
    public NetworkDeviceBlockEntity getDevice(Level level, UUID id) {
        return networkDevices.containsKey(id) ? networkDevices.get(id).getDevice(level) : null;
    }

    public Collection<NetworkDevice> getNetworkDevices() {
        return networkDevices.values();
    }

    public Collection<NetworkDevice> getConnectedDevices(Level level) {
        Optional<RouterBlockEntity> blockEntity = level.getBlockEntity(pos, DeviceBlockEntities.ROUTER.get());
        if (blockEntity.isEmpty())
            return Collections.emptyList();
        sendBeacon(blockEntity.get(), level);
        return networkDevices.values().stream().filter(device -> device.getPos() != null).toList();
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
                return BuiltInRegistries.BLOCK_ENTITY_TYPE.wrapAsHolder(blockEntity.getType()).is(targetType);
            }
            return false;
        };
        return getConnectedDevices(level).stream().filter(deviceType).toList();
    }

    private void sendBeacon(RouterBlockEntity blockEntity, Level level) {
        if (level.isClientSide)
            return;

        EventSystem.MAIN.publish(new RouterBeaconEvent(this, blockEntity, networkDevices));

        networkDevices.forEach((uuid, device) -> device.setPos(null));
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
        if (blockEntity instanceof NetworkDeviceBlockEntity device) {
            if (!networkDevices.containsKey(device.getId()))
                return;
            if (device.receiveBeacon(this)) {
                networkDevices.get(device.getId()).setPos(currentPos);
            }
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
        networkDevices.forEach((id, device) -> deviceList.add(device.toTag(includePos)));
        tag.put("network_devices", deviceList);

        return tag;
    }

    public static Router fromTag(BlockPos pos, CompoundTag tag) {
        Router router = new Router(pos);
        router.routerId = tag.getUUID("id");

        ListTag deviceList = tag.getList("network_devices", 10);
        for (int i = 0; i < deviceList.size(); i++) {
            NetworkDevice device = NetworkDevice.fromTag(deviceList.getCompound(i));
            router.networkDevices.put(device.getId(), device);
        }
        return router;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Router router)) return false;
        return Objects.equals(routerId, router.routerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(routerId);
    }
}
