package com.ultreon.devices.block.entity;

import com.ultreon.devices.block.LaptopBlock;
import com.ultreon.devices.entity.SeatEntity;
import com.ultreon.devices.init.DeviceBlockEntities;
import com.ultreon.devices.util.Colorable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class OfficeChairBlockEntity extends SyncBlockEntity implements Colorable {
    private DyeColor color = DyeColor.RED;

    public OfficeChairBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(DeviceBlockEntities.SEAT.get(), pWorldPosition, pBlockState);
    }

    @Override
    public DyeColor getColor() {
        return color;
    }

    @Override
    public void setColor(DyeColor color) {
        this.color = color;
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        color = DyeColor.byId(input.getByteOr("color", (byte) 0));
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        output.putByte("color", (byte) color.getId());
    }

    @Override
    public CompoundTag saveSyncTag() {
        CompoundTag tag = new CompoundTag();
        tag.putByte("color", (byte) color.getId());
        return tag;
    }

    public float getRotation() {
        Level level = this.level;
        if (level == null) return 0;
        List<SeatEntity> seats = level.getEntitiesOfClass(SeatEntity.class, new AABB(getBlockPos()));
        if (!seats.isEmpty()) {
            SeatEntity seat = seats.getFirst();
            if (seat.getControllingPassenger() != null) {
                if (seat.getControllingPassenger() != null) {
                    LivingEntity living = seat.getControllingPassenger();
                    //living.yHeadRotO = living.yHeadRot;
                    //living.yRotO = living.yHeadRot;
                    living.setYBodyRot(living.yHeadRot);
                    //living.renderYawOffset = living.rotationYaw;
                    //living.prevRenderYawOffset = living.rotationYaw;
                    return living.yHeadRot;
                }
                return seat.getControllingPassenger().getYHeadRot();
            }
        }
        var direction = this.getBlockState().getValue(LaptopBlock.FACING).getClockWise().toYRot();
        return direction - 90F;
    }
}