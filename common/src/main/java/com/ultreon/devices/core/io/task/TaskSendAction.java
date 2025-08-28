package com.ultreon.devices.core.io.task;

import com.ultreon.devices.api.io.Drive;
import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.block.entity.ComputerBlockEntity;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.core.io.FileSystem;
import com.ultreon.devices.core.io.action.FileAction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * @author MrCrayfish
 */
public class TaskSendAction extends Task {
    private String uuid;
    private FileAction action;
    private BlockPos pos;

    private FileSystem.Response response;

    public TaskSendAction() {
        super("send_action");
    }

    public TaskSendAction(Drive drive, FileAction action) {
        this();
        this.uuid = drive.getUUID().toString();
        this.action = action;
        this.pos = Laptop.getPos();
    }

    @Override
    public void prepareRequest(CompoundTag tag) {
        tag.putString("uuid", uuid);
        tag.put("action", action.toTag());
        tag.putLong("pos", pos.asLong());
    }

    @Override
    public void processRequest(CompoundTag tag, Level level, Player player) {
        FileAction action = FileAction.fromTag(tag.getCompound("action"));
        BlockEntity tileEntity = level.getChunkAt(BlockPos.of(tag.getLong("pos"))).getBlockEntity(BlockPos.of(tag.getLong("pos")), LevelChunk.EntityCreationType.IMMEDIATE);
        if (tileEntity instanceof ComputerBlockEntity laptop) {
            response = laptop.getFileSystem().readAction(tag.getString("uuid"), action, level);
            this.setSuccessful();
        }
    }

    @Override
    public void prepareResponse(CompoundTag tag) {
        tag.put("response", response.toTag());
    }

    @Override
    public void processResponse(CompoundTag tag) {

    }
}
