package com.ultreon.devices.block;

import com.ultreon.devices.IDeviceType;
import com.ultreon.devices.ModDeviceTypes;
import com.ultreon.devices.block.entity.DeviceBlockEntity;
import com.ultreon.devices.util.BlockEntityUtil;
import com.ultreon.devices.util.Colorable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings("deprecation")
public abstract class DeviceBlock extends HorizontalDirectionalBlock implements EntityBlock, IDeviceType {
    private final ModDeviceTypes deviceType;

    public DeviceBlock(Properties properties, ModDeviceTypes deviceType) {
        super(properties.strength(0.5f));
        this.deviceType = deviceType;
    }

//    @Override
//    public RenderShape getRenderShape(BlockState state) {
//        return RenderShape.INVISIBLE;
//    }

    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return Shapes.empty();
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext pContext) {
        BlockState state = super.getStateForPlacement(pContext);
        return state != null ? state.setValue(FACING, Objects.requireNonNull(pContext.getPlayer(), "Player in block placement context is null.").getDirection().getOpposite()) : null;
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof DeviceBlockEntity deviceBlockEntity) {
            if (stack.hasCustomHoverName()) {
                deviceBlockEntity.setCustomName(stack.getHoverName().getString());
            }
        }
    }


    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof DeviceBlockEntity device) {
                CompoundTag blockEntityTag = new CompoundTag();
                blockEntity.saveWithoutMetadata();
                blockEntityTag.remove("id");

                removeTagsForDrop(blockEntityTag);

                CompoundTag tag = new CompoundTag();
                tag.put("BlockEntityTag", blockEntityTag);

                ItemStack drop;
                if (blockEntity instanceof Colorable) {
                    drop = new ItemStack(this, 1);
                } else {
                    drop = new ItemStack(this);
                }
                drop.setTag(tag);

                if (device.hasCustomName()) {
                    drop.setHoverName(Component.literal(device.getCustomName()));
                }

                level.addFreshEntity(new ItemEntity((Level) level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop));

                level.removeBlock(pos, false);
                return;
            }
        }
        super.destroy(level, pos, state);
    }

    protected void removeTagsForDrop(CompoundTag blockEntityTag) {

    }

    @Nullable
    @Override
    public abstract BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state);

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return BlockEntityUtil.getTicker();
    }

    @Override
    public boolean triggerEvent(@NotNull BlockState state, Level level, @NotNull BlockPos pos, int id, int param) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(id, param);
    }

    @Override
    public ModDeviceTypes getDeviceType() {
        return deviceType;
    }

    public static abstract class Colored extends DeviceBlock implements ColoredBlock {
        private final DyeColor color;

        protected Colored(Properties properties, DyeColor color, ModDeviceTypes deviceType) {
            super(properties, deviceType);
            this.color = color;
        }

        @Override
        public DyeColor getColor() {
            return color;
        }

        @Override
        public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
            super.setPlacedBy(level, pos, state, placer, stack);
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof Colorable colored) {
                colored.setColor(color);
            }
        }

        // Todo - Implement onDestroyedByPlayer if colored, and needed to implement it. Needs to check if it works without it.


        @Override
        protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> pBuilder) {
            super.createBlockStateDefinition(pBuilder);
            pBuilder.add(FACING);
        }
    }
}
