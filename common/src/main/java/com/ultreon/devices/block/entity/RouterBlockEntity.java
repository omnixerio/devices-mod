package com.ultreon.devices.block.entity;

import com.ultreon.devices.core.network.Router;
import com.ultreon.devices.init.DeviceBlockEntities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("unused")
public class RouterBlockEntity extends DeviceBlockEntity.Colored {
    private Router router;

    @Environment(EnvType.CLIENT)
    private int debugTimer;

    public RouterBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(DeviceBlockEntities.ROUTER.get(), pWorldPosition, pBlockState);
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
        if (!level.isClientSide) {
            getRouter().tick(level);
        } else if (debugTimer > 0) {
            debugTimer--;
        }
    }

    @Environment(EnvType.CLIENT)
    public boolean isDebug() {
        return debugTimer > 0;
    }

    @Environment(EnvType.CLIENT)
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
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (tag.contains("router", Tag.TAG_COMPOUND)) {
            router = Router.fromTag(worldPosition, tag.getCompound("router"));
        }
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
