package dev.ultreon.devices.item;

import dev.ultreon.devices.IDeviceType;
import dev.ultreon.devices.ModDeviceTypes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

/// <summary>
///     A device item.
/// </summary>
///
/// @author [MrCrayfish](https://github.com/MrCrayfish), [Qubix](https://github.com/Qubilux)
public class DeviceItem extends BlockItem implements IDeviceType {
    private final ModDeviceTypes deviceType;

    /// <summary>
    ///     Initializes a new instance of the <see cref="DeviceItem"/> class.
    /// </summary>
    ///
    /// @param block      The device block to use.
    /// @param properties The item properties.
    /// @param deviceType The type of the device.
    public DeviceItem(Block block, Properties properties, ModDeviceTypes deviceType) {
        super(block, properties.stacksTo(1));
        this.deviceType = deviceType;
    }

    /*
    //This method is still bugged due to NeoForge.
    @Nullable
    @PlatformOnly(PlatformOnly.FORGE)
    public CompoundTag getShareTag(ItemStack stack) {
        CompoundTag tag = new CompoundTag();
        if (stack.getTag() != null && stack.getTag().contains("display", Tag.TAG_COMPOUND)) {
            tag.put("display", Objects.requireNonNull(stack.getTag().get("display")));
        }
        return tag;
    }
     */

    /// <summary>
    ///     Gets the type of the device.
    /// </summary>
    ///
    /// @return The type of the device.
    @Override
    public ModDeviceTypes getDeviceType() {
        return deviceType;
    }
}
