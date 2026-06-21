package dev.ultreon.devices.block;

import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import dev.ultreon.devices.ModDeviceTypes;
import dev.ultreon.devices.block.entity.ComputerBlockEntity;
import dev.ultreon.devices.util.Colorable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public abstract class ComputerBlock extends DeviceBlock {
    public ComputerBlock(BlockBehaviour.Properties properties) {
        super(properties, ModDeviceTypes.COMPUTER);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        return use(blockState, level, blockPos, player, InteractionHand.MAIN_HAND, blockHitResult);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        InteractionResult use = use(blockState, level, blockPos, player, interactionHand, blockHitResult);
        return switch (use) {
            case SUCCESS, SUCCESS_NO_ITEM_USED -> ItemInteractionResult.SUCCESS;
            case CONSUME -> ItemInteractionResult.CONSUME;
            case PASS -> ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            case CONSUME_PARTIAL -> ItemInteractionResult.CONSUME_PARTIAL;
            default -> ItemInteractionResult.FAIL;
        };
    }

    @NotNull
    public InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ComputerBlockEntity computer) {
            accessComputer(level, computer);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    public void accessComputer(Level level, ComputerBlockEntity computer) {
        if (level.isClientSide) {
            EnvExecutor.runInEnv(Env.CLIENT, () -> () -> {
                ClientLaptopWrapper.execute(computer);
            });
        }
    }

    public abstract boolean isDesktopPC();

    public boolean isLaptop() {
        return !isDesktopPC();
    }

    @Override
    protected void removeTagsForDrop(CompoundTag tileEntityTag) {
        tileEntityTag.remove("open");
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(FACING);
    }

    public enum Type implements StringRepresentable {
        BASE, SCREEN;

        @NotNull
        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public static abstract class Colored extends ComputerBlock implements ColoredBlock {
        private final DyeColor color;

        protected Colored(Properties properties, DyeColor color, ModDeviceTypes deviceType) {
            super(properties);
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
        }
    }
}
