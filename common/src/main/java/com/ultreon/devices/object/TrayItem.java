package com.ultreon.devices.object;

import com.ultreon.devices.api.app.IIcon;
import com.ultreon.devices.api.app.listener.ClickListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;

/**
 * @author MrCrayfish
 */
public class TrayItem {
    private IIcon icon;
    private ClickListener listener;
    private final Identifier id;

    public TrayItem(IIcon icon, Identifier id) {
        this.icon = icon;
        this.id = id;
    }

    public void init() {
    }

    public void tick() {
    }

    public void setIcon(IIcon icon) {
        this.icon = icon;
    }

    public IIcon getIcon() {
        return icon;
    }

    public void setClickListener(ClickListener listener) {
        this.listener = listener;
    }

    public void handleClick(int mouseX, int mouseY, int mouseButton) {
        if (listener != null) {
            listener.onClick(mouseX, mouseY, mouseButton);
        }
    }

    public Identifier getId() {
        return id;
    }

    public CompoundTag serialize() {
        return new CompoundTag();
    }

    public void deserialize(CompoundTag trayTag) {

    }
}
