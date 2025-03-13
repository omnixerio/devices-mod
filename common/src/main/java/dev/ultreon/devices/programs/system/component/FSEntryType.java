package dev.ultreon.devices.programs.system.component;

public enum FSEntryType {
    FILE,
    FOLDER;

    public static FSEntryType fromByte(byte type) {
        return type == 0 ? FILE : FOLDER;
    }
}
