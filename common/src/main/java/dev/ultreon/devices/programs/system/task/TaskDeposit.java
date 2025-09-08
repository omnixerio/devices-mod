package dev.ultreon.devices.programs.system.task;

import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.api.utils.BankUtil;
import dev.ultreon.devices.programs.system.object.Account;
import dev.ultreon.devices.util.InventoryUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * @author MrCrayfish
 */
public class TaskDeposit extends Task {
    private int amount;

    public TaskDeposit() {
        super("bank_deposit");
    }

    public TaskDeposit(int amount) {
        this();
        this.amount = amount;
    }

    @Override
    public void prepareRequest(HolderLookup.Provider provider, CompoundTag tag) {
        tag.putInt("amount", this.amount);
    }

    @Override
    public void processRequest(HolderLookup.Provider provider, CompoundTag tag, Level level, Player player) {
        Account account = BankUtil.INSTANCE.getAccount(player);
        int amount = tag.getInt("amount");
        long value = account.getBalance() + amount;
        if (value < 0) {
            amount = Integer.MAX_VALUE - account.getBalance();
        }
        if (InventoryUtil.removeItemWithAmount(player, Items.EMERALD, amount)) {
            if (account.deposit(amount)) {
                this.amount = account.getBalance();
                this.setSuccessful();
            }
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
