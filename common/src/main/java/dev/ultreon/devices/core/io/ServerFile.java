package dev.ultreon.devices.core.io;

import dev.ultreon.devices.api.app.Application;
import dev.ultreon.devices.api.io.MimeType;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

/**
 * @author MrCrayfish
 */
public sealed class ServerFile permits ServerFolder {
    public static final Comparator<ServerFile> SORT_BY_NAME = (f1, f2) -> {
        if (f1.isFolder() && !f2.isFolder()) return -1;
        if (!f1.isFolder() && f2.isFolder()) return 1;
        return f1.name.compareTo(f2.name);
    };
    private MimeType mimeType = MimeType.APPLICATION_OCTET_STREAM;

    protected ServerFolder parent;
    protected String name;
    protected String openingApp;
    protected CompoundTag data;
    protected boolean protect = false;
    protected long lastModified;
    protected long lastAccessed;
    protected long creationTime;

    protected ServerFile() {

    }

    public ServerFile(String name, Application app, CompoundTag data) {
        this(name, app.getInfo().getFormattedId(), data, false);
    }

    public ServerFile(String name, String openingAppId, CompoundTag data) {
        this(name, openingAppId, data, false);
    }

    public ServerFile(String name, String openingAppId, MimeType mimeType, CompoundTag data) {
        this(name, openingAppId, data, mimeType, false);
    }

    private ServerFile(String name, String openingAppId, CompoundTag data,  boolean protect) {
        this(name, openingAppId, data, MimeType.APPLICATION_OCTET_STREAM, protect);
    }

    private ServerFile(String name, String openingAppId, CompoundTag data, MimeType mimeType, boolean protect) {
        this.name = name;
        this.openingApp = openingAppId;
        this.data = data;
        this.mimeType = mimeType;
        this.protect = protect;
        this.creationTime = System.currentTimeMillis();
        this.lastModified = this.creationTime;
        this.lastAccessed = this.creationTime;
    }

    private ServerFile(String name, boolean protect, CompoundTag tag) {
        this.openingApp = tag.getString("openingApp");
        this.name = name;
        this.data = tag.getCompound("data");
        this.creationTime = tag.getLong("creationTime");
        this.lastModified = tag.getLong("lastModified");
        this.lastAccessed = tag.getLong("lastAccessed");
        this.protect = protect;
        this.mimeType = MimeType.of(tag.getCompound("mimeType"));
    }

    public String getName() {
        return name;
    }

    public FileSystem.Response rename(String name) {
        if (this.protect)
            return FileSystem.createResponse(FileSystem.Status.FILE_IS_PROTECTED, "Cannot rename a protected file");

        if (!FileSystem.PATTERN_FILE_NAME.matcher(name).matches())
            return FileSystem.createResponse(FileSystem.Status.FILE_INVALID_NAME, "Invalid file name");

        this.name = name;
        lastModified = System.currentTimeMillis();
        return FileSystem.createSuccessResponse();
    }

    @Nullable
    public String getOpeningApp() {
        return openingApp;
    }

    public FileSystem.Response setData(CompoundTag data) {
        if (this.protect)
            return FileSystem.createResponse(FileSystem.Status.FILE_IS_PROTECTED, "Cannot set data on a protected file");

        if (data == null)
            return FileSystem.createResponse(FileSystem.Status.FILE_INVALID_DATA, "Invalid data");

        this.data = data;
        lastModified = System.currentTimeMillis();
        return FileSystem.createSuccessResponse();
    }

    @Nullable
    public CompoundTag getData() {
        CompoundTag data1 = data.copy(); // make a copy to prevent modifying the original data
        lastAccessed = System.currentTimeMillis();
        return data1;
    }

    @Nullable
    public ServerFolder getParent() {
        return parent;
    }

    public boolean isProtected() {
        return protect;
    }

    public boolean isFolder() {
        return false;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long getLastModified() {
        return lastModified;
    }

    public long getLastAccessed() {
        return lastAccessed;
    }

    public boolean isForApplication(Application app) {
        return openingApp != null && openingApp.equals(app.getInfo().getFormattedId());
    }

    public FileSystem.Response delete() {
        if (this.protect)
            return FileSystem.createResponse(FileSystem.Status.FILE_IS_PROTECTED, "Cannot delete a protected file");

        if (parent != null)
            return parent.delete(this);

        return FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Invalid file");
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("openingApp", openingApp);
        tag.putLong("lastModified", lastModified);
        tag.putLong("lastAccessed", lastAccessed);
        tag.putLong("creationTime", creationTime);
        tag.put("data", data);
        return tag;
    }

    public static ServerFile fromTag(String name, CompoundTag tag) {
        return new ServerFile(name, false, tag);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof ServerFile))
            return false;
        return ((ServerFile) obj).name.equalsIgnoreCase(name);
    }

    public ServerFile copy() {
        return new ServerFile(name, openingApp, data.copy());
    }

    void onCreate() {
        creationTime = System.currentTimeMillis();
    }
}
