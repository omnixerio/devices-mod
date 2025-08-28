package com.ultreon.devices.core.task;

import com.ultreon.devices.Devices;
import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.block.entity.ComputerBlockEntity;
import com.ultreon.devices.debug.DebugLog;
import com.ultreon.devices.object.AppInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * @author MrCrayfish
 */
public class TaskInstallApp extends Task {
    private String appId;
    private BlockPos laptopPos;
    private boolean install;

    public TaskInstallApp() {
        super("install_app");
    }

    public TaskInstallApp(AppInfo info, BlockPos laptopPos, boolean install) {
        this();
        this.appId = info.getFormattedId();
        this.laptopPos = laptopPos;
        this.install = install;
    }

    @Override
    public void prepareRequest(CompoundTag tag) {
        tag.putString("appId", appId);
        tag.putLong("pos", laptopPos.asLong());
        tag.putBoolean("install", install);
        DebugLog.log("Prep message " + appId + ", " + laptopPos.toString() + ", " + install);
    }

    @Override
    public void processRequest(CompoundTag tag, Level level, Player player) {
        DebugLog.log("Proc message " + tag.getString("appId") + ", " +  BlockPos.of(tag.getLong("pos")) + ", " + tag.getBoolean("install"));
        String appId = tag.getString("appId");
        DebugLog.log(level.getBlockState(BlockPos.of(tag.getLong("pos"))).getBlock());
        BlockEntity tileEntity = level.getChunkAt(BlockPos.of(tag.getLong("pos"))).getBlockEntity(BlockPos.of(tag.getLong("pos")), LevelChunk.EntityCreationType.IMMEDIATE);
        DebugLog.log(tileEntity);
        if (tileEntity instanceof ComputerBlockEntity laptop) {
            CompoundTag systemData = laptop.getSystemData();
            ListTag list = systemData.getList("InstalledApps", Tag.TAG_STRING);

            if (tag.getBoolean("install")) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.getString(i).equals(appId)) {
                        Devices.LOGGER.warn("Found duplicate, noping out");
                        return;
                    }
                }
                list.add(StringTag.valueOf(appId));
                this.setSuccessful();
            } else {
                list.removeIf(appTag -> {
                    if (appTag.getAsString().equals(appId)) {
                        this.setSuccessful();
                        return true;
                    } else {
                        return false;
                    }
                });
            }
            systemData.put("InstalledApps", list);
        }
        if (!this.isSucessful()) {
            Devices.LOGGER.info("Installing {} unsuccessful", appId);
        }
    }


    @Override
    public void prepareResponse(CompoundTag tag) {

    }

    @Override
    public void processResponse(CompoundTag tag) {

    }
}
