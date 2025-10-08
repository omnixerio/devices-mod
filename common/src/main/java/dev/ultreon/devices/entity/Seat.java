package dev.ultreon.devices.entity;

import dev.ultreon.devices.block.OfficeChairBlock;
import dev.ultreon.devices.init.DeviceEntities;
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

/// ## SeatEntity
/// The entity that represents a seat
/// Is used for [OfficeChairBlock]
///
/// @author [MrCrayfish](https://github.com/MrCrayfish), [Qubix](https://github.com/Qubilux)
/// @see OfficeChairBlock
public class Seat extends Entity {
    private double yOffset;
    private BlockPos blockPos;

    /// Constructor for the seat entity
    ///
    /// @param type    The type of the entity
    /// @param worldIn The world the entity is in
    public Seat(EntityType<? extends Seat> type, Level worldIn) {
        super(type, worldIn);
        this.setBoundingBox(new AABB(0.001F, 0.001F, 0.001F, -0.001F, -0.001F, -0.001F));
        this.setInvisible(true);
    }

    /// Constructor for the seat entity
    ///
    /// @param worldIn The world the entity is in
    /// @param pos     The position of the entity
    /// @param yOffset The y position of the entity
    public Seat(Level worldIn, BlockPos pos, double yOffset) {
        this(DeviceEntities.SEAT.get(), worldIn);
        this.setPos(pos.getX() + 0.5, pos.getY() + yOffset, pos.getZ() + 0.5);
        this.blockPos = pos;
    }

    /// Returns the y position of the eye
    @Override
    public double getEyeY() {
        return 0;
    }


    /// Sets the y position of the eye
    public void setYOffset(double offset) {
        this.yOffset = offset;
    }

    /// Sets the y position of the entity
    public void setViaYOffset(BlockPos pos) {
        blockPos = pos;
        this.setPos(pos.getX() + 0.5, pos.getY() + yOffset, pos.getZ() + 0.5);
    }

    /// Defines the synched data
    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {

    }

    /// Ticks the entity
    @Override
    public void tick() {
        if (!this.level().isClientSide && (blockPos == null || !this.hasExactlyOnePlayerPassenger() || this.level().isEmptyBlock(blockPos))) {
            this.kill();
        }
    }


    /// Returns the controlling passenger
    public LivingEntity getControllingPassenger() {
        List<Entity> list = this.getPassengers();
        return list.isEmpty() ? null : list.getFirst() instanceof LivingEntity livingEntity ? livingEntity : null;
    }

    /// Creates the packet to add the entity
    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket(@NotNull ServerEntity serverEntity) {
        return new ClientboundAddEntityPacket(this, serverEntity);
    }

    /// Reads the block position from the save data
    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("DevicesChairX", Tag.TAG_INT) && compound.contains("DevicesChairY", Tag.TAG_INT) && compound.contains("DevicesChairZ", Tag.TAG_INT)) {
            blockPos = new BlockPos(compound.getInt("DevicesChairX"), compound.getInt("DevicesChairY"), compound.getInt("DevicesChairZ"));
        }
    }

    /// Adds the block position to the save data
    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compound) {
        if (blockPos == null) return;
        compound.putInt("DevicesChairX", blockPos.getX());
        compound.putInt("DevicesChairY", blockPos.getY());
        compound.putInt("DevicesChairZ", blockPos.getZ());
    }
}
