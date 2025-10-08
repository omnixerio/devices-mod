package dev.ultreon.devices.core.io.task;

import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.block.entity.computer.ComputerBlockEntity;
import dev.ultreon.devices.core.ComputerScreen;
import dev.ultreon.devices.core.io.FileSystem;
import dev.ultreon.devices.core.io.action.FileAction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.UUID;

/// @author MrCrayfish
public class TaskSendAction extends Task {
    private UUID uuid;
    private FileAction<?> action;
    private BlockPos pos;

    private FileSystem.Response response;

    public TaskSendAction() {
        super();
    }

    public TaskSendAction(UUID drive, FileAction<?> action) {
        this();
        this.uuid = drive;
        this.action = action;
        this.pos = ComputerScreen.getPos();
    }

    @Override
    public void prepareRequest(HolderLookup.Provider provider, CompoundTag tag) {
        tag.putUUID("uuid", uuid);
        tag.put("action", action.toTag());
        tag.putLong("pos", pos.asLong());
    }

    @Override
    public void processRequest(HolderLookup.Provider provider, CompoundTag tag, Level level, Player player) {
        FileAction<?> action = FileAction.fromTag(tag.getCompound("action"));
        BlockEntity tileEntity = level.getChunkAt(BlockPos.of(tag.getLong("pos"))).getBlockEntity(BlockPos.of(tag.getLong("pos")), LevelChunk.EntityCreationType.IMMEDIATE);
        if (tileEntity instanceof ComputerBlockEntity laptop) {
            response = laptop.getFileSystem().readAction(tag.getUUID("uuid"), action, level);
            this.setSuccessful();
        }
    }

    @Override
    public void prepareResponse(HolderLookup.Provider provider, CompoundTag tag) {
        tag.put("response", response.toTag());
    }

    @Override
    public void processResponse(HolderLookup.Provider provider, CompoundTag tag) {

    }
}
