package dev.ultreon.devices.util;

import dev.ultreon.quantum.block.BlockState;
import dev.ultreon.quantum.network.packets.s2c.S2CBlockEntityUpdatePacket;
import dev.ultreon.quantum.network.packets.s2c.S2CBlockSetPacket;
import dev.ultreon.quantum.ubo.types.MapType;
import dev.ultreon.quantum.world.BlockFlags;
import dev.ultreon.quantum.world.ServerWorld;
import dev.ultreon.quantum.world.World;
import dev.ultreon.quantum.world.vec.BlockVec;
import org.intellij.lang.annotations.MagicConstant;

public class BlockEntityUtil {
    private BlockEntityUtil() {
        throw new AssertionError("Utility class");
    }

    public static void updateBlock(World world, BlockVec pos) {
        if (world.isServerSide()) {
            ServerWorld serverWorld = (ServerWorld) world;

            // Essentially updates the block on the client
            serverWorld.sendAllTracking(pos.x, pos.y, pos.z, new S2CBlockSetPacket(pos, world.get(pos)));
        }
    }

    public static void setBlockState(World world, BlockVec pos, BlockState state,
                                     @MagicConstant(flagsFromClass = BlockFlags.class) int flags) {
        if (world instanceof ServerWorld serverLserverWorld) {
            serverLserverWorld.set(pos, state, flags);
        }
    }

    public static void sendPipeline(World world, BlockVec pos, MapType pipeline) {
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.sendAllTracking(pos.x, pos.y, pos.z, new S2CBlockEntityUpdatePacket(pos, pipeline));
        }
    }
}
