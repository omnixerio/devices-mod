package dev.ultreon.devices.block;

import dev.ultreon.devices.IDeviceType;
import dev.ultreon.devices.ModDeviceTypes;
import dev.ultreon.devices.block.entity.DeviceBlockEntity;
import dev.ultreon.devices.util.BlockEntityUtil;
import dev.ultreon.devices.util.Colorable;
import dev.ultreon.quantum.block.Block;
import dev.ultreon.quantum.block.Block.Properties;
import dev.ultreon.quantum.block.BlockState;
import dev.ultreon.quantum.block.Blocks;
import dev.ultreon.quantum.block.EntityBlock;
import dev.ultreon.quantum.block.entity.BlockEntity;
import dev.ultreon.quantum.block.property.StateProperties;
import dev.ultreon.quantum.item.UseItemContext;
import dev.ultreon.quantum.world.World;
import dev.ultreon.quantum.world.vec.BlockVec;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.DyeColor;
import dev.ultreon.quantum.item.ItemStack;
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
public abstract class DeviceBlock extends Block implements EntityBlock, IDeviceType {
    private final ModDeviceTypes deviceType;

    public DeviceBlock(Properties properties, ModDeviceTypes deviceType) {
        super(properties.hardness(0.5f));
        this.deviceType = deviceType;
    }

    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return Shapes.empty();
    }

    @Override
    public @NotNull BlockState onPlacedBy(@NotNull BlockState blockMeta, @NotNull BlockVec at, @NotNull UseItemContext context) {
        BlockState state = super.onPlacedBy(blockMeta, at, context);

        BlockEntity blockEntity = context.world().getBlockEntity(at);
        if (blockEntity instanceof DeviceBlockEntity deviceBlockEntity) {
            if (stack.hasCustomHoverName()) {
                deviceBlockEntity.setCustomName(stack.getHoverName().getString());
            }
        }

        return state.with(StateProperties.FACING, Objects.requireNonNull(context.result(), "Player in block placement context is null.").getRay().getDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(@NotNull World level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
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
