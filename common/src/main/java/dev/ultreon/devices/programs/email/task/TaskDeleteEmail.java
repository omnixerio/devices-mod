package dev.ultreon.devices.programs.email.task;

import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.programs.email.EmailManager;
import dev.ultreon.devices.programs.email.object.Email;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

public class TaskDeleteEmail extends Task {

	private int index;

	public TaskDeleteEmail() {
		super("delete_email");
	}

	public TaskDeleteEmail(int index) {
		this();
		this.index = index;
	}

	@Override
	public void prepareRequest(HolderLookup.Provider provider, CompoundTag nbt) {
		nbt.putInt("Index", this.index);
	}

	@Override
	public void processRequest(HolderLookup.Provider provider, CompoundTag nbt, Level level, Player player) {
		List<Email> emails = EmailManager.INSTANCE.getEmailsForAccount(player);
		if (emails != null) {
			int index = nbt.getInt("Index");
			if (index >= 0 && index < emails.size()) {
				emails.remove(index);
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
