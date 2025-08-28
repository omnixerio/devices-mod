package com.ultreon.devices.api.app;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.core.client.ClientNotification;
import com.ultreon.devices.network.PacketHandler;
import com.ultreon.devices.network.task.NotificationPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.lang3.StringUtils;

/**
 * The notification class for the notification system.
 * <p>
 * This class is intended to be used only on the server (logical and physical) side only. Typically,
 * you'd want to be able to send a notification to anyone on the server. There is two options to
 * perform this, either create a background task on the server (a tick event) or send a
 * {@link Task} from the client to the server. It is not possible to
 * do this from the client side alone.
 * <p>
 * If a notification is needed to be produced on the client side only, see
 * {@link ClientNotification}
 */
public class Notification {
    private final IIcon icon;
    private final String title;
    private String subTitle;

    /**
     * The default constructor for a notification.
     *
     * @param icon  the icon to display
     * @param title the title of the notification
     */
    public Notification(IIcon icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    /**
     * The alternate constructor for a notification. This includes a subtitle.
     *
     * @param icon     the icon to display
     * @param title    the title of the notification
     * @param subTitle the subtitle of the notification
     */
    public Notification(IIcon icon, String title, String subTitle) {
        this(icon, title);
        this.subTitle = subTitle;
    }

    /**
     * Writes the notification to a tag for the client
     *
     * @return the notification tag
     */
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("title", title);

        if (!StringUtils.isEmpty(subTitle)) {
            tag.putString("subTitle", subTitle);
        }

        CompoundTag tagIcon = new CompoundTag();
        tagIcon.putInt("ordinal", icon.getOrdinal());
        tagIcon.putString("className", icon.getClass().getName());

        tag.put("icon", tagIcon);

        return tag;
    }

    /**
     * Sends this notification to the specified player
     *
     * @param player the target player
     */
    public void pushTo(ServerPlayer player) {
        PacketHandler.sendToClient(new NotificationPacket(this), player);
    }
}
