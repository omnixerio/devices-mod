package dev.ultreon.devices.block;

import dev.ultreon.devices.DyeColor;
import dev.ultreon.devices.EnvExecutor;
import dev.ultreon.devices.ModDeviceTypes;
import dev.ultreon.devices.block.entity.LaptopBlockEntity;
import dev.ultreon.devices.debug.DebugLog;
import dev.ultreon.devices.item.FlashDriveItem;
import dev.ultreon.devices.object.Player;
import dev.ultreon.devices.util.BlockEntityUtil;
import dev.ultreon.devices.util.Colorable;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import dev.ultreon.quantum.block.Block;
import dev.ultreon.quantum.block.BlockState;
import dev.ultreon.quantum.block.property.BoolPropertyKey;
import dev.ultreon.quantum.block.property.EnumPropertyKey;
import dev.ultreon.quantum.block.property.StringSerializable;
import dev.ultreon.quantum.util.Env;
import dev.ultreon.quantum.world.World;
import dev.ultreon.quantum.world.vec.BlockVec;
import net.minecraft.core.BlockVec;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import dev.ultreon.quantum.item.ItemStack;
import net.minecraft.world.world.World;
import net.minecraft.world.world.block.Block;
import net.minecraft.world.world.block.entity.BlockEntity;
import net.minecraft.world.world.block.state.BlockBehaviour;
import net.minecraft.world.world.block.state.BlockState;
import net.minecraft.world.world.block.state.StateDefinition;
import net.minecraft.world.world.block.state.properties.BooleanProperty;
import net.minecraft.world.world.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

import static dev.ultreon.quantum.block.property.StateProperties.FACING;

public abstract class ComputerBlock extends DeviceBlock {
    public static final EnumPropertyKey<Type> TYPE = new EnumPropertyKey("type", Type.class);
    public static final BoolPropertyKey OPEN = new BoolPropertyKey("open");

    public ComputerBlock(Block.Properties properties) {
        super(properties, ModDeviceTypes.COMPUTER);
        registerDefaultState(this.getStateDefinition().any().setValue(TYPE, Type.BASE).setValue(OPEN, false));
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(@NotNull BlockState state, @NotNull World world, @NotNull BlockVec vec, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {

        BlockEntity blockEntity = world.getBlockEntity(vec);
        if (blockEntity instanceof LaptopBlockEntity laptop) {
            if (player.isCrouching()) {
                if (!world.isClientSide) {
                    laptop.openClose(player);
                }
                return InteractionResult.SUCCESS;
            } else {
                if (hit.getDirection() == state.getValue(FACING).getClockWise(Direction.Axis.Y)) {
                    ItemStack heldItem = player.getItemInHand(hand);
                    if (!heldItem.isEmpty() && heldItem.getItem() instanceof FlashDriveItem) {
                        if (laptop.canChangeAttachment()) {
                            if (laptop.getFileSystem().attachDrive(heldItem.copy())) {
                                DebugLog.logTime(world.getGameTime(), "Attached Drive");
                                laptop.setAttachmentCooldown(10);
                                heldItem.shrink(1);
                                return InteractionResult.sidedSuccess(world.isClientSide);
                            } else {
                                return InteractionResult.FAIL;
                            }
                        }
                    }

                    if (laptop.canChangeAttachment()) {
                        ItemStack stack = laptop.getFileSystem().detachDrive();
                        if (stack != null) {
                            DebugLog.logTime(world.getGameTime(), "Detached Drive");
                            laptop.setAttachmentCooldown(10);
                            BlockVec summonVec = vec.relative(state.getValue(FACING).getClockWise(Direction.Axis.Y));
                            world.addFreshEntity(new ItemEntity(world, summonVec.getX() + 0.5, summonVec.getY(), summonVec.getZ() + 0.5, stack));
                            BlockEntityUtil.updateBlock(world, vec);
                            return InteractionResult.sidedSuccess(world.isClientSide);
                        }
                    }
                    return InteractionResult.FAIL;
                }

                if (laptop.isOpen()) {
                    if (world.isClientSide) {
                        EnvExecutor.runInEnv(dev.ultreon.quantum.util.Env.CLIENT, () -> () -> {
                            ClientLaptopWrapper.execute(laptop);
                        });
                    }
                    return InteractionResult.sidedSuccess(world.isClientSide);
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

    public enum Type implements StringSerializable {
        BASE, SCREEN;

        @Override
        public @NotNull String serialize() {
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
        public void setPlacedBy(@NotNull World world, @NotNull BlockVec vec, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
            super.setPlacedBy(world, vec, state, placer, stack);
            BlockEntity blockEntity = world.getBlockEntity(vec);
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
