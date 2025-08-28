package com.ultreon.devices.core.network;

import com.ultreon.devices.block.entity.RouterBlockEntity;
import com.ultreon.devices.debug.DebugLog;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Connection {
    private UUID routerId;
    private BlockPos routerPos;

    private Connection() {

    }

    public Connection(Router router) {
        this.routerId = router.getId();
        this.routerPos = router.getPos();
    }

    public UUID getRouterId() {
        return routerId;
    }

    @Nullable
    public BlockPos getRouterPos() {
        return routerPos;
    }

    public void setRouterPos(@Nullable BlockPos routerPos) {
        this.routerPos = routerPos;
    }

    @Nullable
    public Router getRouter(@NotNull Level level) {
        if (routerPos == null)
            return null;

        BlockEntity blockEntity = level.getBlockEntity(routerPos);
        System.out.println("routerPos = " + routerPos);
        System.out.println("blockEntity = " + blockEntity);
        if (blockEntity instanceof RouterBlockEntity router) {
            if (router.getRouter().getId().equals(routerId)) {
                return router.getRouter();
            } else {
                DebugLog.log("Invalid router ID");
            }
        } else {
            DebugLog.log("Router is not a router");
        }
        return null;
    }

    public boolean isConnected() {
        return routerPos != null;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", routerId.toString());
        if (routerPos != null) {
            tag.put("Pos", NbtUtils.writeBlockPos(routerPos));
        }
        return tag;
    }

    public static Connection fromTag(CompoundTag tag) {
        Connection connection = new Connection();
        connection.routerId = UUID.fromString(tag.getString("id"));
        if (tag.contains("Pos", Tag.TAG_COMPOUND)) {
            connection.routerPos = NbtUtils.readBlockPos(tag.getCompound("Pos"));
        }
        return connection;
    }
}
