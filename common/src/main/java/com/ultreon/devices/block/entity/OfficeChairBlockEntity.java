package com.ultreon.devices.block.entity;

import com.ultreon.devices.block.LaptopBlock;
import com.ultreon.devices.entity.SeatEntity;
import com.ultreon.devices.init.DeviceBlockEntities;
import com.ultreon.devices.util.Colorable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class OfficeChairBlockEntity extends SyncBlockEntity implements Colorable
{
    private DyeColor color = DyeColor.RED;

    public OfficeChairBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(DeviceBlockEntities.SEAT.get(), pWorldPosition, pBlockState);
    }

    @Override
    public DyeColor getColor()
    {
        return color;
    }

    @Override
    public void setColor(DyeColor color)
    {
        this.color = color;
    }

    @Override
    public void load(CompoundTag compound)
    {
        super.load(compound);
        if(compound.contains("color", Tag.TAG_BYTE))
        {
            color = DyeColor.byId(compound.getByte("color"));
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound)
    {
        super.saveAdditional(compound);
        compound.putByte("color", (byte) color.getId());
    }

    @Override
    public CompoundTag saveSyncTag()
    {
        CompoundTag tag = new CompoundTag();
        tag.putByte("color", (byte) color.getId());
        return tag;
    }

    @Environment(EnvType.CLIENT)
    public float getRotation()
    {
        List<SeatEntity> seats = level.getEntitiesOfClass(SeatEntity.class, new AABB(getBlockPos()));
        if(!seats.isEmpty())
        {
            SeatEntity seat = seats.get(0);
            if(seat.getControllingPassenger() != null)
            {
                if(seat.getControllingPassenger() instanceof LivingEntity)
                {
                    LivingEntity living = (LivingEntity) seat.getControllingPassenger();
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