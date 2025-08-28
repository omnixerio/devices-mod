package dev.ultreon.devices.block;

import com.mojang.serialization.MapCodec;
import dev.ultreon.devices.ModDeviceTypes;
import dev.ultreon.devices.block.entity.OfficeChairBlockEntity;
import dev.ultreon.devices.debug.DebugLog;
import dev.ultreon.devices.entity.Seat;
import dev.ultreon.devices.util.SeatUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OfficeChairBlock extends DeviceBlock.Colored {
    public static final MapCodec<OfficeChairBlock> CODEC = simpleCodec(OfficeChairBlock::new);

    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);

    private static final VoxelShape EMPTY_BOX = Shapes.box(0, 0, 0, 0, 0, 0);
    private static final VoxelShape SELECTION_BOX = Shapes.box(0.0625f, 0, 0.0625f, 0.9375f, /*1.6875f*/0.625f, 0.9375f);
    private static final VoxelShape SEAT_BOUNDING_BOX = Shapes.box(0.0625f, 0, 0.0625f, 0.9375f, 0.625f, 0.9375f);

    public OfficeChairBlock(DyeColor color) {
        super(BlockBehaviour.Properties.of().mapColor(color), color, ModDeviceTypes.SEAT);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(TYPE, Type.LEGS));
    }

    private OfficeChairBlock(Properties properties) {
        super(properties, DyeColor.WHITE, ModDeviceTypes.SEAT);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(TYPE, Type.LEGS));
    }

//    @Override
//    public String getDescriptionId() {
//        return Util.makeDescriptionId("block", new ResourceLocation("devices", "office_chair"));
//    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader reader, @NotNull BlockPos pos) {
        return false || true;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return SELECTION_BOX;
    }

    @Override
    public @NotNull VoxelShape getVisualShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SEAT_BOUNDING_BOX;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        if (context instanceof EntityCollisionContext entityCollisionContext) {
            if (entityCollisionContext.getEntity() != null && entityCollisionContext.getEntity().getVehicle() instanceof Seat) {
                return EMPTY_BOX;
            }
        }
        return SELECTION_BOX;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        DebugLog.log("OKOKJRTKFD"); // Jab125? What is this?
        if (!level.isClientSide) {
            SeatUtil.createSeatAndSit(level, pos, player, -1);
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new OfficeChairBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(TYPE);
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    public enum Type implements StringRepresentable {
        LEGS, SEAT, FULL;

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase();
        }
    }
}