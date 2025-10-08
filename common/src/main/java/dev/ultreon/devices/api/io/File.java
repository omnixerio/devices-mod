package dev.ultreon.devices.api.io;

import dev.ultreon.devices.api.app.Application;
import dev.ultreon.devices.api.task.Callback;
import dev.ultreon.devices.core.DataPath;
import dev.ultreon.devices.core.io.FileSystem;
import dev.ultreon.devices.core.io.Path;
import dev.ultreon.devices.core.io.action.FileAction;
import dev.ultreon.devices.programs.system.component.FileBrowser;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Comparator;
import java.util.Objects;

@Deprecated
@SuppressWarnings("unused")
public class File {
    /// Comparator to sort the file list by alphabetical order. Folders are brought to the top.
    @Deprecated
    public static final Comparator<File> SORT_BY_NAME = (f1, f2) -> {
        if (f1.isFolder() && !f2.isFolder()) return -1;
        if (!f1.isFolder() && f2.isFolder()) return 1;
        return f1.name.compareTo(f2.name);
    };

    protected Drive drive;
    protected Folder parent;
    protected String name;
    protected String openingApp;
    protected CompoundTag data;
    protected boolean protect = false;
    protected boolean valid = false;

    protected File() {

    }

    /// The standard constructor for a file
    ///
    /// @param name the name of the file
    /// @param app  the application that is opening the file
    /// @param data the data of the file
    @Deprecated
    public File(String name, Application app, CompoundTag data) {
        this(name, app.getInfo().getFormattedId(), data, false);
    }

    /// The alternate constructor for a file. This second constructor allows the specification of
    /// an application identifier. This allows the creation of files for different applications. You
    /// should know the format of the target file if you are using this constructor
    ///
    /// @param name         the name of the file
    /// @param openingAppId the application identifier of the application that is opening the file
    /// @param data         the data of the file
    @Deprecated
    public File(String name, String openingAppId, CompoundTag data) {
        this(name, openingAppId, data, false);
    }

    private File(String name, String openingAppId, CompoundTag data, boolean protect) {
        this.name = name;
        this.openingApp = openingAppId;
        this.data = data;
        this.protect = protect;
    }

    /// Gets the name of the file
    ///
    /// @return the file name
    @Deprecated
    public String getName() {
        return name;
    }

    /// Renames the file with the specified name. This method is asynchronous, so the name will not
    /// be set immediately. It will ignore if the rename failed. Use
    /// [#rename(String,Callback)] instead if you need to know it that it successfully
    /// renamed the file.
    ///
    /// @param name the new file name
    @Deprecated
    public void rename(String name) {
        rename(name, null);
    }

    /// Renames the file with the specified name and allows a callback to be specified. This method
    /// is asynchronous, so the name will not be set immediately. The callback is fired when the file
    /// has been renamed, however it is not necessarily successful.
    ///
    /// @param name the new file name
    @Deprecated
    public void rename(String name, @Nullable Callback<FileSystem.Response> callback) {
//        if (!valid)
//            throw new IllegalStateException("File must be added to the system before you can rename it");
//
//        if (protect) {
//            if (callback != null) {
//                callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_IS_PROTECTED, "Cannot rename a protected file"), false);
//            }
//            return;
//        }
//
//        if (!FileSystem.PATTERN_FILE_NAME.matcher(name).matches()) {
//            if (callback != null) {
//                callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_INVALID_NAME, "Invalid file name"), true);
//            }
//            return;
//        }
//
//        FileSystem.sendAction(drive, FileAction.Factory.makeRename(fi, name), (response, success) -> {
//            if (success) {
//                this.name = name;
//            }
//            if (callback != null) {
//                callback.execute(response, success);
//            }
//        });
    }

    /// Gets the path of this file. The path is the set of all the folders needed to traverse in
    /// order to find the folder this file is contained within and appends the file's name at the
    /// end. This is different to [#getLocation()] which does not append the file's name.
    ///
    /// @return the path of the file
    @Deprecated
    public String getPath() {
        if (parent == null)
            return "/";

        StringBuilder builder = new StringBuilder();

        File current = this;
        while (current != null) {
            if (current.getParent() == null) break;
            builder.insert(0, "/" + current.getName());
            current = current.getParent();
        }
        return builder.toString();
    }

    /// Gets the location of this file. The location is the set of folders needed to traverse in
    /// order to find the folder this file is contained within. This is different to
    /// [#getPath()] and does not include the file name on the end.
    ///
    /// @return the location of the file
    @Deprecated
    public String getLocation() {
        if (parent == null)
            throw new NullPointerException("File must have a parent to compile the directory");

        StringBuilder builder = new StringBuilder();

        Folder current = parent;
        while (current != null) {
            if (current.getParent() == null) break;
            builder.insert(0, "/" + current.getName());
            current = current.getParent();
        }
        return builder.toString();
    }

    /// Gets the application this file can be open with.
    ///
    /// @return the application identifier
    @Nullable
    @Deprecated
    public String getOpeningApp() {
        return openingApp;
    }

    /// Sets the data for the file. This method is asynchronous, so data will not be set immediately.
    ///
    /// @param data the data for the file
    @Deprecated
    public void setData(CompoundTag data) {
        setData(data, null);
    }

    /// Sets the data for the file and allows a callback to be specified. This method is
    /// asynchronous, so data will not be set immediately. The callback is fired when the data is
    /// set, however it is not necessarily successful.
    ///
    /// @param data     the data for the file
    /// @param callback the callback to be fired when the data is set
    @Deprecated
    public void setData(CompoundTag data, @Nullable Callback<FileSystem.Response> callback) {
        if (!valid)
            throw new IllegalStateException("File must be added to the system before you can rename it");

        if (protect) {
            if (callback != null) {
                callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_IS_PROTECTED, "Cannot set data on a protected file"), false);
            }
            return;
        }

        if (data == null) {
            if (callback != null) {
                callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_INVALID_DATA, "Invalid data"), false);
            }
            return;
        }

        try {
            FileSystem.sendAction(drive.getUUID(), FileAction.Factory.makeData(Path.of(getPath()), data), (response, success) -> {
                if (success) {
                    this.data = data.copy();
                }
                if (callback != null) {
                    callback.execute(response, success);
                }
            });
        } catch (IOException e) {
            if (callback != null) {
                callback.execute(FileSystem.createResponse(FileSystem.Status.FAILED, "Unknown error occurred"), false);
            }
        }
    }

    /// Gets the data of this file. The data you receive is a copied version. If you want to update
    /// it, use [#setData(CompoundTag,Callback)] to do so.
    ///
    /// @return the file's data
    @Nullable
    @Deprecated
    public CompoundTag getData() {
        return data.copy();
    }

    @Deprecated
    public byte[] getDataBytes() {
        byte[] bytes = new byte[data.getByteArray("data").length];
        System.arraycopy(data.getByteArray("data"), 0, bytes, 0, bytes.length);
        return bytes;
    }

    /// Gets the [Folder] this file is contained in.
    ///
    /// @return the parent of this file
    @Nullable
    @Deprecated
    public Folder getParent() {
        return parent;
    }

    /// Gets the drive this file belongs to.
    ///
    /// @return the drive this file is contained in
    @Deprecated
    public Drive getDrive() {
        return drive;
    }

    /// Sets the drive for this file.
    ///
    /// @param drive the drive this file is contained in
    void setDrive(Drive drive) {
        this.drive = drive;
    }

    /// Gets the protected flag of this file.
    ///
    /// @return the protected flag
    @Deprecated
    public boolean isProtected() {
        return protect;
    }

    /// Gets whether this file is actually folder
    ///
    /// @return is this file is a folder
    @Deprecated
    public boolean isFolder() {
        return false;
    }

    /// Determines if this file is for the specified application. This helps identify files that are
    /// designed for the specified application. Useful in filtering out files in a list.
    ///
    /// @param app the application to test against
    /// @return if this file is for the application
    @Deprecated
    public boolean isForApplication(Application app) {
        return openingApp != null && openingApp.equals(app.getInfo().getFormattedId());
    }

    /// Deletes this file from the folder its contained in. This method does not specify a callback,
    /// so any errors occurred will not be reported.
    @Deprecated
    public void delete() {
        delete(null);
    }

    /// Deletes this file from the folder its contained in. This method allows the specification of a
    /// callback and will tell if deleted successfully or not.
    ///
    /// @param callback the callback
    @Deprecated
    public void delete(@Nullable Callback<FileSystem.Response> callback) {
        if (!valid)
            throw new IllegalStateException("File must be added to the system before you can rename it");

        if (this.protect) {
            if (callback != null) {
                callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_IS_PROTECTED, "Cannot delete a protected file"), false);
            }
            return;
        }

        if (parent != null) {
            parent.delete(this, callback);
        }
    }

    @Deprecated
    public void copyTo(Folder destination, boolean override, @Nullable Callback<FileSystem.Response> callback) {
        if (destination == null) {
            if (callback != null) {
                callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Illegal folder"), false);
            }
            return;
        }

        if (!destination.valid || destination.drive == null) {
            if (callback != null) {
                callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Destination folder is invalid"), false);
            }
            return;
        }

        if (!valid || drive == null) {
            if (callback != null) {
                callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Source file is invalid"), false);
            }
            return;
        }

        if (destination.hasFile(name)) {
            if (!override) {
                if (callback != null) {
                    callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_EXISTS, "A file with that name already exists"), false);
                }
                return;
            } else if (Objects.requireNonNull(destination.getFile(name)).isProtected()) {
                if (callback != null) {
                    callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_IS_PROTECTED, "Unable to override protected files"), false);
                }
                return;
            }
        }

        FileSystem.sendAction(drive.getUUID(), FileAction.Factory.makeCopy(Path.of(getPath()), Path.of(destination.getPath()), override), (response, success) -> {
            assert response != null;
            if (response.getStatus() == FileSystem.Status.SUCCESSFUL) {
                if (override) {
                    destination.files.remove(destination.getFile(name));
                }
                File file = copy();
                file.valid = true;
                file.parent = destination;
                file.setDrive(destination.drive);
                destination.files.add(file);
                FileBrowser.refreshList = true;
            }
            if (callback != null) {
                callback.execute(response, success);
            }
        });
    }

    @Deprecated
    public void moveTo(Folder destination, boolean override, @Nullable Callback<FileSystem.Response> callback) {
        if (destination == null) {
            if (callback != null) {
                callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Illegal folder"), false);
            }
            return;
        }

        if (!destination.valid || destination.drive == null) {
            if (callback != null) {
                callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Destination folder is invalid"), false);
            }
            return;
        }

        if (!valid || drive == null) {
            if (callback != null) {
                callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_INVALID, "Source file is invalid"), false);
            }
            return;
        }

        if (this.equals(destination.getFile(name))) {
            if (callback != null) {
                callback.execute(FileSystem.createSuccessResponse(), false);
            }
            return;
        }

        if (destination.hasFile(name)) {
            if (!override) {
                if (callback != null) {
                    callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_EXISTS, "A file with that name already exists"), false);
                }
                return;
            } else if (Objects.requireNonNull(destination.getFile(name)).isProtected()) {
                if (callback != null) {
                    callback.execute(FileSystem.createResponse(FileSystem.Status.FILE_IS_PROTECTED, "Unable to override protected files"), false);
                }
                return;
            }
        }

        FileSystem.sendAction(drive.getUUID(), FileAction.Factory.makeCopyCut(Path.of(getPath()), new DataPath(destination.drive.getUUID(), Path.of(destination.getPath())), override, true), (response, success) -> {
            assert response != null;
            if (response.getStatus() == FileSystem.Status.SUCCESSFUL) {
                if (override) {
                    destination.files.remove(destination.getFile(name));
                }
                parent.files.remove(this);
                setDrive(destination.drive);
                parent = destination;
                destination.files.add(this);
                FileBrowser.refreshList = true;
            }
            if (callback != null) {
                callback.execute(response, success);
            }
        });
    }

    /// Converts this file into a tag compound. Due to how the file system works, this tag does not
    /// include the name of the file and will have to be set manually for any storage.
    ///
    /// @return the file tag
    @Deprecated
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("openingApp", openingApp);
        tag.put("data", data);
        return tag;
    }

    /// Converts a tag compound to a file instance.
    ///
    /// @param name the name of the file
    /// @param tag  the tag compound from [#toTag()]
    /// @return a file instance
    @Deprecated
    public static File fromTag(String name, CompoundTag tag) {
        return new File(name, tag.getString("openingApp"), tag.getCompound("data"));
    }

    @Override
    @Deprecated
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof File file))
            return false;
        return parent == file.parent && name.equalsIgnoreCase(file.name);
    }

    /// Returns a copy of this file. The copied file is considered invalid and changes to it can not
    /// be made until it is added into the file system.
    ///
    /// @return copy of this file
    @Deprecated
    public File copy() {
        return new File(name, openingApp, data.copy());
    }

    /// Returns a copy of this file with a different name. The copied file is considered invalid and
    /// changes to it can not be made until it is added into the file system.
    ///
    /// @param newName the new name for the file
    /// @return copy of this file
    @Deprecated
    public File copy(String newName) {
        return new File(newName, openingApp, data.copy());
    }
}
