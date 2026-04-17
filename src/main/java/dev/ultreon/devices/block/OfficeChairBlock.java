package dev.ultreon.devices.block;

import com.mojang.serialization.MapCodec;
import dev.ultreon.devices.ModDeviceTypes;
import dev.ultreon.devices.block.entity.OfficeChairBlockEntity;
import dev.ultreon.devices.debug.DebugLog;
import dev.ultreon.devices.entity.SeatEntity;
import dev.ultreon.devices.util.SeatUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
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
import org.jspecify.annotations.NonNull;

public class OfficeChairBlock extends DeviceBlock.Colored {
    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);

    private static final VoxelShape EMPTY_BOX = Shapes.box(0, 0, 0, 0, 0, 0);
    private static final VoxelShape SELECTION_BOX = Shapes.box(0.0625f, 0, 0.0625f, 0.9375f, /*1.6875f*/0.625f, 0.9375f);
    private static final VoxelShape SEAT_BOUNDING_BOX = Shapes.box(0.0625f, 0, 0.0625f, 0.9375f, 0.625f, 0.9375f);

    public OfficeChairBlock(DyeColor color, Properties id) {
        super(id.mapColor(color), color, ModDeviceTypes.SEAT);
        //this.setUnlocalizedName("office_chair");
        //this.setRegistryName("office_chair");
        //this.setCreativeTab(MrCrayfishDeviceMod.TAB_DEVICE);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(TYPE, Type.LEGS));
    }

    public OfficeChairBlock(Properties properties) {
        super(properties, DyeColor.WHITE, ModDeviceTypes.SEAT);
    }

//    @Override
//    public String getDescriptionId() {
//        return Util.makeDescriptionId("block", new Identifier("devices", "office_chair"));
//    }

    @Override
    public boolean canSurvive(@NonNull BlockState state, @NonNull LevelReader reader, @NonNull BlockPos pos) {
        return true;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return SELECTION_BOX;
    }

    @Override
    public @NonNull VoxelShape getVisualShape(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        return SEAT_BOUNDING_BOX;
    }

    @Override
    public @NonNull VoxelShape getCollisionShape(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        if (context instanceof EntityCollisionContext entityCollisionContext) {
            if (entityCollisionContext.getEntity() != null && entityCollisionContext.getEntity().getVehicle() instanceof SeatEntity) {
                return EMPTY_BOX;
            }
        }
        return SELECTION_BOX;
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hitResult) {
        DebugLog.log("OKOKJRTKFD");
        if (!level.isClientSide()) {
            SeatUtil.createSeatAndSit(level, pos, player, -1);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected @NonNull InteractionResult useItemOn(@NonNull ItemStack itemStack, @NonNull BlockState state, Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hitResult) {
        DebugLog.log("OKOKJRTKFD");
        if (!level.isClientSide()) {
            SeatUtil.createSeatAndSit(level, pos, player, -1);
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new OfficeChairBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(TYPE);
    }

    @Override
    protected @NonNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return simpleCodec(OfficeChairBlock::new);
    }

    public enum Type implements StringRepresentable {
        LEGS, SEAT, FULL;

        @Override
        public @NonNull String getSerializedName() {
            return name().toLowerCase();
        }
    }
}