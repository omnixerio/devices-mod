package com.ultreon.devices.block.entity;

import com.ultreon.devices.block.DeviceBlock;
import com.ultreon.devices.util.Colorable;
import com.ultreon.devices.util.Tickable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class DeviceBlockEntity extends SyncBlockEntity implements Tickable {
    private DyeColor color = DyeColor.RED;
    private UUID deviceId;
    private String name;

    public DeviceBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
    }

    @NotNull
    public final UUID getId() {
        if (deviceId == null) {
            deviceId = UUID.randomUUID();
        }
        return deviceId;
    }

    public abstract String getDeviceName();

    public String getCustomName() {
        return hasCustomName() ? name : getDeviceName();
    }

    public void setCustomName(String name) {
        this.name = name;
    }

    public boolean hasCustomName() {
        return name != null && StringUtils.isEmpty(name);
    }

    public Component getDisplayName() {
        return Component.literal(getCustomName());
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);

        tag.putString("deviceId", getId().toString());
        if (hasCustomName()) {
            tag.putString("name", name);
        }

        tag.putByte("color", (byte) color.getId());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);

        if (tag.contains("deviceId", Tag.TAG_STRING)) {
            deviceId = UUID.fromString(tag.getString("deviceId"));
        }
        if (tag.contains("name", Tag.TAG_STRING)) {
            name = tag.getString("name");
        }
        if (tag.contains("color", Tag.TAG_BYTE)) {
            color = DyeColor.byId(tag.getByte("color"));
        }
    }

    @Override
    public CompoundTag saveSyncTag() {
        CompoundTag tag = new CompoundTag();
        if (hasCustomName()) {
            tag.putString("name", name);
        }

        tag.putByte("color", (byte) color.getId());

        return tag;
    }

    public Block getBlock() {
        return getBlockState().getBlock();
    }

    public DeviceBlock getDeviceBlock() {
        Block block = getBlockState().getBlock();
        if (block instanceof DeviceBlock deviceBlock) {
            return deviceBlock;
        }
        return null;
    }

    public static abstract class Colored extends DeviceBlockEntity implements Colorable {
        private DyeColor color = DyeColor.RED;

        public Colored(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
            super(pType, pWorldPosition, pBlockState);
        }

        @Override
        public void load(@NotNull CompoundTag tag) {
            super.load(tag);
            if (tag.contains("color", Tag.TAG_BYTE)) {
                color = DyeColor.byId(tag.getByte("color"));
            }
        }

        @Override
        protected void saveAdditional(@NotNull CompoundTag tag) {
            super.saveAdditional(tag);
            tag.putByte("color", (byte) color.getId());
        }

        @Override
        public CompoundTag saveSyncTag() {
            CompoundTag tag = super.saveSyncTag();
            tag.putByte("color", (byte) color.getId());
            return tag;
        }

        public DyeColor getColor() {
            return color;
        }

        public void setColor(DyeColor color) {
            this.color = color;
        }
    }
}
