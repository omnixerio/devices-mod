package com.ultreon.devices.programs.system.object;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.awt.*;

/**
 * @author MrCrayfish
 */
public class ColorScheme {
    public int buttonColor;
    public int textColor;
    public int textSecondaryColor;
    public int headerColor;
    public int backgroundColor;
    public int backgroundSecondaryColor;
    public int itemBackgroundColor;
    public int itemHighlightColor;
    public int buttonOutlineColor;
    public int windowBackgroundColor;
    public int windowOutlineColor;
    public int iconColor;
    public int iconSecondaryColor;

    public ColorScheme() {
        resetDefault();
    }

    public static ColorScheme fromTag(CompoundTag tag) {
        ColorScheme scheme = new ColorScheme();
        if (tag.contains("buttonColor", Tag.TAG_INT)) {
            scheme.buttonColor = tag.getInt("buttonColor");
        }
        if (tag.contains("textColor", Tag.TAG_INT)) {
            scheme.textColor = tag.getInt("textColor");
        }
        if (tag.contains("textSecondaryColor", Tag.TAG_INT)) {
            scheme.textSecondaryColor = tag.getInt("textSecondaryColor");
        }
        if (tag.contains("headerColor", Tag.TAG_INT)) {
            scheme.headerColor = tag.getInt("headerColor");
        }
        if (tag.contains("backgroundColor", Tag.TAG_INT)) {
            scheme.backgroundColor = tag.getInt("backgroundColor");
        }
        if (tag.contains("backgroundSecondaryColor", Tag.TAG_INT)) {
            scheme.backgroundSecondaryColor = tag.getInt("backgroundSecondaryColor");
        }
        if (tag.contains("itemBackgroundColor", Tag.TAG_INT)) {
            scheme.itemBackgroundColor = tag.getInt("itemBackgroundColor");
        }
        if (tag.contains("itemHighlightColor", Tag.TAG_INT)) {
            scheme.itemHighlightColor = tag.getInt("itemHighlightColor");
        }
        if (tag.contains("buttonOutlineColor", Tag.TAG_INT)) {
            scheme.buttonOutlineColor = tag.getInt("buttonOutlineColor");
        }
        if (tag.contains("windowBackgroundColor", Tag.TAG_INT)) {
            scheme.windowBackgroundColor = tag.getInt("windowBackgroundColor");
        }
        if (tag.contains("windowOutlineColor", Tag.TAG_INT)) {
            scheme.windowOutlineColor = tag.getInt("windowOutlineColor");
        }
        if (tag.contains("iconColor", Tag.TAG_INT)) {
            scheme.iconColor = tag.getInt("iconColor");
        }
        if (tag.contains("iconSecondaryColor", Tag.TAG_INT)) {
            scheme.iconSecondaryColor = tag.getInt("iconSecondaryColor");
        }
        return scheme;
    }

    public int getButtonColor() {
        return buttonColor;
    }

    public void setButtonColor(int buttonColor) {
        this.buttonColor = buttonColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getTextSecondaryColor() {
        return textSecondaryColor;
    }

    public void setTextSecondaryColor(int textSecondaryColor) {
        this.textSecondaryColor = textSecondaryColor;
    }

    public int getHeaderColor() {
        return headerColor;
    }

    public void setHeaderColor(int headerColor) {
        this.headerColor = headerColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getBackgroundSecondaryColor() {
        return backgroundSecondaryColor;
    }

    public void setBackgroundSecondaryColor(int backgroundSecondaryColor) {
        this.backgroundSecondaryColor = backgroundSecondaryColor;
    }

    public int getItemBackgroundColor() {
        return itemBackgroundColor;
    }

    public void setItemBackgroundColor(int itemBackgroundColor) {
        this.itemBackgroundColor = itemBackgroundColor;
    }

    public int getItemHighlightColor() {
        return itemHighlightColor;
    }

    public void setItemHighlightColor(int itemHighlightColor) {
        this.itemHighlightColor = itemHighlightColor;
    }

    public int getButtonOutlineColor() {
        return buttonOutlineColor;
    }

    public void setButtonOutlineColor(int buttonOutlineColor) {
        this.buttonOutlineColor = buttonOutlineColor;
    }

    public int getWindowBackgroundColor() {
        return windowBackgroundColor;
    }

    public void setWindowBackgroundColor(int windowBackgroundColor) {
        this.windowBackgroundColor = windowBackgroundColor;
    }

    public int getWindowOutlineColor() {
        return windowOutlineColor;
    }

    public void setWindowOutlineColor(int windowOutlineColor) {
        this.windowOutlineColor = windowOutlineColor;
    }

    public int getIconColor() {
        return iconColor;
    }

    public void setIconColor(int iconColor) {
        this.iconColor = iconColor;
    }

    public int getIconSecondaryColor() {
        return iconSecondaryColor;
    }

    public void setIconSecondaryColor(int iconSecondaryColor) {
        this.iconSecondaryColor = iconSecondaryColor;
    }

    public void resetDefault() {
        buttonColor = Color.decode("0x2E6897").getRGB();
        textColor = Color.decode("0xFFFFFF").getRGB();
        textSecondaryColor = Color.decode("0xABEFF4").getRGB();
        headerColor = Color.decode("0x387A96").getRGB();
        backgroundColor = Color.decode("0x6899C2").getRGB();
        backgroundSecondaryColor = Color.decode("0x36C052").getRGB();
        backgroundColor = Color.decode("0x6899C2").getRGB();
        backgroundSecondaryColor = Color.decode("0x36C052").getRGB();
        iconColor = Color.decode("0x6899C2").getRGB();
        iconSecondaryColor = Color.decode("0x36C052").getRGB();
        itemBackgroundColor = Color.decode("0x2E6897").getRGB();
        itemHighlightColor = Color.decode("0x8B74C9").getRGB();
        buttonOutlineColor = Color.decode("0xFFFFFF").getRGB();
        windowBackgroundColor = Color.decode("0x788086").getRGB();
        windowOutlineColor = Color.decode("0x3F3F3F").getRGB();
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("buttonColor", buttonColor);
        tag.putInt("textColor", textColor);
        tag.putInt("textSecondaryColor", textSecondaryColor);
        tag.putInt("headerColor", headerColor);
        tag.putInt("backgroundColor", backgroundColor);
        tag.putInt("backgroundSecondaryColor", backgroundSecondaryColor);
        tag.putInt("itemBackgroundColor", itemBackgroundColor);
        tag.putInt("itemHighlightColor", itemHighlightColor);
        tag.putInt("buttonOutlineColor", buttonOutlineColor);
        tag.putInt("windowBackgroundColor", windowBackgroundColor);
        tag.putInt("windowOutlineColor", windowOutlineColor);
        tag.putInt("iconColor", iconColor);
        tag.putInt("iconSecondaryColor", iconSecondaryColor);
        return tag;
    }
}
