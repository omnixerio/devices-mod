package com.ultreon.devices.item;

import com.ultreon.devices.ModDeviceTypes;
import com.ultreon.devices.IDeviceType;
import dev.architectury.injectables.annotations.PlatformOnly;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class DeviceItem extends BlockItem implements IDeviceType {
    private final ModDeviceTypes deviceType;

    public DeviceItem(Block block, Properties properties, ModDeviceTypes deviceType) {
        super(block, properties.stacksTo(1));
        this.deviceType = deviceType;
    }

    //This method is still bugged due to Forge.
    @Nullable
    @PlatformOnly(PlatformOnly.FORGE)
//    @Override
    public CompoundTag getShareTag(ItemStack stack) {
        CompoundTag tag = new CompoundTag();
        if (stack.getTag() != null && stack.getTag().contains("display", Tag.TAG_COMPOUND)) {
            tag.put("display", Objects.requireNonNull(stack.getTag().get("display")));
        }
        return tag;
    }

    public ModDeviceTypes getDeviceType() {
        return deviceType;
    }
}
