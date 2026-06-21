package dev.ultreon.devices.block;

import com.mojang.serialization.MapCodec;
import dev.ultreon.devices.ModDeviceTypes;
import dev.ultreon.devices.block.entity.ComputerBlockEntity;
import dev.ultreon.devices.block.entity.LaptopBlockEntity;
import dev.ultreon.devices.debug.DebugLog;
import dev.ultreon.devices.item.FlashDriveItem;
import dev.ultreon.devices.util.BlockEntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LaptopBlock extends ComputerBlock.Colored {
    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);
    public static final BooleanProperty OPEN = BooleanProperty.create("open");

    private static final VoxelShape SHAPE_OPEN_NORTH = Shapes.or(Block.box(1, 0, 12.5, 15, 11.4, 17), Block.box(1, 0, 1, 15, 1.3, 12.5));
    private static final VoxelShape SHAPE_OPEN_EAST = Shapes.or(Block.box(-1, 0, 1, 3.5, 11.4, 15), Block.box(3.5, 0, 1, 15, 1.3, 15));
    private static final VoxelShape SHAPE_OPEN_SOUTH = Shapes.or(Block.box(1, 0, -1, 15, 11.4, 3.5), Block.box(1, 0, 3.5, 15, 1.3, 15));
    private static final VoxelShape SHAPE_OPEN_WEST = Shapes.or(Block.box(12.5, 0, 1, 17, 11.4, 15), Block.box(1, 0, 1, 12.5, 1.3, 15));
    private static final VoxelShape SHAPE_CLOSED_NORTH = Block.box(1, 0, 1, 15, 2, 13);
    private static final VoxelShape SHAPE_CLOSED_EAST = Block.box(3, 0, 1, 15, 2, 15);
    private static final VoxelShape SHAPE_CLOSED_SOUTH = Block.box(1, 0, 3, 15, 2, 15);
    private static final VoxelShape SHAPE_CLOSED_WEST = Block.box(1, 0, 1, 13, 2, 15);
    private final DyeColor color;

    public LaptopBlock(DyeColor color) {
        super(Properties.of().mapColor(color).strength(6f).sound(SoundType.METAL), color, ModDeviceTypes.COMPUTER);
        registerDefaultState(this.getStateDefinition().any().setValue(TYPE, Type.BASE).setValue(OPEN, false));
        this.color = color;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return pState.getValue(OPEN) ? switch (pState.getValue(FACING)) {
            case NORTH -> SHAPE_OPEN_NORTH;
            case EAST -> SHAPE_OPEN_EAST;
            case SOUTH -> SHAPE_OPEN_SOUTH;
            case WEST -> SHAPE_OPEN_WEST;
            default -> throw new IllegalStateException("Unexpected value: " + pState.getValue(FACING));
        } : switch (pState.getValue(FACING)) {
            case NORTH -> SHAPE_CLOSED_NORTH;
            case EAST -> SHAPE_CLOSED_EAST;
            case SOUTH -> SHAPE_CLOSED_SOUTH;
            case WEST -> SHAPE_CLOSED_WEST;
            default -> throw new IllegalStateException("Unexpected value: " + pState.getValue(FACING));
        };
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof LaptopBlockEntity laptop)) return InteractionResult.FAIL;

        if (player.isCrouching()) {
            if (!level.isClientSide) {
                laptop.openClose(player);
            }
            return InteractionResult.SUCCESS;
        } else if (hit.getDirection() == state.getValue(FACING).getClockWise(Direction.Axis.Y)) {
            return manageFlashDrive(state, level, pos, player, hand, laptop);
        }

        if (laptop.isOpen()) {
            accessComputer(level, laptop);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void accessComputer(Level level, ComputerBlockEntity computer) {
        if (computer instanceof LaptopBlockEntity laptop && laptop.isOpen()) {
            super.accessComputer(level, computer);
        }
    }

    private static @NotNull InteractionResult manageFlashDrive(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, LaptopBlockEntity laptop) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof FlashDriveItem && laptop.canChangeAttachment()) {
            return attachDrive(level, laptop, heldItem);
        }

        if (!laptop.canChangeAttachment())
            return InteractionResult.FAIL;

        ItemStack stack = laptop.getFileSystem().detachDrive();
        if (stack != null) {
            return detachDrive(state, level, pos, laptop, stack);
        }
        return InteractionResult.FAIL;
    }

    private static @NotNull InteractionResult attachDrive(@NotNull Level level, LaptopBlockEntity laptop, ItemStack heldItem) {
        if (laptop.getFileSystem().attachDrive(heldItem.copy())) {
            DebugLog.logTime(level.getGameTime(), "Attached Drive");
            laptop.setAttachmentCooldown(10);
            heldItem.shrink(1);
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return InteractionResult.FAIL;
        }
    }

    private static @NotNull InteractionResult detachDrive(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, LaptopBlockEntity laptop, ItemStack stack) {
        DebugLog.logTime(level.getGameTime(), "Detached Drive");
        laptop.setAttachmentCooldown(10);
        BlockPos summonPos = pos.relative(state.getValue(FACING).getClockWise(Direction.Axis.Y));
        level.addFreshEntity(new ItemEntity(level, summonPos.getX() + 0.5, summonPos.getY(), summonPos.getZ() + 0.5, stack));
        BlockEntityUtil.markBlockForUpdate(level, pos);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public boolean isDesktopPC() {
        return false;
    }

    @Override
    protected void removeTagsForDrop(CompoundTag tileEntityTag) {
        tileEntityTag.remove("open");
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        BlockEntity parameter = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (parameter == null) return drops;
        for (ItemStack drop : drops) {
            if (drop.getItem() instanceof BlockItem blockItem) {
                if (blockItem.getBlock() instanceof LaptopBlock) {
                    parameter.saveToItem(drop, builder.getLevel().registryAccess());
                }
            }
        }

        return drops;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new LaptopBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(TYPE, OPEN);
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return simpleCodec(properties1 -> new LaptopBlock(color));
    }
}
