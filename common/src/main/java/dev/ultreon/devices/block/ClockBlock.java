package dev.ultreon.devices.block;

import com.mojang.serialization.MapCodec;
import dev.ultreon.devices.ModDeviceTypes;
import dev.ultreon.devices.block.entity.ClockBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.phys.shapes.Shapes.box;

public class ClockBlock extends DeviceBlock.Colored {
    private static final VoxelShape[] BODY_BOUNDING_BOX = {
            box(4, 0, 2, 12, 7, 14),
            box(2, 0, 4, 14, 7, 12),
            box(4, 0, 2, 12, 7, 14),
            box(2, 0, 4, 14, 7, 12)
    };

    public ClockBlock(BlockBehaviour.Properties properties, DyeColor color) {
        super(properties, color, ModDeviceTypes.CLOCK);
    }

    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return switch (pState.getValue(FACING)) {
            case EAST -> BODY_BOUNDING_BOX[0];
            case NORTH -> BODY_BOUNDING_BOX[1];
            case WEST -> BODY_BOUNDING_BOX[2];
            case SOUTH -> BODY_BOUNDING_BOX[3];
            default -> BODY_BOUNDING_BOX[0];
        };
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ClockBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return simpleCodec(properties1 -> new ClockBlock(properties, color));
    }
}
