package dev.ultreon.devices.core;

import dev.ultreon.devices.block.entity.DeviceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Device {
    protected UUID id;
    protected String name;
    protected BlockPos pos;

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
    public BlockPos getPos() {
        return pos;
    }

    public void setPos(@Nullable BlockPos pos) {
        this.pos = pos;
    }

    public void update(@NotNull DeviceBlockEntity device) {
        name = device.getCustomName();
        pos = device.getBlockPos();
    }

    @Nullable
    public DeviceBlockEntity getDevice(@NotNull Level level) {
        if (pos == null)
            return null;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof DeviceBlockEntity deviceBlockEntity) {
            if (deviceBlockEntity.getId().equals(getId())) {
                return deviceBlockEntity;
            }
        }

        return null;
    }

    public CompoundTag toTag(boolean includePos) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", getId().toString());
        tag.putString("name", getName());
        if (includePos) {
            tag.putLong("pos", pos.asLong());
        }
        return tag;
    }

    public static Device fromTag(CompoundTag tag) {
        Device device = new Device();
        device.id = UUID.fromString(tag.getString("id"));
        device.name = tag.getString("name");
        if (tag.contains("pos", Tag.TAG_LONG)) {
            device.pos = BlockPos.of(tag.getLong("pos"));
        }
        return device;
    }
}
