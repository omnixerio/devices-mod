package dev.ultreon.devices.programs.email.task;

import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.programs.email.EmailManager;
import dev.ultreon.devices.programs.email.object.Email;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class TaskSendEmail extends Task {
    private Email email;
    private String to;

    public TaskSendEmail() {
        super();
    }

    public TaskSendEmail(Email email, String to) {
        this();
        this.email = email;
        this.to = to;
    }

    @Override
    public void prepareRequest(HolderLookup.Provider provider, CompoundTag nbt) {
        this.email.save(nbt);
        nbt.putString("to", this.to);
    }

    @Override
    public void processRequest(HolderLookup.Provider provider, CompoundTag nbt, Level level, Player player) {
        String name = EmailManager.INSTANCE.getName(player);
        if (name != null) {
            Email email = Email.readFromNBT(nbt);
            email.setAuthor(name);
            if (EmailManager.INSTANCE.addEmailToInbox(email, nbt.getString("to"))) {
                this.setSuccessful();
            }
        }
    }

    @Override
    public void prepareResponse(HolderLookup.Provider provider, CompoundTag nbt) {
    }

    @Override
    public void processResponse(HolderLookup.Provider provider, CompoundTag nbt) {
    }

}
