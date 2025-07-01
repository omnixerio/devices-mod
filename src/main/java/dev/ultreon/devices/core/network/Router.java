package dev.ultreon.devices.core.network;

import dev.ultreon.devices.DeviceConfig;
import dev.ultreon.devices.block.entity.NetworkDeviceBlockEntity;
import dev.ultreon.quantum.world.World;
import dev.ultreon.quantum.world.vec.BlockVec;
import net.minecraft.core.BlockVec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.world.World;
import net.minecraft.world.world.block.entity.BlockEntity;
import net.minecraft.world.world.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

public class Router {
    private final Map<UUID, NetworkDevice> NETWORK_DEVICES = new HashMap<>();

    private int timer;
    private UUID routerId;
    private BlockVec vec;

    public Router(BlockVec vec) {
        this.vec = vec;
    }

    public void tick(World world) {
        if (++timer >= DeviceConfig.BEACON_INTERVAL.getValue()) {
            sendBeacon(world);
            timer = 0;
        }
    }

    public boolean addDevice(UUID id, String name) {
        if (NETWORK_DEVICES.size() >= DeviceConfig.MAX_DEVICES.getValue()) {
            return NETWORK_DEVICES.containsKey(id);
        }
        if (!NETWORK_DEVICES.containsKey(id)) {
            NETWORK_DEVICES.put(id, new NetworkDevice(id, name, this));
        }
        timer = DeviceConfig.BEACON_INTERVAL.getValue();
        return true;
    }

    public boolean addDevice(NetworkDeviceBlockEntity device) {
        if (NETWORK_DEVICES.size() >= DeviceConfig.MAX_DEVICES.getValue()) {
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
        return isDeviceRegistered(device) && NETWORK_DEVICES.get(device.getId()).getVec() != null;
    }

    public void removeDevice(NetworkDeviceBlockEntity device) {
        NETWORK_DEVICES.remove(device.getId());
    }

    @Nullable
    public NetworkDeviceBlockEntity getDevice(World world, UUID id) {
        return NETWORK_DEVICES.containsKey(id) ? NETWORK_DEVICES.get(id).getDevice(world) : null;
    }

    public Collection<NetworkDevice> getNetworkDevices() {
        return NETWORK_DEVICES.values();
    }

    public Collection<NetworkDevice> getConnectedDevices(World world) {
        sendBeacon(world);
        return NETWORK_DEVICES.values().stream().filter(device -> device.getVec() != null).toList();
    }

    public Collection<NetworkDevice> getConnectedDevices(final World world, BlockEntityType<?> targetType) {
        final Predicate<NetworkDevice> deviceType = networkDevice -> {
            if (networkDevice.getVec() == null)
                return false;

            BlockEntity blockEntity = world.getBlockEntity(networkDevice.getVec());
            return blockEntity instanceof NetworkDeviceBlockEntity device && targetType.equals(device.getType());

        };
        return getConnectedDevices(world).stream().filter(deviceType).toList();
    }

    public Collection<NetworkDevice> getConnectedDevices(final World world, TagKey<BlockEntityType<?>> targetType) {
        final Predicate<NetworkDevice> deviceType = networkDevice -> {
            if (networkDevice.getVec() == null)
                return false;

            BlockEntity blockEntity = world.getBlockEntity(networkDevice.getVec());

            if (blockEntity instanceof NetworkDeviceBlockEntity device) {
                return BuiltInRegistries.BLOCK_ENTITY_TYPE.wrapAsHolder(blockEntity.getType()).is(targetType);
            }
            return false;
        };
        return getConnectedDevices(world).stream().filter(deviceType).toList();
    }

    private void sendBeacon(World world) {
        if (world.isClientSide)
            return;

        NETWORK_DEVICES.forEach((uuid, device) -> device.setVec(null));
        int range = DeviceConfig.SIGNAL_RANGE.get();
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockVec currentVec = new BlockVec(vec.getX() + x, vec.getY() + y, vec.getZ() + z);
                    BlockEntity blockEntity = world.getBlockEntity(currentVec);
                    if (blockEntity instanceof NetworkDeviceBlockEntity device) {
                        if (!NETWORK_DEVICES.containsKey(device.getId()))
                            continue;
                        if (device.receiveBeacon(this)) {
                            NETWORK_DEVICES.get(device.getId()).setVec(currentVec);
                        }
                    }
                }
            }
        }
    }

    public UUID getId() {
        if (routerId == null) {
            routerId = UUID.randomUUID();
        }
        return routerId;
    }

    public BlockVec getVec() {
        return vec;
    }

    public void setVec(BlockVec vec) {
        this.vec = vec;
    }

    public CompoundTag toTag(boolean includeVec) {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("id", getId());

        ListTag deviceList = new ListTag();
        NETWORK_DEVICES.forEach((id, device) -> {
            deviceList.add(device.toTag(includeVec));
        });
        tag.put("network_devices", deviceList);

        return tag;
    }

    public static Router fromTag(BlockVec vec, CompoundTag tag) {
        Router router = new Router(vec);
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
}
