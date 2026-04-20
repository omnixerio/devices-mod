package dev.ultreon.devices.programs.system.task;

import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.api.utils.BankUtil;
import dev.ultreon.devices.programs.system.object.Account;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class TaskRemove extends Task {
    private int amount;

    public TaskRemove() {
        super("bank_remove");
    }

    public TaskRemove(int amount) {
        this();
        this.amount = amount;
    }

    @Override
    public void prepareRequest(CompoundTag tag) {
        tag.putInt("amount", this.amount);
    }

    @Override
    public void processRequest(CompoundTag tag, Level level, Player player) {
        this.amount = tag.getIntOr("amount", 0);
        Account sender = BankUtil.INSTANCE.getAccount(player);
        if (sender.hasAmount(amount)) {
            sender.remove(amount);
            this.setSuccessful();
        }
    }

    @Override
    public void prepareResponse(CompoundTag tag) {
        if (isSucessful()) {
            tag.putInt("balance", this.amount);
        }
    }

    @Override
    public void processResponse(CompoundTag tag) {
    }
}
