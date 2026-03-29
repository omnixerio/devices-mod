package com.ultreon.devices.programs.email.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.programs.email.EmailManager;
import com.ultreon.devices.programs.email.object.Email;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

public class TaskUpdateInbox extends Task {
    private List<Email> emails;

    public TaskUpdateInbox() {
        super("update_inbox");
    }

    @Override
    public void prepareRequest(CompoundTag nbt) {
    }

    @Override
    public void processRequest(CompoundTag nbt, Level world, Player player) {
        this.emails = EmailManager.INSTANCE.getEmailsForAccount(player);
    }

    @Override
    public void prepareResponse(CompoundTag nbt) {
        ListTag tagList = new ListTag();
        if (emails != null) {
            for (Email email : emails) {
                CompoundTag emailTag = new CompoundTag();
                email.save(emailTag);
                tagList.add(emailTag);
            }
        }
        nbt.put("emails", tagList);
    }

    @Override
    public void processResponse(CompoundTag nbt) {
        EmailManager.INSTANCE.getInbox().clear();
        ListTag emails = (ListTag) nbt.get("emails");
        for (int i = 0; i < emails.size(); i++) {
            CompoundTag emailTag = emails.getCompoundOrEmpty(i);
            Email email = Email.readFromNBT(emailTag);
            EmailManager.INSTANCE.getInbox().add(email);
        }
    }
}
