package dev.ultreon.devices.core.task;

import dev.ultreon.devices.UltreonDevicesCommon;
import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.block.entity.ComputerBlockEntity;
import dev.ultreon.devices.debug.DebugLog;
import dev.ultreon.devices.object.AppInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
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
        DebugLog.log("Proc message " + tag.getString("appId").orElse(null) + ", " +  BlockPos.of(tag.getLongOr("pos", 0)) + ", " + tag.getBooleanOr("install", false));
        String appId = tag.getString("appId").orElse(null);
        DebugLog.log(level.getBlockState(BlockPos.of(tag.getLongOr("pos", 0))).getBlock());
        BlockEntity tileEntity = level.getChunkAt(BlockPos.of(tag.getLongOr("pos", 0))).getBlockEntity(BlockPos.of(tag.getLongOr("pos", 0)), LevelChunk.EntityCreationType.IMMEDIATE);
        DebugLog.log(tileEntity);
        if (tileEntity instanceof ComputerBlockEntity laptop) {
            CompoundTag systemData = laptop.getSystemData();
            ListTag list = systemData.getListOrEmpty("InstalledApps");

            if (tag.getBooleanOr("install", false)) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.getString(i).equals(appId)) {
                        UltreonDevicesCommon.LOGGER.warn("Found duplicate, noping out");
                        return;
                    }
                }
                list.add(StringTag.valueOf(appId));
                this.setSuccessful();
            } else {
                list.removeIf(appTag -> {
                    if (appTag.asString().equals(appId)) {
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
            UltreonDevicesCommon.LOGGER.info("Installing {} unsuccessful", appId);
        }
    }


    @Override
    public void prepareResponse(CompoundTag tag) {

    }

    @Override
    public void processResponse(CompoundTag tag) {

    }
}
