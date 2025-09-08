package dev.ultreon.devices.programs.example.task;

import dev.ultreon.devices.api.app.Icons;
import dev.ultreon.devices.api.app.Notification;
import dev.ultreon.devices.api.task.Task;
import net.minecraft.core.HolderLookup;
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
    public void prepareRequest(HolderLookup.Provider provider, CompoundTag nbt) {

    }

    @Override
    public void processRequest(HolderLookup.Provider provider, CompoundTag nbt, Level world, Player player) {
        Notification notification = new Notification(Icons.MAIL, "New Email!", "Check your inbox");
        notification.pushTo((ServerPlayer) player);

       /* MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        List<EntityPlayerMP> players = server.getPlayerList().getPlayers();
        players.forEach(notification::pushTo);*/
    }

    @Override
    public void prepareResponse(HolderLookup.Provider provider, CompoundTag nbt) {

    }

    @Override
    public void processResponse(HolderLookup.Provider provider, CompoundTag nbt) {

    }
}
