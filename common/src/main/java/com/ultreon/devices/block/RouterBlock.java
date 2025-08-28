package com.ultreon.devices.block;

import com.ultreon.devices.ModDeviceTypes;
import com.ultreon.devices.block.entity.RouterBlockEntity;
import com.ultreon.devices.network.PacketHandler;
import com.ultreon.devices.network.task.SyncBlockPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author MrCrayfish
 */
public class RouterBlock extends DeviceBlock.Colored {
    public static final BooleanProperty VERTICAL = BooleanProperty.create("vertical");

    // Todo - do rotations for voxel shapes properly.
    private static final VoxelShape[] BODY_BOUNDING_BOX = {
            box(4, 0, 2, 12, 2, 14),
            box(4, 0, 2, 12, 2, 14),
            box(4, 0, 2, 12, 2, 14),
            box(4, 0, 2, 12, 2, 14)
    };
    private static final VoxelShape[] BODY_VERTICAL_BOUNDING_BOX = {
            box(14, 1, 2, 16, 9, 14),
            box(14, 1, 2, 16, 9, 14),
            box(14, 1, 2, 16, 9, 14),
            box(14, 1, 2, 16, 9, 14)
    };
    private static final VoxelShape[] SELECTION_BOUNDING_BOX = {
            box(3, 0, 1, 13, 3, 15),
            box(3, 0, 1, 13, 3, 15),
            box(3, 0, 1, 13, 3, 15),
            box(3, 0, 1, 13, 3, 15)
    };
    private static final VoxelShape[] SELECTION_VERTICAL_BOUNDING_BOX = {
            box(13, 0, 1, 16, 10, 15),
            box(13, 0, 1, 16, 10, 15),
            box(13, 0, 1, 16, 10, 15),
            box(13, 0, 1, 16, 10, 15)
    };

    public RouterBlock(DyeColor color) {
        super(Properties.of().mapColor(color).strength(6f).sound(SoundType.METAL), color, ModDeviceTypes.ROUTER);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(VERTICAL, false));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return switch (pState.getValue(FACING)) {
            case NORTH -> pState.getValue(VERTICAL) ? BODY_VERTICAL_BOUNDING_BOX[0] : BODY_BOUNDING_BOX[0];
            case EAST -> pState.getValue(VERTICAL) ? BODY_VERTICAL_BOUNDING_BOX[1] : BODY_BOUNDING_BOX[1];
            case SOUTH -> pState.getValue(VERTICAL) ? BODY_VERTICAL_BOUNDING_BOX[2] : BODY_BOUNDING_BOX[2];
            case WEST -> pState.getValue(VERTICAL) ? BODY_VERTICAL_BOUNDING_BOX[3] : BODY_BOUNDING_BOX[3];
            default -> BODY_BOUNDING_BOX[0];
        };
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide && player.isCreative()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof RouterBlockEntity router) {
                router.setDebug(true);
                if (router.isDebug()) {
                    PacketHandler.INSTANCE.sendToServer(new SyncBlockPacket(pos));
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public @org.jetbrains.annotations.Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext pContext) {
        BlockState state = super.getStateForPlacement(pContext);
        return state != null ? state.setValue(FACING, pContext.getHorizontalDirection().getOpposite()).setValue(VERTICAL, pContext.getClickLocation().y - pContext.getClickLocation().y > 0.5) : null;
    }

//    @Override
//    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
//        IBlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
//        return state.withProperty(VERTICAL, facing.getHorizontalIndex() != -1);
//    }

//    @Override
//    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
//        return side != EnumFacing.DOWN;
//    }

    @NotNull
    @Override
    @Contract("_, _ -> new")
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new RouterBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(VERTICAL);
    }
}
