package dev.ultreon.devices.block.entity;

import dev.ultreon.devices.block.LaptopBlock;
import dev.ultreon.devices.entity.Seat;
import dev.ultreon.devices.init.DeviceBlockEntities;
import dev.ultreon.devices.util.Colorable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

@SuppressWarnings("ALL")
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
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("color", Tag.TAG_BYTE)) {
            color = DyeColor.byId(tag.getByte("color"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putByte("color", (byte) color.getId());
    }

    @Override
    public CompoundTag saveSyncTag() {
        CompoundTag tag = new CompoundTag();
        tag.putByte("color", (byte) color.getId());
        return tag;
    }

    public float getRotation() {
        List<Seat> seats = level.getEntitiesOfClass(Seat.class, new AABB(getBlockPos()));
        if (!seats.isEmpty()) {
            Seat seat = seats.get(0);
            if (seat.getControllingPassenger() != null) {
                if (seat.getControllingPassenger() instanceof LivingEntity) {
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

    public float getRotation(float partialTick) {
        List<Seat> seats = level.getEntitiesOfClass(Seat.class, new AABB(getBlockPos()));
        if (!seats.isEmpty()) {
            Seat seat = seats.get(0);
            if (seat.getControllingPassenger() != null) {
                if (seat.getControllingPassenger() instanceof LivingEntity) {
                    LivingEntity living = (LivingEntity) seat.getControllingPassenger();
                    living.setYBodyRot(living.yHeadRot);
                    return living.yHeadRot;
                }
                return seat.getControllingPassenger().getViewYRot(partialTick);
            }
        }
        var direction = this.getBlockState().getValue(LaptopBlock.FACING).getClockWise().toYRot();
        return direction - 90F;
    }
}