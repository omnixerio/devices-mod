package dev.ultreon.devices.programs.email.task;

import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.programs.email.EmailManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class TaskCheckEmailAccount extends Task {
    private boolean hasAccount = false;
    private String name = null;

    public TaskCheckEmailAccount() {
        super("check_email_account");
    }

    @Override
    public void prepareRequest(CompoundTag tag) {

    }

    @Override
    public void processRequest(CompoundTag tag, Level level, Player player) {
        this.hasAccount = EmailManager.INSTANCE.hasAccount(player.getUUID());
        if (this.hasAccount) {
            this.name = EmailManager.INSTANCE.getName(player);
            this.setSuccessful();
        }
    }

    @Override
    public void prepareResponse(CompoundTag tag) {
        if (this.isSucessful()) tag.putString("Name", this.name);
    }

    @Override
    public void processResponse(CompoundTag tag) {

    }

}
