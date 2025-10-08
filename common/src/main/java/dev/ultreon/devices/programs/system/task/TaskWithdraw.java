package dev.ultreon.devices.programs.system.task;

import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.api.utils.BankUtil;
import dev.ultreon.devices.programs.system.object.Account;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * @author MrCrayfish
 */
public class TaskWithdraw extends Task {
    private int amount;

    public TaskWithdraw() {
        super();
    }

    public TaskWithdraw(int amount) {
        this();
        this.amount = amount;
    }

    @Override
    public void prepareRequest(HolderLookup.Provider provider, CompoundTag tag) {
        tag.putInt("amount", this.amount);
    }

    @Override
    public void processRequest(HolderLookup.Provider provider, CompoundTag tag, Level level, Player player) {
        int amount = tag.getInt("amount");
        Account account = BankUtil.INSTANCE.getAccount(player);
        if (account.withdraw(amount)) {
            int stacks = amount / 64;
            for (int i = 0; i < stacks; i++) {
                level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), new ItemStack(Items.EMERALD, 64)));
            }

            int remaining = amount % 64;
            if (remaining > 0) {
                level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), new ItemStack(Items.EMERALD, remaining)));
            }

            this.amount = account.getBalance();
            this.setSuccessful();
        }
    }

    @Override
    public void prepareResponse(HolderLookup.Provider provider, CompoundTag tag) {
        tag.putInt("balance", this.amount);
    }

    @Override
    public void processResponse(HolderLookup.Provider provider, CompoundTag tag) {
    }
}
