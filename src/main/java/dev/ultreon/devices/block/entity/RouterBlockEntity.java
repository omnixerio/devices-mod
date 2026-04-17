package dev.ultreon.devices.block.entity;

import dev.ultreon.devices.core.network.Router;
import dev.ultreon.devices.init.DeviceBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;

@SuppressWarnings("unused")
public class RouterBlockEntity extends DeviceBlockEntity.Colored {
    private Router router;

    private int debugTimer;

    public RouterBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(DeviceBlockEntities.ROUTER, pWorldPosition, pBlockState);
    }

    public Router getRouter() {
        if (router == null) {
            router = new Router(worldPosition);
            setChanged();
        }
        return router;
    }

    public void tick() {
        assert level != null;
        if (!level.isClientSide()) {
            getRouter().tick(level);
        } else if (debugTimer > 0) {
            debugTimer--;
        }
    }

    public boolean isDebug() {
        return debugTimer > 0;
    }

    public void setDebug(boolean debug) {
        if (debug) {
            debugTimer = 1200;
        } else {
            debugTimer = 0;
        }
    }

    public String getDeviceName() {
        return "Router";
    }

    @Override
    public void saveAdditional(@NonNull ValueOutput tag) {
        super.saveAdditional(tag);
    }

    public void syncDevicesToClient() {
        pipeline.put("router", getRouter().toTag(true));
        sync();
    }

    // Todo - Maybe implement this whenever possible?
//    @Override
//    public double getMaxRenderDistanceSqr() {
//        return 16384;
//    }
//
//    @PlatformOnly("forge")
//    @Environment(EnvType.CLIENT)
//    @ExpectPlatform
//    public AABB getRenderBoundingBox() {
//        throw new AssertionError();
//    }
}
