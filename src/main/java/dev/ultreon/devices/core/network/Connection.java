package dev.ultreon.devices.core.network;

import dev.ultreon.devices.block.entity.RouterBlockEntity;
import dev.ultreon.devices.debug.DebugLog;
import net.minecraft.core.BlockVec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Connection {
    private UUID routerId;
    private BlockVec routerVec;

    private Connection() {

    }

    public Connection(Router router) {
        this.routerId = router.getId();
        this.routerVec = router.getVec();
    }

    public UUID getRouterId() {
        return routerId;
    }

    @Nullable
    public BlockVec getRouterVec() {
        return routerVec;
    }

    public void setRouterVec(@Nullable BlockVec routerVec) {
        this.routerVec = routerVec;
    }

    @Nullable
    public Router getRouter(@NotNull World world) {
        if (routerVec == null)
            return null;

        BlockEntity blockEntity = world.getBlockEntity(routerVec);
        System.out.println("routerVec = " + routerVec);
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
        return routerVec != null;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", routerId.toString());
        if (routerVec != null) {
            tag.put("Vec", NbtUtils.writeBlockVec(routerVec));
        }
        return tag;
    }

    public static Connection fromTag(CompoundTag tag) {
        Connection connection = new Connection();
        connection.routerId = UUID.fromString(tag.getString("id"));
        if (tag.contains("Vec", Tag.TAG_COMPOUND)) {
            connection.routerVec = NbtUtils.readBlockVec(tag.getCompound("Vec"));
        }
        return connection;
    }
}
