package dev.ultreon.devices.programs.system.task;

import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.block.entity.computer.ComputerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

public class TaskUpdateSystemData extends Task {
    private BlockPos pos;
    private CompoundTag data;

    public TaskUpdateSystemData() {
        super("update_system_data");
    }

    public TaskUpdateSystemData(BlockPos pos, CompoundTag data) {
        this();
        this.pos = pos;
        this.data = data;
    }

    @Override
    public void prepareRequest(CompoundTag tag) {
        tag.putLong("pos", pos.asLong());
        tag.put("data", this.data);
    }

    @Override
    public void processRequest(CompoundTag tag, Level level, Player player) {
        BlockPos pos = BlockPos.of(tag.getLong("pos"));
        BlockEntity tileEntity = level.getChunkAt(pos).getBlockEntity(pos, LevelChunk.EntityCreationType.IMMEDIATE);
        if (tileEntity instanceof ComputerBlockEntity laptop)
            laptop.setSystemData(tag.getCompound("data"));
        this.setSuccessful();
    }

    @Override
    public void prepareResponse(CompoundTag tag) {

    }

    @Override
    public void processResponse(CompoundTag tag) {

    }
}
