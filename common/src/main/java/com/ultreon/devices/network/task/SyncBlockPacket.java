package com.ultreon.devices.network.task;

import com.ultreon.devices.block.entity.RouterBlockEntity;
import com.ultreon.devices.network.Packet;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author MrCrayfish
 */
public class SyncBlockPacket extends Packet<SyncBlockPacket> {
    private final BlockPos routerPos;

    public SyncBlockPacket(FriendlyByteBuf buf) {
        this.routerPos = buf.readBlockPos();
    }

    public SyncBlockPacket(BlockPos routerPos) {
        this.routerPos = routerPos;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(routerPos);
    }

    @Override
    public boolean onMessage(Supplier<NetworkManager.PacketContext> ctx) {
        Level level = Objects.requireNonNull(ctx.get().getPlayer()).level();
        BlockEntity blockEntity = level.getChunkAt(routerPos).getBlockEntity(routerPos, LevelChunk.EntityCreationType.IMMEDIATE);
        if (blockEntity instanceof RouterBlockEntity router) {
            router.syncDevicesToClient();
        }
        return true;
    }
}
