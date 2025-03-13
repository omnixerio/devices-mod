package dev.ultreon.devices.block;

import com.mojang.serialization.MapCodec;
import dev.ultreon.devices.ModDeviceTypes;
import dev.ultreon.devices.block.entity.LaptopBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LaptopBlock extends ComputerBlock.Colored {
    public static final MapCodec<LaptopBlock> CODEC = simpleCodec(LaptopBlock::new);

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

    public LaptopBlock(DyeColor color) {
        this(Properties.of().mapColor(color).strength(6f).sound(SoundType.METAL), color);
    }

    private LaptopBlock(Properties properties, DyeColor color) {
        super(properties, color, ModDeviceTypes.COMPUTER);
        this.registerDefaultState(this.stateDefinition.any().setValue(TYPE, Type.BASE).setValue(FACING, Direction.NORTH).setValue(OPEN, false));
    }

    private LaptopBlock(Properties properties) {
        super(properties, DyeColor.WHITE, ModDeviceTypes.COMPUTER);
        this.registerDefaultState(this.stateDefinition.any().setValue(TYPE, Type.BASE).setValue(FACING, Direction.NORTH).setValue(OPEN, false));
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
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(TYPE, OPEN);
    }

    @Override
    public boolean isDesktopPC() {
        return false;
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        BlockEntity parameter = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (parameter == null) return drops;
        for (ItemStack drop : drops) {
            if (drop.getItem() instanceof BlockItem blockItem) {
                if (blockItem.getBlock() instanceof LaptopBlock) {
                    Level level = parameter.getLevel();
                    if (level == null) level = builder.getLevel();
                    parameter.saveToItem(drop, level.registryAccess());
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
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }
}
