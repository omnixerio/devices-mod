package dev.ultreon.devices.api.event;

import dev.ultreon.devices.block.entity.DeviceBlockEntity;
import dev.ultreon.mods.xinexlib.event.block.BlockStateEvent;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public interface DeviceEvent extends BlockStateEvent {
    @NotNull DeviceBlockEntity getDeviceBlockEntity();

    default BlockState getState() {
        return getDeviceBlockEntity().getBlockState();
    }
}
