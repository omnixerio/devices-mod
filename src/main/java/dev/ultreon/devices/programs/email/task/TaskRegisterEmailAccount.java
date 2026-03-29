package dev.ultreon.devices.programs.email.task;

import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.programs.email.EmailManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class TaskRegisterEmailAccount extends Task {
    private String name;

    public TaskRegisterEmailAccount() {
        super("register_email_account");
    }

    public TaskRegisterEmailAccount(String name) {
        this();
        this.name = name;
    }

    @Override
    public void prepareRequest(CompoundTag nbt) {
        nbt.putString("AccountName", this.name);
    }

    @Override
    public void processRequest(CompoundTag nbt, Level level, Player player) {
        if (EmailManager.INSTANCE.addAccount(player, nbt.getString("AccountName").orElse(null))) {
            this.setSuccessful();
        }
    }

    @Override
    public void prepareResponse(CompoundTag nbt) {
    }

    @Override
    public void processResponse(CompoundTag nbt) {
    }

}
