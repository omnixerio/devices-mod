package com.ultreon.devices.core.io;

import com.ultreon.devices.Devices;
import com.ultreon.devices.api.app.Application;
import com.ultreon.devices.api.io.Drive;
import com.ultreon.devices.api.io.Folder;
import com.ultreon.devices.api.task.Callback;
import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.api.task.TaskManager;
import com.ultreon.devices.block.entity.ComputerBlockEntity;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.core.io.action.FileAction;
import com.ultreon.devices.core.io.drive.AbstractDrive;
import com.ultreon.devices.core.io.drive.ExternalDrive;
import com.ultreon.devices.core.io.drive.InternalDrive;
import com.ultreon.devices.core.io.task.TaskGetFiles;
import com.ultreon.devices.core.io.task.TaskGetMainDrive;
import com.ultreon.devices.core.io.task.TaskSendAction;
import com.ultreon.devices.debug.DebugLog;
import com.ultreon.devices.init.DeviceItems;
import com.ultreon.devices.item.FlashDriveItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class FileSystem {
    public static final Pattern PATTERN_FILE_NAME = Pattern.compile("^[\\w'.:_ ]{1,32}$");
    public static final Pattern PATTERN_DIRECTORY = Pattern.compile("^(/)|(/[\\w'.:_ ]{1,32})*$");

    public static final String DIR_ROOT = "/";
    public static final String DIR_APPLICATION_DATA = DIR_ROOT + "Application Data";
    public static final String DIR_HOME = DIR_ROOT + "Home";
    public static final String LAPTOP_DRIVE_NAME = "Root";

    private AbstractDrive mainDrive = null;
    private final Map<UUID, AbstractDrive> additionalDrives = new HashMap<>();
    private AbstractDrive attachedDrive = null;
    private DyeColor attachedDriveColor = DyeColor.WHITE;

    private final ComputerBlockEntity blockEntity;

    public FileSystem(ComputerBlockEntity blockEntity, CompoundTag tag) {
        this.blockEntity = blockEntity;

        load(tag);
    }

    @Environment(EnvType.CLIENT)
    public static void sendAction(Drive drive, FileAction action, @Nullable Callback<Response> callback) {
        if (Laptop.getPos() != null) {
            DebugLog.log("Sending action " + action + " to " + drive);
            Task task = new TaskSendAction(drive, action);
            task.setCallback((tag, success) -> {
                DebugLog.log("Action " + action + " sent to " + drive + ": " + success);
                if (callback != null) {
                    assert tag != null;
                    DebugLog.log("Callback: " + tag.getString("response"));
                    callback.execute(Response.fromTag(tag.getCompound("response")), success);
                }
            });
            TaskManager.sendTask(task);
        } else {
            DebugLog.log("Sending action " + action + " to " + drive + " failed: Laptop not found");
        }
    }

    public static void getApplicationFolder(Application app, Callback<Folder> callback) {
        if (Devices.hasAllowedApplications()) { // in arch we do not do instances
            if (!Devices.getAllowedApplications().contains(app.getInfo())) {
                callback.execute(null, false);
                return;
            }
        }

        if (Laptop.getMainDrive() == null) {
            Task task = new TaskGetMainDrive(Laptop.getPos());
            task.setCallback((tag, success) -> {
                if (success) {
                    setupApplicationFolder(app, callback);
                } else {
                    callback.execute(null, false);
                }
            });

            TaskManager.sendTask(task);
        } else {
            setupApplicationFolder(app, callback);
        }
    }

    private static void setupApplicationFolder(Application app, Callback<Folder> callback) {
        assert Laptop.getMainDrive() != null;
        Folder folder = Laptop.getMainDrive().getFolder(FileSystem.DIR_APPLICATION_DATA);
        if (folder != null) {
            if (folder.hasFolder(app.getInfo().getFormattedId())) {
                Folder appFolder = folder.getFolder(app.getInfo().getFormattedId());
                assert appFolder != null;
                if (appFolder.isSynced()) {
                    callback.execute(appFolder, true);
                } else {
                    Task task = new TaskGetFiles(appFolder, Laptop.getPos());
                    task.setCallback((tag, success) -> {
                        assert tag != null;
                        if (success && tag.contains("files", Tag.TAG_LIST)) {
                            ListTag files = tag.getList("files", Tag.TAG_COMPOUND);
                            appFolder.syncFiles(files);
                            callback.execute(appFolder, true);
                        } else {
                            callback.execute(null, false);
                        }
                    });
                    TaskManager.sendTask(task);
                }
            } else {
                Folder appFolder = new Folder(app.getInfo().getFormattedId());
                folder.add(appFolder, (response, success) -> {
                    if (response != null && response.getStatus() == Status.SUCCESSFUL) {
                        callback.execute(appFolder, true);
                    } else {
                        callback.execute(null, false);
                    }
                });
            }
        } else {
            DebugLog.log("Application data folder is not initialized");
            callback.execute(null, false);
        }
    }

    public static Response createSuccessResponse() {
        return new Response(Status.SUCCESSFUL);
    }

    public static Response createResponse(int status, String message) {
        return new Response(status, message);
    }

    private void load(CompoundTag tag) {
        if (tag.contains("main_drive", Tag.TAG_COMPOUND))
            mainDrive = InternalDrive.fromTag(tag.getCompound("main_drive"));
        if (tag.contains("drives", Tag.TAG_LIST)) {
            ListTag list = tag.getList("drives", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag driveTag = list.getCompound(i);
                AbstractDrive drive = InternalDrive.fromTag(driveTag.getCompound("drive"));
                additionalDrives.put(drive.getUuid(), drive);
            }
        }
        if (tag.contains("external_drive", Tag.TAG_COMPOUND))
            attachedDrive = ExternalDrive.fromTag(tag.getCompound("external_drive"));
        if (tag.contains("external_drive_color", Tag.TAG_BYTE))
            attachedDriveColor = DyeColor.byId(tag.getByte("external_drive_color"));

        setupDefault();
    }

    private void setupDefault() {
        if (mainDrive == null) {
            AbstractDrive drive = new InternalDrive(LAPTOP_DRIVE_NAME);
            ServerFolder root = drive.getRoot(blockEntity.getLevel());
            root.add(createProtectedFolder("Home"), false);
            root.add(createProtectedFolder("Application Data"), false);
            mainDrive = drive;
            blockEntity.setChanged();
        }
    }

    private ServerFolder createProtectedFolder(String name) {
        try {
            Constructor<ServerFolder> constructor = ServerFolder.class.getDeclaredConstructor(String.class, boolean.class);
            constructor.setAccessible(true);
            return constructor.newInstance(name, true);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Response readAction(String driveUuid, FileAction action, Level level) {
        UUID uuid = UUID.fromString(driveUuid);
        AbstractDrive drive = getAvailableDrives(level, true).get(uuid);
        if (drive != null) {
            Response response = drive.handleFileAction(this, action, level);
            if (response.getStatus() == Status.SUCCESSFUL) {
                blockEntity.setChanged();
            }
            return response;
        }
        return createResponse(Status.DRIVE_UNAVAILABLE, "Drive unavailable or missing");
    }

    public AbstractDrive getMainDrive() {
        return mainDrive;
    }

    public Map<UUID, AbstractDrive> getAvailableDrives(@Nullable Level level, boolean includeMain) {
        Map<UUID, AbstractDrive> drives = new LinkedHashMap<>();

        if (includeMain && this.mainDrive != null) drives.put(this.mainDrive.getUuid(), this.mainDrive);

        drives.putAll(this.additionalDrives);

        if (this.attachedDrive != null) drives.put(this.attachedDrive.getUuid(), this.attachedDrive);

        // TODO add network drives
        return drives;
    }

    public AbstractDrive getAttachedDrive() {
        return attachedDrive;
    }

    public DyeColor getAttachedDriveColor() {
        return attachedDriveColor;
    }

    public boolean attachDrive(ItemStack flashDrive) {
        if (flashDrive.getItem() instanceof FlashDriveItem flashDriveItem) {
            if (attachedDrive == null) {
                ExternalDrive drive = getExternalDrive(flashDrive);
                if (drive != null) {
                    drive.setName(flashDrive.getHoverName().getString());
                    attachedDrive = drive;

                    attachedDriveColor = flashDriveItem.getColor();

                    blockEntity.getPipeline().putByte("external_drive_color", (byte) attachedDriveColor.getId());
                    blockEntity.sync();

                    return true;
                }
            }
        }

        return false;
    }

    @Nullable
    public ItemStack detachDrive() {
        if (attachedDrive != null) {
            ItemStack stack = new ItemStack(DeviceItems.getFlashDriveByColor(attachedDriveColor), 1);
            stack.setHoverName(Component.literal(attachedDrive.getName()));
            stack.getOrCreateTag().put("drive", attachedDrive.toTag());
            attachedDrive = null;
            return stack;
        }
        return null;
    }

    public static CompoundTag getExternalDriveTag(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) tag = new CompoundTag();

        if (!tag.contains("drive", Tag.TAG_COMPOUND)) {
            tag.put("drive", new ExternalDrive(stack.getDisplayName().getString()).toTag());
            stack.setTag(tag);
        }
        return tag;
    }

    public static ExternalDrive getExternalDrive(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) tag = new CompoundTag();

        if (!tag.contains("drive", Tag.TAG_COMPOUND)) {
            ExternalDrive externalDrive = new ExternalDrive(stack.getDisplayName().getString());
            tag.put("drive", externalDrive.toTag());
            stack.setTag(tag);
            return externalDrive;
        } else {
            return ExternalDrive.fromTag(tag.getCompound("drive"));
        }
    }

    public CompoundTag toTag() {
        CompoundTag fileSystemTag = new CompoundTag();

        if (mainDrive != null)
            fileSystemTag.put("main_drive", mainDrive.toTag());

        ListTag list = new ListTag();
        additionalDrives.forEach((k, v) -> list.add(v.toTag()));
        fileSystemTag.put("drives", list);

        if (attachedDrive != null) {
            fileSystemTag.put("external_drive", attachedDrive.toTag());
            fileSystemTag.putByte("external_drive_color", (byte) attachedDriveColor.getId());
        }

        return fileSystemTag;
    }

    public static class Response {
        private final int status;
        private String message = "";

        private Response(int status) {
            this.status = status;
        }

        private Response(int status, String message) {
            this.status = status;
            this.message = message;
        }

        public static Response fromTag(CompoundTag responseTag) {
            return new Response(responseTag.getInt("status"), responseTag.getString("message"));
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public CompoundTag toTag() {
            CompoundTag responseTag = new CompoundTag();
            responseTag.putInt("status", status);
            responseTag.putString("message", message);
            return responseTag;
        }
    }

    public static final class Status {
        public static final int FAILED = 0;
        public static final int SUCCESSFUL = 1;
        public static final int FILE_INVALID = 2;
        public static final int FILE_IS_PROTECTED = 3;
        public static final int FILE_EXISTS = 4;
        public static final int FILE_INVALID_NAME = 5;
        public static final int FILE_INVALID_DATA = 6;
        public static final int DRIVE_UNAVAILABLE = 7;
    }
}
