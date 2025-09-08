package dev.ultreon.devices.programs.system.task;

import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.api.utils.BankUtil;
import dev.ultreon.devices.programs.system.object.Account;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class TaskGetBalance extends Task {
    private int balance;

    public TaskGetBalance() {
        super("bank_get_balance");
    }

    @Override
    public void prepareRequest(HolderLookup.Provider provider, CompoundTag tag) {
    }

    @Override
    public void processRequest(HolderLookup.Provider provider, CompoundTag tag, Level level, Player player) {
        Account account = BankUtil.INSTANCE.getAccount(player);
        this.balance = account.getBalance();
        this.setSuccessful();
    }

    @Override
    public void prepareResponse(HolderLookup.Provider provider, CompoundTag tag) {
        tag.putInt("balance", this.balance);
    }

    @Override
    public void processResponse(HolderLookup.Provider provider, CompoundTag tag) {
    }
}
