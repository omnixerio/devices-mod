package mastef_chief.gitwebbuilder.app.tasks;

import com.ultreon.devices.api.app.Icons;
import com.ultreon.devices.api.app.Notification;
import com.ultreon.devices.api.task.Task;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class TaskNotificationCopiedLink extends Task {

    public TaskNotificationCopiedLink() {
        super("notification_copiedlink");
    }

    /**
     * Called before the request is sent off to the server.
     * You should store the data you want to sendTask into the NBT Tag
     *
     * @param nbt The NBT to be sent to the server
     */
    @Override
    public void prepareRequest(CompoundTag nbt) {

    }

    /**
     * Called when the request arrives to the server. Here you can perform actions
     * with your request. Data attached to the NBT from {@link Task#prepareRequest(NBTTagCompound nbt)}
     * can be accessed from the NBT tag parameter.
     *
     * @param nbt    The NBT Tag received from the client
     * @param world
     * @param player
     */
    @Override
    public void processRequest(CompoundTag nbt, Level level, Player player) {
        Notification notification = new Notification(Icons.COPY, ChatFormatting.BOLD + "Copied", "Link To Clipboard");
        notification.pushTo((ServerPlayer) player);
    }

    /**
     * Called before the response is sent back to the client.
     * You should store the data you want to sendTask back into the NBT Tag
     *
     * @param nbt The NBT to be sent back to the client
     */
    @Override
    public void prepareResponse(CompoundTag nbt) {

    }

    /**
     * Called when the response arrives to the client. Here you can update data
     * on the client side. If you want to update any UI component, you should set
     * a Callback before you sendTask the request. See {@link #setCallback(Callback)}
     *
     * @param nbt The NBT Tag received from the server
     */
    @Override
    public void processResponse(CompoundTag nbt) {

    }
}
