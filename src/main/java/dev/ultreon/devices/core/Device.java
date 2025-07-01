package dev.ultreon.devices.core;

import dev.ultreon.devices.block.entity.DeviceBlockEntity;
import dev.ultreon.quantum.block.entity.BlockEntity;
import dev.ultreon.quantum.ubo.DataTypes;
import dev.ultreon.quantum.ubo.types.MapType;
import dev.ultreon.quantum.world.World;
import dev.ultreon.quantum.world.vec.BlockVec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Device {
    protected UUID id;
    protected String name;
    protected BlockVec vec;

    protected Device() {

    }

    public Device(@NotNull DeviceBlockEntity device) {
        this.id = device.getId();
        update(device);
    }

    public Device(@NotNull UUID id, @NotNull String name) {
        this.id = id;
        this.name = name;
    }

    @NotNull
    public UUID getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Nullable
    public BlockVec getVec() {
        return vec;
    }

    public void setVec(@Nullable BlockVec vec) {
        this.vec = vec;
    }

    public void update(@NotNull DeviceBlockEntity device) {
        name = device.getCustomName();
        vec = device.pos();
    }

    @Nullable
    public DeviceBlockEntity getDevice(@NotNull World world) {
        if (vec == null)
            return null;

        BlockEntity blockEntity = world.getBlockEntity(vec);
        if (blockEntity instanceof DeviceBlockEntity deviceBlockEntity) {
            if (deviceBlockEntity.getId().equals(getId())) {
                return deviceBlockEntity;
            }
        }

        return null;
    }

    public MapType toDataTypes(boolean includeVec) {
        MapType data = new MapType();
        data.putString("id", getId().toString());
        data.putString("name", getName());
        if (includeVec) {
            data.putInt("vecX", vec.x);
            data.putInt("vecY", vec.y);
            data.putInt("vecZ", vec.z);
        }
        return data;
    }

    public static Device fromDataTypes(MapType data) {
        Device device = new Device();
        device.id = UUID.fromString(data.getString("id"));
        device.name = data.getString("name");
        if (data.contains("vec", DataTypes.LONG)) {
            device.vec = new BlockVec(data.getInt("vecX"), data.getInt("vecY"), data.getInt("vecZ"));
        }
        return device;
    }
}
