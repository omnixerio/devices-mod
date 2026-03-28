package com.ultreon.devices.core.network;

import com.ultreon.devices.block.entity.RouterBlockEntity;
import com.ultreon.devices.debug.DebugLog;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
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
            CompoundTag posTag = new CompoundTag();
            posTag.putInt("x", routerPos.getX());
            posTag.putInt("y", routerPos.getY());
            posTag.putInt("z", routerPos.getZ());
            tag.put("Pos", posTag);
        }
        return tag;
    }

    public static Connection fromTag(CompoundTag tag) {
        Connection connection = new Connection();
        connection.routerId = UUID.fromString(tag.getString("id").orElseThrow());
        if (tag.contains("Pos")) {
            CompoundTag posTag = tag.getCompound("Pos").orElseThrow();
            connection.routerPos = new BlockPos(posTag.getInt("x").orElseThrow(), posTag.getInt("y").orElseThrow(), posTag.getInt("z").orElseThrow());
        }
        return connection;
    }

    public void save(@NotNull ValueOutput tag) {
        tag.putString("id", routerId.toString());
        if (routerPos != null) {
            ValueOutput pos = tag.child("Pos");
            pos.putInt("x", routerPos.getX());
            pos.putInt("y", routerPos.getY());
            pos.putInt("z", routerPos.getZ());
        }
    }

    public static Connection load(@NotNull ValueInput tag) {
        Connection connection = new Connection();
        connection.routerId = UUID.fromString(tag.getString("id").orElseThrow());
        Optional<ValueInput> pos = tag.child("Pos");
        if (pos.isPresent()) {
            ValueInput valueInput = pos.get();
            int x = valueInput.getInt("x").orElseThrow();
            int y = valueInput.getInt("y").orElseThrow();
            int z = valueInput.getInt("z").orElseThrow();
            connection.routerPos = new BlockPos(x, y, z);
        }
        return connection;
    }
}
