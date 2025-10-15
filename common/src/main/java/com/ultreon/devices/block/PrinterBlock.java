package com.ultreon.devices.block;

import com.ultreon.devices.ModDeviceTypes;
import com.ultreon.devices.block.entity.PrinterBlockEntity;
import com.ultreon.devices.util.Colored;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author MrCrayfish
 */
public class PrinterBlock extends DeviceBlock.Colored implements Colored {
    private static final VoxelShape SHAPE_NORTH = Shapes.or(
            box(2, 0, 7, 14, 5, 12),
            box(3.5, 0.1, 1, 12.5, 1.1, 7),
            box(12, 0, 12, 15, 5, 14),
            box(12, 0, 5, 15, 3, 7),
            box(1, 0, 5, 4, 3, 7),
            box(1, 0, 12, 4, 5, 14),
            box(1.1, 0, 7, 14.9, 5, 12),
            box(4, 0, 12, 12, 3, 14),
            box(3.5, 0.1, 1, 12.5, 1.1, 7.5),
            box(1, 3, 5, 15, 5, 7),
            box(4, 3, 12, 12, 9.3, 16));
    private static final VoxelShape SHAPE_EAST = Shapes.or(
            box(4, 0, 2, 9, 5, 14),
            box(9, 0.1, 3.5, 15, 1.1, 12.5),
            box(2, 0, 12, 4, 5, 15),
            box(9, 0, 12, 11, 3, 15),
            box(9, 0, 1, 11, 3, 4),
            box(2, 0, 1, 4, 5, 4),
            box(4, 0, 1.1, 9, 5, 14.9),
            box(2, 0, 4, 4, 3, 12),
            box(8.5, 0.1, 3.5, 15, 1.1, 12.5),
            box(9, 3, 1, 11, 5, 15),
            box(0, 3, 4, 4, 9.3, 12));
    private static final VoxelShape SHAPE_SOUTH = Shapes.or(
            box(2, 0, 4, 14, 5, 9),
            box(3.5, 0.1, 9, 12.5, 1.1, 15),
            box(1, 0, 2, 4, 5, 4),
            box(1, 0, 9, 4, 3, 11),
            box(12, 0, 9, 15, 3, 11),
            box(12, 0, 2, 15, 5, 4),
            box(1.1, 0, 4, 14.9, 5, 9),
            box(4, 0, 2, 12, 3, 4),
            box(3.5, 0.1, 8.5, 12.5, 1.1, 15),
            box(1, 3, 9, 15, 5, 11),
            box(4, 3, 0, 12, 9.3, 3.4));
    private static final VoxelShape SHAPE_WEST = Shapes.or(
            box(7, 0, 2, 12, 5, 14),
            box(1, 0.1, 3.5, 7, 1.1, 12.5),
            box(12, 0, 1, 14, 5, 4),
            box(5, 0, 1, 7, 3, 4),
            box(5, 0, 12, 7, 3, 15),
            box(12, 0, 12, 14, 5, 15),
            box(7, 0, 1.1, 12, 5, 14.9),
            box(12, 0, 4, 14, 3, 12),
            box(1, 0.1, 3.5, 7.5, 1.1, 12.5),
            box(5, 3, 1, 7, 5, 15),
            box(12, 3, 4, 16, 9.3, 12));

    public PrinterBlock(DyeColor color) {
        super(Properties.of().mapColor(color).strength(6f).sound(SoundType.METAL), color, ModDeviceTypes.PRINTER);
        this.registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return switch (pState.getValue(FACING)) {
            case NORTH -> SHAPE_NORTH;
            case EAST -> SHAPE_EAST;
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            default -> throw new IllegalStateException("Unexpected value: " + pState.getValue(FACING));
        };
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) {
            if (player.isCrouching()) {
                return InteractionResult.SUCCESS;
            } else {
                return super.use(state, level, pos, player, hand, hit);
            }
        }

        ItemStack heldItem = player.getItemInHand(hand);
        BlockEntity tileEntity = level.getChunkAt(pos).getBlockEntity(pos, LevelChunk.EntityCreationType.IMMEDIATE);
        if (tileEntity instanceof PrinterBlockEntity printer) {
            return printer.addPaper(heldItem, player.isCrouching()) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        }
        return InteractionResult.PASS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new PrinterBlockEntity(pos, state);
    }
}
