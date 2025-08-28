package com.ultreon.devices.core.io.action;

import com.ultreon.devices.api.io.File;
import com.ultreon.devices.api.io.Folder;
import net.minecraft.nbt.CompoundTag;

/**
 * @author MrCrayfish
 */
public class FileAction {
    private final Type type;
    private final CompoundTag data;

    private FileAction(Type type, CompoundTag data) {
        this.type = type;
        this.data = data;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("type", type.ordinal());
        tag.put("data", data);
        return tag;
    }

    public static FileAction fromTag(CompoundTag tag) {
        Type type = Type.values()[tag.getInt("type")];
        CompoundTag data = tag.getCompound("data");
        return new FileAction(type, data);
    }

    public Type getType() {
        return type;
    }

    public CompoundTag getData() {
        return data;
    }

    public enum Type {
        NEW, DELETE, RENAME, DATA, COPY_CUT
    }

    public static class Factory {
        public static FileAction makeNew(Folder parent, File file, boolean override) {
            CompoundTag vars = new CompoundTag();
            vars.putString("directory", parent.getPath());
            vars.putString("file_name", file.getName());
            vars.putBoolean("override", override);
            vars.put("data", file.toTag());
            return new FileAction(Type.NEW, vars);
        }

        public static FileAction makeDelete(File file) {
            CompoundTag vars = new CompoundTag();
            vars.putString("directory", file.getLocation());
            vars.putString("file_name", file.getName());
            return new FileAction(Type.DELETE, vars);
        }

        public static FileAction makeRename(File file, String newFileName) {
            CompoundTag vars = new CompoundTag();
            vars.putString("directory", file.getLocation());
            vars.putString("file_name", file.getName());
            vars.putString("new_file_name", newFileName);
            return new FileAction(Type.RENAME, vars);
        }

        public static FileAction makeData(File file, CompoundTag data) {
            CompoundTag vars = new CompoundTag();
            vars.putString("directory", file.getLocation());
            vars.putString("file_name", file.getName());
            vars.put("data", data);
            return new FileAction(Type.DATA, vars);
        }

        public static FileAction makeCopyCut(File source, Folder destination, boolean override, boolean cut) {
            CompoundTag vars = new CompoundTag();
            vars.putString("directory", source.getLocation());
            vars.putString("file_name", source.getName());
            vars.putString("destination_drive", destination.getDrive().getUUID().toString());
            vars.putString("destination_folder", destination.getPath());
            vars.putBoolean("override", override);
            vars.putBoolean("cut", cut);
            return new FileAction(Type.COPY_CUT, vars);
        }
    }
}
