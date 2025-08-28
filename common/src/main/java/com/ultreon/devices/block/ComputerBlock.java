package com.ultreon.devices.block;

import com.ultreon.devices.ModDeviceTypes;
import com.ultreon.devices.block.entity.LaptopBlockEntity;
import com.ultreon.devices.debug.DebugLog;
import com.ultreon.devices.item.FlashDriveItem;
import com.ultreon.devices.util.BlockEntityUtil;
import com.ultreon.devices.util.Colorable;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public abstract class ComputerBlock extends DeviceBlock {
    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);
    public static final BooleanProperty OPEN = BooleanProperty.create("open");

    public ComputerBlock(BlockBehaviour.Properties properties) {
        super(properties, ModDeviceTypes.COMPUTER);
        registerDefaultState(this.getStateDefinition().any().setValue(TYPE, Type.BASE).setValue(OPEN, false));
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof LaptopBlockEntity laptop) {
            if (player.isCrouching()) {
                if (!level.isClientSide) {
                    laptop.openClose(player);
                }
                return InteractionResult.SUCCESS;
            } else {
                if (hit.getDirection() == state.getValue(FACING).getClockWise(Direction.Axis.Y)) {
                    ItemStack heldItem = player.getItemInHand(hand);
                    if (!heldItem.isEmpty() && heldItem.getItem() instanceof FlashDriveItem) {
                        if (laptop.canChangeAttachment()) {
                            if (laptop.getFileSystem().attachDrive(heldItem.copy())) {
                                DebugLog.logTime(level.getGameTime(), "Attached Drive");
                                laptop.setAttachmentCooldown(10);
                                heldItem.shrink(1);
                                return InteractionResult.sidedSuccess(level.isClientSide);
                            } else {
                                return InteractionResult.FAIL;
                            }
                        }
                    }

                    if (laptop.canChangeAttachment()) {
                        ItemStack stack = laptop.getFileSystem().detachDrive();
                        if (stack != null) {
                            DebugLog.logTime(level.getGameTime(), "Detached Drive");
                            laptop.setAttachmentCooldown(10);
                            BlockPos summonPos = pos.relative(state.getValue(FACING).getClockWise(Direction.Axis.Y));
                            level.addFreshEntity(new ItemEntity(level, summonPos.getX() + 0.5, summonPos.getY(), summonPos.getZ() + 0.5, stack));
                            BlockEntityUtil.markBlockForUpdate(level, pos);
                            return InteractionResult.sidedSuccess(level.isClientSide);
                        }
                    }
                    return InteractionResult.FAIL;
                }

                if (laptop.isOpen()) {
                    if (level.isClientSide) {
                        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> {
                            ClientLaptopWrapper.execute(laptop);
                        });
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }

        return InteractionResult.PASS;
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
        pBuilder.add(TYPE, OPEN, FACING);
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
