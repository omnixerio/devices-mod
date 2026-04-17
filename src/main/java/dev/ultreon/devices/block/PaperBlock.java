package dev.ultreon.devices.block;

import com.mojang.serialization.MapCodec;
import dev.ultreon.devices.OmnixerioDevicesCommon;
import dev.ultreon.devices.block.entity.PaperBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("NullableProblems")
public class PaperBlock extends HorizontalDirectionalBlock implements EntityBlock {
    private static final VoxelShape SELECTION_BOUNDS = box(15, 0, 0, 16, 16, 16);

    private static final VoxelShape SELECTION_BOX_NORTH = box(15, 0, 0, 16, 16, 16);
    private static final VoxelShape SELECTION_BOX_SOUTH = box(0, 0, 0, 1, 16, 16);
    private static final VoxelShape SELECTION_BOX_WEST = box(0, 0, 15, 16, 16, 16);
    private static final VoxelShape SELECTION_BOX_EAST = box(0, 0, 0, 16, 16, 1);
    private static final VoxelShape[] SELECTION_BOUNDING_BOX = {SELECTION_BOX_SOUTH, SELECTION_BOX_WEST, SELECTION_BOX_NORTH, SELECTION_BOX_EAST};

    public PaperBlock(Properties pProperties) {
        super(pProperties.setId(ResourceKey.create(Registries.BLOCK, OmnixerioDevicesCommon.id("paper"))).noCollision().instabreak().noOcclusion().noLootTable());

        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @NotNull
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return switch (pState.getValue(FACING)) {
            case NORTH -> SELECTION_BOX_NORTH;
            case SOUTH -> SELECTION_BOX_SOUTH;
            case WEST -> SELECTION_BOX_WEST;
            case EAST -> SELECTION_BOX_EAST;
            default -> throw new IllegalStateException("Unexpected value: " + pState.getValue(FACING));
        };
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState state = super.getStateForPlacement(pContext);
        return state != null ? state.setValue(FACING, pContext.getHorizontalDirection()) : null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof PaperBlockEntity paper) {
                paper.nextRotation();
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pBuilder) {
        return new ArrayList<>();
    }

    // FixMe Port this to Minecraft 26.1
//    @Override
//    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
//        if (!level.isClientSide()) {
//            BlockEntity tileEntity = level.getBlockEntity(pos);
//            if (tileEntity instanceof PaperBlockEntity paper) {
//                ItemStack drop = IPrint.generateItem(paper.getPrint());
//                level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop));
//            }
//        }
//        super.onRemove(state, level, pos, newState, isMoving);
//    }

    @Override
    public boolean triggerEvent(@NotNull BlockState state, Level level, @NotNull BlockPos pos, int id, int param) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(id, param);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    // Todo: Port this to Minecraft 1.18.2
//    public EnumBlockRenderType getRenderType(IBlockState state) {
//        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
//    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PaperBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return simpleCodec(PaperBlock::new);
    }
}
