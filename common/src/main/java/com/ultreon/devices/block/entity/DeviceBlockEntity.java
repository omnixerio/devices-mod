package com.ultreon.devices.block.entity;

import com.ultreon.devices.block.DeviceBlock;
import com.ultreon.devices.util.Colorable;
import com.ultreon.devices.util.Tickable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        output.putString("deviceId", getId().toString());
        if (hasCustomName()) {
            output.putString("name", name);
        }

        output.putByte("color", (byte) color.getId());
    }

    @Override
    public void loadAdditional(@NotNull ValueInput tag) {
        super.loadAdditional(tag);

        deviceId = UUID.fromString(tag.getStringOr("deviceId", getId().toString()));
        name = tag.getStringOr("name", name);
        color = DyeColor.byId(tag.getByteOr("color", (byte) color.getId()));
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
        public void loadAdditional(@NotNull ValueInput tag) {
            super.loadAdditional(tag);
            color = DyeColor.byId(tag.getByteOr("color", (byte) color.getId()));
        }

        @Override
        protected void saveAdditional(@NotNull ValueOutput tag) {
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
