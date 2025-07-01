package dev.ultreon.devices;

public enum DyeColor {
    BLACK,
    BLUE,
    BROWN,
    CYAN,
    GRAY,
    GREEN,
    LIGHT_BLUE,
    LIGHT_GRAY,
    LIME,
    MAGENTA,
    ORANGE,
    PINK,
    PURPLE,
    RED,
    WHITE,
    YELLOW;

    public static DyeColor byId(byte color) {
        if (color < 0 || color >= DyeColor.values().length) {
            return DyeColor.WHITE;
        }
        return DyeColor.values()[color];
    }

    public String getName() {
        return this.name().toLowerCase();
    }

    public byte getId() {
        return (byte) this.ordinal();
    }
}
