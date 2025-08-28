package com.ultreon.devices.block;

import com.ultreon.devices.block.entity.MacMaxXBlockEntity;
import com.ultreon.devices.init.DeviceBlocks;
import dev.architectury.platform.Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Qboi123
 */
@SuppressWarnings("deprecation")
public class MacMaxXBlock extends ComputerBlock {
    private static final VoxelShape SHAPE_NORTH = Shapes.or(
            Block.box(-16, 31, 5, 32, 32, 7),
            Block.box(-15, 4, 5.5, 31, 32, 7),
            Block.box(-16, 5, 5, -15, 32, 7),
            Block.box(-16, 3, 5, 32, 7, 7),
            Block.box(31, 5, 5, 32, 32, 7),
            Block.box(-6, 0.5, 6.5, -2, 19, 9),
            Block.box(20, 0.5, 6.5, 24, 19, 9),
            Block.box(0, 13, 6.5, 18, 27, 8),
            Block.box(19, 0, 6.5, 25, 0.5, 9),
            Block.box(-7, 0, 6.5, -1, 0.5, 9)
    );
    private static final VoxelShape SHAPE_EAST = Shapes.or(
            Block.box(9, 31, -16, 11, 32, 32),
            Block.box(9, 4, -15, 10.5, 32, 31),
            Block.box(9, 5, -16, 11, 32, -15),
            Block.box(9, 3, -16, 11, 7, 32),
            Block.box(9, 5, 31, 11, 32, 32),
            Block.box(7, 0.5, -6, 9.5, 19, -2),
            Block.box(7, 0.5, 20, 9.5, 19, 24),
            Block.box(8, 13, 0, 9.5, 27, 18),
            Block.box(7, 0, 19, 9.5, 0.5, 25),
            Block.box(7, 0, -7, 9.5, 0.5, -1)
    );
    private static final VoxelShape SHAPE_SOUTH = Shapes.or(
            Block.box(-16, 31, 9, 32, 32, 11),
            Block.box(-15, 4, 9, 31, 32, 10.5),
            Block.box(31, 5, 9, 32, 32, 11),
            Block.box(-16, 3, 9, 32, 7, 11),
            Block.box(-16, 5, 9, -15, 32, 11),
            Block.box(18, 0.5, 7, 22, 19, 9.5),
            Block.box(-8, 0.5, 7, -4, 19, 9.5),
            Block.box(-2, 13, 8, 16, 27, 9.5),
            Block.box(-9, 0, 7, -3, 0.5, 9.5),
            Block.box(17, 0, 7, 23, 0.5, 9.5)
    );
    private static final VoxelShape SHAPE_WEST = Shapes.or(
            Block.box(5, 31, -16, 7, 32, 32),
            Block.box(5.5, 4, -15, 7, 32, 31),
            Block.box(5, 5, 31, 7, 32, 32),
            Block.box(5, 3, -16, 7, 7, 32),
            Block.box(5, 5, -16, 7, 32, -15),
            Block.box(6.5, 0.5, 18, 9, 19, 22),
            Block.box(6.5, 0.5, -8, 9, 19, -4),
            Block.box(6.5, 13, -2, 8, 27, 16),
            Block.box(6.5, 0, -9, 9, 0.5, -3),
            Block.box(6.5, 0, 17, 9, 0.5, 23)
    );

    public MacMaxXBlock() {
        super(BlockBehaviour.Properties.of().mapColor(DyeColor.WHITE).strength(6f).sound(SoundType.METAL).noOcclusion().dynamicShape());
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return switch (pState.getValue(FACING)) {
            case NORTH -> SHAPE_NORTH;
            case EAST -> SHAPE_EAST;
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            default -> throw new IllegalStateException("Unexpected value: " + pState.getValue(FACING));
        };
    }

    @Override
    public float getShadeBrightness(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return 1.0f;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return canPlace(context.getLevel(), context.getClickedPos(), context.getHorizontalDirection(), context.getHand(), context.getItemInHand()) ? super.getStateForPlacement(context) : null;
    }

    public boolean canPlace(Level level, BlockPos pos, Direction face, InteractionHand hand, ItemStack itemInHand) {
        hasBlock(level, pos.above(), hand, itemInHand, face);
        switch (face) {
            case NORTH -> {
                if (hasBlock(level, pos.above().west(), hand, itemInHand, face)) return false;
                if (hasBlock(level, pos.above().east(), hand, itemInHand, face)) return false;
                if (hasBlock(level, pos.above(), hand, itemInHand, face)) return false;
                if (hasBlock(level, pos.west(), hand, itemInHand, face)) return false;
                if (hasBlock(level, pos.east(), hand, itemInHand, face)) return false;
            }
            case SOUTH -> {
                if (hasBlock(level, pos.above().east(), hand, itemInHand, face)) return false;
                if (hasBlock(level, pos.above().west(), hand, itemInHand, face)) return false;
                if (hasBlock(level, pos.above(), hand, itemInHand, face)) return false;
                if (hasBlock(level, pos.east(), hand, itemInHand, face)) return false;
                if (hasBlock(level, pos.west(), hand, itemInHand, face)) return false;
            }
            case WEST -> {
                if (hasBlock(level, pos.above().north(), hand, itemInHand, face)) return false;
                if (hasBlock(level, pos.above().south(), hand, itemInHand, face)) return false;
                if (hasBlock(level, pos.above(), hand, itemInHand, face)) return false;
                if (hasBlock(level, pos.north(), hand, itemInHand, face)) return false;
                if (hasBlock(level, pos.south(), hand, itemInHand, face)) return false;
            }
            case EAST -> {
                if (hasBlock(level, pos.above().south(), hand, itemInHand, face)) return false;
                if (hasBlock(level, pos.above().north(), hand, itemInHand, face)) return false;
                if (hasBlock(level, pos.above(), hand, itemInHand, face)) return false;
                if (hasBlock(level, pos.south(), hand, itemInHand, face)) return false;
                if (hasBlock(level, pos.north(), hand, itemInHand, face)) return false;
            }
            default -> throw new IllegalStateException("Unexpected value: " + face);
        }
        return true;
    }

    private boolean hasBlock(Level level, BlockPos pos, InteractionHand hand, ItemStack itemInHand, Direction face) {
        return !(level.getBlockState(pos).isAir() || level.getBlockState(pos).canBeReplaced(new FakeBlockPlaceContext(level, hand, itemInHand, new BlockHitResult(Vec3.atCenterOf(pos), face, pos, false))));
    }

    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, BlockState state, LivingEntity placer, @NotNull ItemStack stack) {
        if (state.isAir()) return;
        BlockState partState = DeviceBlocks.MAC_MAX_X_PART.get().defaultBlockState();
        partState = partState.setValue(FACING, state.getValue(FACING));
        level.setBlock(pos.above(), partState.setValue(MacMaxXBlockPart.PART, MacMaxXBlockPart.Part.T), 3);
        switch (state.getValue(FACING)) {
            case NORTH -> {
                level.setBlock(pos.above().west(), partState.setValue(MacMaxXBlockPart.PART, MacMaxXBlockPart.Part.TL), 3);
                level.setBlock(pos.above().east(), partState.setValue(MacMaxXBlockPart.PART, MacMaxXBlockPart.Part.TR), 3);
                level.setBlock(pos.west(), partState.setValue(MacMaxXBlockPart.PART, MacMaxXBlockPart.Part.BL), 3);
                level.setBlock(pos.east(), partState.setValue(MacMaxXBlockPart.PART, MacMaxXBlockPart.Part.BR), 3);
            } 
            case SOUTH -> {
                level.setBlock(pos.above().east(), partState.setValue(MacMaxXBlockPart.PART, MacMaxXBlockPart.Part.TL), 3);
                level.setBlock(pos.above().west(), partState.setValue(MacMaxXBlockPart.PART, MacMaxXBlockPart.Part.TR), 3);
                level.setBlock(pos.east(), partState.setValue(MacMaxXBlockPart.PART, MacMaxXBlockPart.Part.BL), 3);
                level.setBlock(pos.west(), partState.setValue(MacMaxXBlockPart.PART, MacMaxXBlockPart.Part.BR), 3);
            }
            case WEST -> {
                level.setBlock(pos.above().north(), partState.setValue(MacMaxXBlockPart.PART, MacMaxXBlockPart.Part.TL), 3);
                level.setBlock(pos.above().south(), partState.setValue(MacMaxXBlockPart.PART, MacMaxXBlockPart.Part.TR), 3);
                level.setBlock(pos.north(), partState.setValue(MacMaxXBlockPart.PART, MacMaxXBlockPart.Part.BL), 3);
                level.setBlock(pos.south(), partState.setValue(MacMaxXBlockPart.PART, MacMaxXBlockPart.Part.BR), 3);
            }
            case EAST -> {
                level.setBlock(pos.above().south(), partState.setValue(MacMaxXBlockPart.PART, MacMaxXBlockPart.Part.TL), 3);
                level.setBlock(pos.above().north(), partState.setValue(MacMaxXBlockPart.PART, MacMaxXBlockPart.Part.TR), 3);
                level.setBlock(pos.south(), partState.setValue(MacMaxXBlockPart.PART, MacMaxXBlockPart.Part.BL), 3);
                level.setBlock(pos.north(), partState.setValue(MacMaxXBlockPart.PART, MacMaxXBlockPart.Part.BR), 3);
            }
            default -> throw new IllegalStateException("Unexpected value: " + state.getValue(FACING));
        }
    }

    @Override
    public void playerWillDestroy(@NotNull Level level, @NotNull BlockPos pos, BlockState state, @NotNull Player player) {
        switch (state.getValue(FACING)) {
            case NORTH -> {
                level.setBlock(pos.above().west(), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(pos.above().east(), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(pos.west(), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(pos.east(), Blocks.AIR.defaultBlockState(), 3);
            }
            case SOUTH -> {
                level.setBlock(pos.above().east(), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(pos.above().west(), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(pos.east(), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(pos.west(), Blocks.AIR.defaultBlockState(), 3);
            }
            case WEST -> {
                level.setBlock(pos.above().north(), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(pos.above().south(), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(pos.north(), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(pos.south(), Blocks.AIR.defaultBlockState(), 3);
            }
            case EAST -> {
                level.setBlock(pos.above().south(), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(pos.above().north(), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(pos.south(), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(pos.north(), Blocks.AIR.defaultBlockState(), 3);
            }
            default -> throw new IllegalStateException("Unexpected value: " + state.getValue(FACING));
        }
    }

    @Override
    public boolean isDesktopPC() {
        return true;
    }

    @Override
    public MutableComponent getName() {
        MutableComponent normalName = Component.translatable("block.devices.mac_max_x");
        if (Platform.isModLoaded("emojiful")) {
            return Component.translatable("block.devices.mac_max_x_emoji");
        }
        return normalName;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new MacMaxXBlockEntity(pos, state);
    }
}
