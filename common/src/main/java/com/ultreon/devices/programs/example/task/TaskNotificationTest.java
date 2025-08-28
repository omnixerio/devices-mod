package com.ultreon.devices.programs.example.task;

import com.ultreon.devices.api.app.Icons;
import com.ultreon.devices.api.app.Notification;
import com.ultreon.devices.api.task.Task;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * @author MrCrayfish
 */
public class TaskNotificationTest extends Task {
    public TaskNotificationTest() {
        super("notification_test");
    }

    @Override
    public void prepareRequest(CompoundTag nbt) {

    }

    @Override
    public void processRequest(CompoundTag nbt, Level world, Player player) {
        Notification notification = new Notification(Icons.MAIL, "New Email!", "Check your inbox");
        notification.pushTo((ServerPlayer) player);

       /* MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        List<EntityPlayerMP> players = server.getPlayerList().getPlayers();
        players.forEach(notification::pushTo);*/
    }

    @Override
    public void prepareResponse(CompoundTag nbt) {

    }

    @Override
    public void processResponse(CompoundTag nbt) {

    }
}
