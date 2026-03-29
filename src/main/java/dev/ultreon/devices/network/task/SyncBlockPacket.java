package dev.ultreon.devices.network.task;

import dev.ultreon.devices.block.entity.RouterBlockEntity;
import dev.ultreon.mods.xinexlib.network.Networker;
import dev.ultreon.mods.xinexlib.network.packet.PacketToServer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * @author MrCrayfish
 */
public class SyncBlockPacket implements PacketToServer<SyncBlockPacket> {
    private final BlockPos routerPos;

    public SyncBlockPacket(FriendlyByteBuf buf) {
        this.routerPos = buf.readBlockPos();
    }

    public SyncBlockPacket(BlockPos routerPos) {
        this.routerPos = routerPos;
    }

    @Override
    public void handle(Networker connection, ServerPlayer player) {
        Level level = player.level();
        BlockEntity blockEntity = level.getChunkAt(routerPos).getBlockEntity(routerPos, LevelChunk.EntityCreationType.IMMEDIATE);
        if (blockEntity instanceof RouterBlockEntity router) {
            router.syncDevicesToClient();
        }
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(routerPos);
    }
}
