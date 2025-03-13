package dev.ultreon.devices.core.io.action;

import dev.ultreon.devices.core.DataPath;
import dev.ultreon.devices.programs.system.component.FileInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Unit;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/// @author MrCrayfish
public class FileAction<T> {
    public static final Function<CompoundTag, Object> NULL = tag1 -> null;
    private final Type type;
    private final CompoundTag data;
    private final Function<CompoundTag, T> deserializer;

    private FileAction(Type type, CompoundTag data, Function<CompoundTag, T> deserializer) {
        this.type = type;
        this.data = data;
        this.deserializer = deserializer;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("type", type.ordinal());
        tag.put("data", data);
        return tag;
    }

    public static FileAction<?> fromTag(CompoundTag tag) {
        Type type = Type.values()[tag.getInt("type")];
        CompoundTag data = tag.getCompound("data");
        return new FileAction<>(type, data, NULL);
    }

    public Type getType() {
        return type;
    }

    public CompoundTag getData() {
        return data;
    }

    @Override
    public String toString() {
        return "FileAction{" +
               "type=" + type +
               '}';
    }

    public T deserialize(CompoundTag data) {
        if (deserializer == NULL) throw new UnsupportedOperationException();
        return deserializer.apply(data);
    }

    public enum Type {
        NEW_FILE, NEW_FOLDER, DELETE, RENAME, WRITE, EXISTS, READ, LIST_DIR, INFO, MOVE, COPY, EXTRA_INFO, NEW_FOLDERS, @Deprecated COPY_CUT
    }

    public static class Factory {
        public static FileAction<FileInfo> makeNewFile(Path parent, String name, boolean override) {
            CompoundTag vars = new CompoundTag();
            vars.putString("path", parent.resolve(name).toString());
            vars.putBoolean("override", override);
            vars.putByteArray("data", new byte[0]);
            return new FileAction<>(Type.NEW_FILE, vars, FileInfo::fromTag);
        }
        public static FileAction<FileInfo> makeNewFolder(Path parent, String name, boolean override) {
            CompoundTag vars = new CompoundTag();
            vars.putString("path", parent.resolve(name).toString());
            vars.putBoolean("override", override);
            return new FileAction<>(Type.NEW_FOLDER, vars, FileInfo::fromTag);
        }

        public static FileAction<Unit> makeDelete(Path file) {
            CompoundTag vars = new CompoundTag();
            vars.putString("path", file.toString());
            return new FileAction<>(Type.DELETE, vars, tag -> Unit.INSTANCE);
        }

        public static FileAction<Unit> makeRename(Path file, String newFileName) {
            CompoundTag vars = new CompoundTag();
            vars.putString("path", file.toString());
            vars.putString("new_file_name", newFileName);
            return new FileAction<>(Type.RENAME, vars, tag -> Unit.INSTANCE);
        }

        @Deprecated
        public static FileAction<Unit> makeData(Path file, CompoundTag data) throws IOException {
            CompoundTag vars = new CompoundTag();
            vars.putString("path", file.toString());
            vars.putByteArray("data", ModNbtUtils.toBytes(data));
            return new FileAction<>(Type.WRITE, vars, tag -> Unit.INSTANCE);
        }

        public static FileAction<FileInfo> makeWrite(Path file, long offset, byte[] data) {
            CompoundTag vars = new CompoundTag();
            vars.putString("path", file.toString());
            vars.putLong("offset", offset);
            vars.putByteArray("data", data);
            return new FileAction<>(Type.WRITE, vars, FileInfo::fromTag);
        }

        @Deprecated
        public static FileAction<Unit> makeCopyCut(Path source, DataPath destination, boolean override, boolean cut) {
            CompoundTag vars = new CompoundTag();
            vars.putString("source", source.toString());
            vars.putString("destination_drive", destination.drive().toString());
            vars.putString("destination_folder", destination.path().toString());
            vars.putBoolean("override", override);
            vars.putBoolean("cut", cut);
            return new FileAction<>(Type.COPY_CUT, vars, tag -> Unit.INSTANCE);
        }

        public static FileAction<Boolean> makeExists(Path path) {
            CompoundTag vars = new CompoundTag();
            vars.putString("path", path.toString());
            return new FileAction<>(Type.EXISTS, vars, tag -> tag.getBoolean("exists"));
        }

        public static FileAction<byte[]> makeRead(Path path) {
            return makeRead(path, -1L, -1);
        }

        public static FileAction<byte[]> makeRead(Path path, long offset, int length) {
            CompoundTag vars = new CompoundTag();
            vars.putString("path", path.toString());
            vars.putLong("offset", offset);
            vars.putInt("length", length);
            return new FileAction<>(Type.READ, vars, tag -> tag.getByteArray("data"));
        }

        public static FileAction<List<FileInfo>> makeList(Path path) {
            CompoundTag vars = new CompoundTag();
            vars.putString("path", path.toString());
            return new FileAction<>(Type.LIST_DIR, vars, (tag) -> {
                List<FileInfo> list = new ArrayList<>();
                ListTag listTag = tag.getList("files", Tag.TAG_STRING);
                for (Tag tag1 : listTag) {
                    if (!(tag1 instanceof CompoundTag compoundTag)) continue;
                    list.add(FileInfo.fromTag(compoundTag));
                }
                return list;
            });
        }

        public static FileAction<FileInfo> makeInfo(Path path) {
            CompoundTag vars = new CompoundTag();
            vars.putString("path", path.toString());
            return new FileAction<>(Type.INFO, vars, FileInfo::fromTag);
        }

        public static FileAction<Unit> makeMove(Path source, Path destination, boolean override) {
            CompoundTag vars = new CompoundTag();
            vars.putString("source", source.toString());
            vars.putString("destination", destination.toString());
            vars.putBoolean("override", override);
            return new FileAction<>(Type.MOVE, vars, tag -> Unit.INSTANCE);
        }

        public static FileAction<FileInfo> makeCopy(Path source, Path destination, boolean override) {
            CompoundTag vars = new CompoundTag();
            vars.putString("source", source.toString());
            vars.putString("destination", destination.toString());
            vars.putBoolean("override", override);
            return new FileAction<>(Type.COPY, vars, FileInfo::fromTag);
        }

        public static FileAction<CompoundTag> makeExtraInfo(Path path) {
            CompoundTag vars = new CompoundTag();
            vars.putString("path", path.toString());
            return new FileAction<>(Type.EXTRA_INFO, vars, Function.identity());
        }

        public static FileAction<FileInfo> makeNewFolders(Path path) {
            CompoundTag vars = new CompoundTag();
            vars.putString("path", path.toString());
            return new FileAction<>(Type.NEW_FOLDERS, vars, FileInfo::fromTag);
        }
    }
}
