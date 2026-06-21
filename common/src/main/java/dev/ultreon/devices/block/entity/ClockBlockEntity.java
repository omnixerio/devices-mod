package dev.ultreon.devices.block.entity;

import dev.ultreon.devices.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ClockBlockEntity extends DeviceBlockEntity {
    public ClockBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        super(ModBlockEntities.CLOCK.get(), pos, state);
    }

    @Override
    public String getDeviceName() {
        return "Clock";
    }

    @Override
    public void tick() {

    }
}
