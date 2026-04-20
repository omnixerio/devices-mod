package dev.ultreon.devices.entity;

import dev.ultreon.devices.init.DeviceEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class SeatEntity extends Entity
{
    private double yOffset;
    private BlockPos blockPos;
    public SeatEntity(EntityType<SeatEntity> type, Level worldIn)
    {
        super(type, worldIn);
        this.setBoundingBox(new AABB(0.001F, 0.001F, 0.001F, -0.001F, -0.001F, -0.001F));
        this.setInvisible(true);
    }

    public SeatEntity(Level worldIn, BlockPos pos, double yOffset)
    {
        this(DeviceEntities.SEAT.get(), worldIn);
        this.setPos(pos.getX() + 0.5, pos.getY() + yOffset, pos.getZ() + 0.5);
        this.blockPos = pos;
    }

    @Override
    public double getEyeY() {
        return this.position().y + 0.5;
    }


    public void setYOffset(double offset) {
        this.yOffset = offset;
    }

    public void setViaYOffset(BlockPos pos) {
        blockPos = pos;
        this.setPos(pos.getX() + 0.5, pos.getY() + yOffset, pos.getZ() + 0.5);
    }



//    @Override
//    protected boolean shouldSetPosAfterLoading()
//    {
//        return false;
//    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder entityData) {

    }

    @Override
    public void tick()
    {
        if(!this.level().isClientSide() && (blockPos == null || !this.hasExactlyOnePlayerPassenger() || this.level().isEmptyBlock(blockPos)))
        {
            this.kill((ServerLevel) level());
        }
    }

    @Override
    public boolean hurtServer(@NonNull ServerLevel level, @NonNull DamageSource source, float damage) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput input) {

    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput output) {

    }


    public LivingEntity getControllingPassenger()
    {
        List<Entity> list = this.getPassengers();
        return list.isEmpty() ? null : list.getFirst() instanceof LivingEntity livingEntity ? livingEntity : null;
    }

//    @Override
//    protected void.json init() {}

    // FixMe - This is a temporary fix for the chair not being saved properly.
//    @Override
//    protected void readAdditionalSaveData(CompoundTag compound) {
//        if (compound.contains("DevicesChairX", Tag.TAG_INT) && compound.contains("DevicesChairY", Tag.TAG_INT) && compound.contains("DevicesChairZ", Tag.TAG_INT)) {
//            blockPos = new BlockPos(compound.getIntOr("DevicesChairX", 0), compound.getIntOr("DevicesChairY", 0), compound.getInt("DevicesChairZ"));
//        }
//    }
//
//    @Override
//    protected void addAdditionalSaveData(CompoundTag compound) {
//        if (blockPos == null) return;
//        compound.putInt("DevicesChairX", blockPos.getX());
//        compound.putInt("DevicesChairY", blockPos.getY());
//        compound.putInt("DevicesChairZ", blockPos.getZ());
//    }
}
