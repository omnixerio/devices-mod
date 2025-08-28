package dev.ultreon.devices.core.print.task;

import dev.ultreon.devices.api.print.IPrint;
import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.block.entity.NetworkDeviceBlockEntity;
import dev.ultreon.devices.block.entity.PrinterBlockEntity;
import dev.ultreon.devices.core.network.NetworkDevice;
import dev.ultreon.devices.core.network.Router;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.UUID;

/**
 * @author MrCrayfish
 */
public class TaskPrint extends Task {
    private BlockPos devicePos;
    private UUID printerId;
    private IPrint print;
    private String reason = null;

    public TaskPrint() {
        super("print");
    }

    public TaskPrint(BlockPos devicePos, NetworkDevice printer, IPrint print) {
        this();
        this.devicePos = devicePos;
        this.printerId = printer.getId();
        this.print = print;
    }

    @Override
    public void prepareRequest(CompoundTag tag) {
        tag.putLong("devicePos", devicePos.asLong());
        tag.putUUID("printerId", printerId);
        tag.put("print", IPrint.save(print));
    }

    @Override
    public void processRequest(CompoundTag tag, Level level, Player player) {
        BlockEntity tileEntity = level.getChunkAt(BlockPos.of(tag.getLong("devicePos"))).getBlockEntity(BlockPos.of(tag.getLong("devicePos")), LevelChunk.EntityCreationType.IMMEDIATE);
        if (tileEntity instanceof NetworkDeviceBlockEntity device) {
            Router router = device.getRouter();
            if (router != null) {
                NetworkDeviceBlockEntity printer = router.getDevice(level, tag.getUUID("printerId"));
                if (printer instanceof PrinterBlockEntity) {
                    IPrint print = IPrint.load(tag.getCompound("print"));
                    ((PrinterBlockEntity) printer).addToQueue(print);
                    this.setSuccessful();
                } else {
                    this.reason = "Network device is not a printer";
                }
            } else {
                this.reason = "Not connected to router";
            }
        } else {
            this.reason = "No network driver found";
        }
    }

    @Override
    public void prepareResponse(CompoundTag tag) {
        if (this.reason != null) {
            tag.putString("reason", this.reason);
        }
    }

    @Override
    public void processResponse(CompoundTag tag) {

    }
}
