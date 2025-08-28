package com.ultreon.devices.block.entity;

import com.ultreon.devices.DeviceConfig;
import com.ultreon.devices.api.print.IPrint;
import com.ultreon.devices.init.DeviceBlockEntities;
import com.ultreon.devices.init.DeviceSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;

import static com.ultreon.devices.block.entity.PrinterBlockEntity.State.*;

/**
 * @author MrCrayfish
 */
public class PrinterBlockEntity extends NetworkDeviceBlockEntity.Colored {
    private State state = IDLE;

    private final Deque<IPrint> printQueue = new ArrayDeque<>();
    private IPrint currentPrint;

    private int totalPrintTime;
    private int remainingPrintTime;
    private int paperCount = 0;

    public PrinterBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(DeviceBlockEntities.PRINTER.get(), pWorldPosition, pBlockState);
    }

    @Override
    public void tick() {
        assert level != null;
        if (!level.isClientSide) {
            if (remainingPrintTime > 0) {
                if (remainingPrintTime % 20 == 0 || state == LOADING_PAPER) {
                    pipeline.putInt("remainingPrintTime", remainingPrintTime);
                    sync();
                    if (remainingPrintTime != 0 && state == PRINTING) {
                        level.playSound(null, worldPosition, DeviceSounds.PRINTER_PRINTING.get(), SoundSource.BLOCKS, 0.5f, 1f);
                    }
                }
                remainingPrintTime--;
            } else {
                setState(state.next());
            }
        }

        if (state == IDLE && remainingPrintTime == 0 && currentPrint != null) {
            if (!level.isClientSide) {
//                BlockState state = level.getBlockState(worldPosition);
//                double[] fixedPosition = CollisionHelper.fixRotation(state.getValue(PrinterBlock.FACING), 0.15, 0.5, 0.15, 0.5);
                ItemEntity entity = new ItemEntity(level, worldPosition.getX(), worldPosition.getY() + 0.0625, worldPosition.getZ(), IPrint.generateItem(currentPrint));
                entity.setDeltaMovement(new Vec3(0, 0, 0));
                level.addFreshEntity(entity);
            }
            currentPrint = null;
        }

        if (state == IDLE && currentPrint == null && !printQueue.isEmpty() && paperCount > 0) {
            print(printQueue.poll());
        }
    }

    @Override
    public String getDeviceName() {
        return "Printer";
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        super.load(compound);
        if (compound.contains("currentPrint", Tag.TAG_COMPOUND)) {
            currentPrint = IPrint.load(compound.getCompound("currentPrint"));
        }
        if (compound.contains("totalPrintTime", Tag.TAG_INT)) {
            totalPrintTime = compound.getInt("totalPrintTime");
        }
        if (compound.contains("remainingPrintTime", Tag.TAG_INT)) {
            remainingPrintTime = compound.getInt("remainingPrintTime");
        }
        if (compound.contains("state", Tag.TAG_INT)) {
            state = State.values()[compound.getInt("state")];
        }
        if (compound.contains("paperCount", Tag.TAG_INT)) {
            paperCount = compound.getInt("paperCount");
        }
        if (compound.contains("queue", Tag.TAG_LIST)) {
            printQueue.clear();
            ListTag queue = compound.getList("queue", Tag.TAG_COMPOUND);
            for (int i = 0; i < queue.size(); i++) {
                IPrint print = IPrint.load(queue.getCompound(i));
                printQueue.offer(print);
            }
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("totalPrintTime", totalPrintTime);
        tag.putInt("remainingPrintTime", remainingPrintTime);
        tag.putInt("state", state.ordinal());
        tag.putInt("paperCount", paperCount);
        if (currentPrint != null) {
            tag.put("currentPrint", IPrint.save(currentPrint));
        }
        if (!printQueue.isEmpty()) {
            ListTag queue = new ListTag();
            printQueue.forEach(print -> queue.add(IPrint.save(print)));
            tag.put("queue", queue);
        }
    }

    @Override
    public CompoundTag saveSyncTag() {
        CompoundTag tag = super.saveSyncTag();
        tag.putInt("paperCount", paperCount);
        return tag;
    }

    public void setState(State newState) {
        if (newState == null) return;

        state = newState;
        if (state == PRINTING) {
            if (DeviceConfig.OVERRIDE_PRINT_SPEED.get()) {
                remainingPrintTime = DeviceConfig.CUSTOM_PRINT_SPEED.get() * 20;
            } else {
                remainingPrintTime = currentPrint.speed() * 20;
            }
        } else {
            remainingPrintTime = state.animationTime;
        }
        totalPrintTime = remainingPrintTime;

        pipeline.putInt("state", state.ordinal());
        pipeline.putInt("totalPrintTime", totalPrintTime);
        pipeline.putInt("remainingPrintTime", remainingPrintTime);
        sync();
    }

    public void addToQueue(IPrint print) {
        printQueue.offer(print);
    }

    private void print(IPrint print) {
        assert level != null;
        level.playSound(null, worldPosition, DeviceSounds.PRINTER_LOADING_PAPER.get(), SoundSource.BLOCKS, 0.5f, 1f);

        setState(LOADING_PAPER);
        currentPrint = print;
        paperCount--;

        pipeline.putInt("paperCount", paperCount);
        pipeline.put("currentPrint", IPrint.save(currentPrint));
        sync();
    }

    public boolean isLoading() {
        return state == LOADING_PAPER;
    }

    public boolean isPrinting() {
        return state == PRINTING;
    }

    public int getTotalPrintTime() {
        return totalPrintTime;
    }

    public int getRemainingPrintTime() {
        return remainingPrintTime;
    }

    public boolean addPaper(ItemStack stack, boolean addAll) {
        if (!stack.isEmpty() && stack.getItem() == Items.PAPER && paperCount < DeviceConfig.MAX_PAPER_COUNT.get()) {
            if (!addAll) {
                paperCount++;
                stack.shrink(1);
            } else {
                paperCount += stack.getCount();
                stack.setCount(Math.max(0, paperCount - 64));
                paperCount = Math.min(64, paperCount);
            }
            pipeline.putInt("paperCount", paperCount);
            sync();
            assert level != null;
            level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_BREAK, SoundSource.BLOCKS, 1f, 1f);
            return true;
        }
        return false;
    }

    public boolean hasPaper() {
        return paperCount > 0;
    }

    public int getPaperCount() {
        return paperCount;
    }

    public IPrint getPrint() {
        return currentPrint;
    }

    public enum State {
        LOADING_PAPER(30), PRINTING(0), IDLE(0);

        final int animationTime;

        State(int time) {
            this.animationTime = time;
        }

        public State next() {
            if (ordinal() + 1 >= values().length) return null;
            return values()[ordinal() + 1];
        }
    }
}
