package dev.ultreon.devices.block.entity;

import dev.ultreon.devices.block.LaptopBlock;
import dev.ultreon.devices.entity.SeatEntity;
import dev.ultreon.devices.init.ModBlockEntities;
import dev.ultreon.devices.util.Colorable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class OfficeChairBlockEntity extends SyncBlockEntity implements Colorable {
    private DyeColor color = DyeColor.RED;

    public OfficeChairBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModBlockEntities.SEAT.get(), pWorldPosition, pBlockState);
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
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.loadAdditional(compound, provider);
        if (compound.contains("color", Tag.TAG_BYTE)) {
            color = DyeColor.byId(compound.getByte("color"));
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putByte("color", (byte) color.getId());
    }

    @Override
    public CompoundTag saveSyncTag() {
        CompoundTag tag = new CompoundTag();
        tag.putByte("color", (byte) color.getId());
        return tag;
    }

    @Environment(EnvType.CLIENT)
    public float getRotation() {
        List<SeatEntity> seats = level.getEntitiesOfClass(SeatEntity.class, new AABB(getBlockPos()));
        if (!seats.isEmpty()) {
            SeatEntity seat = seats.get(0);
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
}