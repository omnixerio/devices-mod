package com.ultreon.devices.core.print.task;

import com.ultreon.devices.api.print.IPrint;
import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.block.entity.NetworkDeviceBlockEntity;
import com.ultreon.devices.block.entity.PrinterBlockEntity;
import com.ultreon.devices.core.network.NetworkDevice;
import com.ultreon.devices.core.network.Router;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.TagValueInput;

import java.util.Optional;
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
        tag.putLongArray("printerId", new long[]{printerId.getMostSignificantBits(), printerId.getLeastSignificantBits()});
        tag.put("print", IPrint.save(print));
    }

    @Override
    public void processRequest(CompoundTag tag, Level level, Player player) {
        BlockEntity tileEntity = level.getChunkAt(BlockPos.of(tag.getLongOr("devicePos", 0))).getBlockEntity(BlockPos.of(tag.getLongOr("devicePos", 0)), LevelChunk.EntityCreationType.IMMEDIATE);
        if (tileEntity instanceof NetworkDeviceBlockEntity device) {
            Router router = device.getRouter();
            if (router != null) {
                Optional<long[]> printerId1 = tag.getLongArray("printerId");
                NetworkDeviceBlockEntity printer = router.getDevice(level, printerId1.map(l -> new UUID(l[0], l[1])).orElseThrow());
                if (printer instanceof PrinterBlockEntity) {
                    IPrint print = IPrint.load(TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), tag.getCompoundOrEmpty("print")));
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
