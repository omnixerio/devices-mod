package dev.ultreon.devices.block.entity;

import dev.ultreon.devices.block.RouterBlock;
import dev.ultreon.devices.block.entity.renderer.RouterRenderer;
import dev.ultreon.devices.core.network.Router;
import dev.ultreon.devices.init.DeviceBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/// # Router (Block Entity)
/// The block entity for the router block.
///
/// @author [MrCrayfish](https://github.com/MrCrayfish), [Qubix](https://github.com/Qubilux)
/// @see Router
/// @see RouterBlock
/// @see RouterRenderer
@SuppressWarnings("unused")
public class RouterBlockEntity extends DeviceBlockEntity.Colored {
    private Router router;

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
            getRouter().tick(this, level);
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
    public void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
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
//    @PlatformOnly("neoforge")
//    @Environment(EnvType.CLIENT)
//    @ExpectPlatform
//    public AABB getRenderBoundingBox() {
//        throw new AssertionError();
//    }
}
