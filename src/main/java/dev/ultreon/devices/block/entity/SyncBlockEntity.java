package dev.ultreon.devices.block.entity;

import dev.ultreon.devices.util.BlockEntityUtil;
import dev.ultreon.quantum.block.entity.BlockEntity;
import dev.ultreon.quantum.block.entity.BlockEntityType;
import dev.ultreon.quantum.ubo.types.MapType;
import dev.ultreon.quantum.world.World;
import dev.ultreon.quantum.world.vec.BlockVec;
import org.jetbrains.annotations.NotNull;

public abstract class SyncBlockEntity extends BlockEntity {
    protected MapType pipeline = new MapType();

    protected SyncBlockEntity(BlockEntityType<?> type, World world, BlockVec pos) {
        super(type, world, pos);
    }

    public void sync() {
        if (!pipeline.isEmpty()) {
            MapType updateTag = pipeline;
            pipeline = new MapType();
            save(updateTag);
            BlockEntityUtil.sendPipeline(world, pos, pipeline);
            BlockEntityUtil.updateBlock(world, pos);
            return;
        }
        MapType updateTag = saveSyncTag();
        save(updateTag);
        BlockEntityUtil.sendPipeline(world, pos, pipeline);
        BlockEntityUtil.updateBlock(world, pos);
    }

    private void update() {
        this.sync();
        BlockEntityUtil.updateBlock(world, pos);
    }

    @Override
    public void onUpdate(@NotNull MapType data) {
        this.load(data);
        this.update();
    }

    public abstract MapType saveSyncTag();

    public MapType getPipeline() {
        return pipeline;
    }
}
