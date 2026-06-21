package dev.ultreon.devices.entity;

import dev.ultreon.devices.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SeatEntity extends Entity {
    private double yOffset;
    private BlockPos blockPos;

    public SeatEntity(EntityType<SeatEntity> type, Level worldIn) {
        super(type, worldIn);
        this.setBoundingBox(new AABB(0.001F, 0.001F, 0.001F, -0.001F, -0.001F, -0.001F));
        this.setInvisible(true);
    }

    @Override
    public double getEyeY() {
        return 0;
    }

    public SeatEntity(Level worldIn, BlockPos pos, double yOffset) {
        this(ModEntities.SEAT.get(), worldIn);
        this.setPos(pos.getX() + 0.5, pos.getY() + yOffset, pos.getZ() + 0.5);
        this.blockPos = pos;
    }


    public void setYOffset(double offset) {
        this.yOffset = offset;
    }

    public void setViaYOffset(BlockPos pos) {
        blockPos = pos;
        this.setPos(pos.getX() + 0.5, pos.getY() + yOffset, pos.getZ() + 0.5);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    public void tick() {
        if (!this.level().isClientSide && (blockPos == null || !this.hasExactlyOnePlayerPassenger() || this.level().isEmptyBlock(blockPos))) {
            this.kill();
        }
    }


    public LivingEntity getControllingPassenger() {
        List<Entity> list = this.getPassengers();
        return list.isEmpty() ? null : list.getFirst() instanceof LivingEntity livingEntity ? livingEntity : null;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
        return new ClientboundAddEntityPacket(this, serverEntity);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("DevicesChairX", Tag.TAG_INT) && compound.contains("DevicesChairY", Tag.TAG_INT) && compound.contains("DevicesChairZ", Tag.TAG_INT)) {
            blockPos = new BlockPos(compound.getInt("DevicesChairX"), compound.getInt("DevicesChairY"), compound.getInt("DevicesChairZ"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (blockPos == null) return;
        compound.putInt("DevicesChairX", blockPos.getX());
        compound.putInt("DevicesChairY", blockPos.getY());
        compound.putInt("DevicesChairZ", blockPos.getZ());
    }
}
