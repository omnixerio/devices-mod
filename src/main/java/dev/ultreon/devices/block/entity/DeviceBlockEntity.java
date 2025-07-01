package dev.ultreon.devices.block.entity;

import dev.ultreon.devices.DyeColor;
import dev.ultreon.devices.block.DeviceBlock;
import dev.ultreon.devices.util.Colorable;
import dev.ultreon.devices.util.Tickable;
import dev.ultreon.quantum.block.Block;
import dev.ultreon.quantum.block.entity.BlockEntityType;
import dev.ultreon.quantum.text.TextObject;
import dev.ultreon.quantum.ubo.DataTypes;
import dev.ultreon.quantum.ubo.types.MapType;
import dev.ultreon.quantum.world.World;
import dev.ultreon.quantum.world.vec.BlockVec;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class DeviceBlockEntity extends SyncBlockEntity implements Tickable {
    private DyeColor color = DyeColor.RED;
    private UUID deviceId;
    private String name;

    public DeviceBlockEntity(BlockEntityType<?> type, World world, BlockVec pos) {
        super(type, world, pos);
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

    public TextObject getDisplayName() {
        return TextObject.literal(getCustomName());
    }

    @Override
    public MapType save(@NotNull MapType tag) {
        super.save(tag);

        tag.putString("deviceId", getId().toString());
        if (hasCustomName()) {
            tag.putString("name", name);
        }

        tag.putByte("color", color.getId());
        return tag;
    }

    @Override
    public void load(@NotNull MapType tag) {
        super.load(tag);

        if (tag.contains("deviceId", DataTypes.STRING)) {
            deviceId = UUID.fromString(tag.getString("deviceId"));
        }
        if (tag.contains("name", DataTypes.STRING)) {
            name = tag.getString("name");
        }
        if (tag.contains("color", DataTypes.BYTE)) {
            color = DyeColor.byId(tag.getByte("color"));
        }
    }

    @Override
    public MapType saveSyncTag() {
        MapType tag = new MapType();
        if (hasCustomName()) {
            tag.putString("name", name);
        }

        tag.putByte("color", color.getId());

        return tag;
    }

    public Block getBlock() {
        return getBlockMeta().getBlock();
    }

    public DeviceBlock getDeviceBlock() {
        Block block = getBlockMeta().getBlock();
        if (block instanceof DeviceBlock deviceBlock) {
            return deviceBlock;
        }
        return null;
    }

    public static abstract class Colored extends DeviceBlockEntity implements Colorable {
        private DyeColor color = DyeColor.RED;

        protected Colored(BlockEntityType<?> type, World world, BlockVec pos) {
            super(type, world, pos);
        }

        @Override
        public void load(@NotNull MapType tag) {
            super.load(tag);
            if (tag.contains("color", DataTypes.BYTE)) {
                color = DyeColor.byId(tag.getByte("color"));
            }
        }

        @Override
        public MapType save(@NotNull MapType tag) {
            super.save(tag);
            tag.putByte("color", color.getId());
            return tag;
        }

        @Override
        public MapType saveSyncTag() {
            MapType tag = super.saveSyncTag();
            tag.putByte("color", color.getId());
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
