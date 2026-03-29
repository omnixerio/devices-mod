package dev.ultreon.devices.core.io;

import dev.ultreon.devices.UltreonDevicesCommon;
import dev.ultreon.devices.api.app.Application;
import dev.ultreon.devices.api.io.Drive;
import dev.ultreon.devices.api.io.Folder;
import dev.ultreon.devices.api.task.Callback;
import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.api.task.TaskManager;
import dev.ultreon.devices.block.entity.ComputerBlockEntity;
import dev.ultreon.devices.core.Laptop;
import dev.ultreon.devices.core.io.action.FileAction;
import dev.ultreon.devices.core.io.drive.AbstractDrive;
import dev.ultreon.devices.core.io.drive.ExternalDrive;
import dev.ultreon.devices.core.io.drive.InternalDrive;
import dev.ultreon.devices.core.io.task.TaskGetFiles;
import dev.ultreon.devices.core.io.task.TaskGetMainDrive;
import dev.ultreon.devices.core.io.task.TaskSendAction;
import dev.ultreon.devices.debug.DebugLog;
import dev.ultreon.devices.init.DeviceItems;
import dev.ultreon.devices.item.DeviceDataComponents;
import dev.ultreon.devices.item.DriveComponent;
import dev.ultreon.devices.item.FlashDriveItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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

    public static void sendAction(Drive drive, FileAction action, @Nullable Callback<Response> callback) {
        if (Laptop.getPos() != null) {
            DebugLog.log("Sending action " + action + " to " + drive);
            Task task = new TaskSendAction(drive, action);
            task.setCallback((tag, success) -> {
                DebugLog.log("Action " + action + " sent to " + drive + ": " + success);
                if (callback != null) {
                    assert tag != null;
                    DebugLog.log("Callback: " + tag.getString("response").orElse(null));
                    callback.execute(Response.fromTag(tag.getCompoundOrEmpty("response")), success);
                }
            });
            TaskManager.sendTask(task);
        } else {
            DebugLog.log("Sending action " + action + " to " + drive + " failed: Laptop not found");
        }
    }

    public static void getApplicationFolder(Application app, Callback<Folder> callback) {
        if (UltreonDevicesCommon.hasAllowedApplications()) { // in arch we do not do instances
            if (!UltreonDevicesCommon.getAllowedApplications().contains(app.getInfo())) {
                callback.execute(null, false);
                return;
            }
        }

        if (Laptop.getMainDrive() == null) {
            Task task = new TaskGetMainDrive(Laptop.getPos());
            task.setCallback((_, success) -> {
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
                        if (success && tag.contains("files")) {
                            ListTag files = tag.getListOrEmpty("files");
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
                folder.add(appFolder, (response, _) -> {
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
        if (tag.contains("main_drive"))
            mainDrive = InternalDrive.fromTag(tag.getCompoundOrEmpty("main_drive"));
        if (tag.contains("drives")) {
            ListTag list = tag.getListOrEmpty("drives");
            for (int i = 0; i < list.size(); i++) {
                CompoundTag driveTag = list.getCompoundOrEmpty(i);
                AbstractDrive drive = InternalDrive.fromTag(driveTag.getCompoundOrEmpty("drive"));
                additionalDrives.put(drive.getUuid(), drive);
            }
        }
        if (tag.contains("external_drive"))
            attachedDrive = ExternalDrive.fromTag(tag.getCompoundOrEmpty("external_drive"));
        if (tag.contains("external_drive_color"))
            attachedDriveColor = DyeColor.byId(tag.getByteOr("external_drive_color", (byte) 0));

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

    public Map<UUID, AbstractDrive> getAvailableDrives(@Nullable Level ignoredLevel, boolean includeMain) {
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
            stack.set(DataComponents.CUSTOM_NAME, Component.literal(attachedDrive.getName()));
            stack.set(DeviceDataComponents.DRIVE.get(), new DriveComponent(attachedDrive.toTag()));
            attachedDrive = null;
            return stack;
        }
        return null;
    }

    public static CompoundTag getExternalDriveTag(ItemStack stack) {
        if (!stack.has(DeviceDataComponents.DRIVE.get())) {
            DriveComponent driveComponent = new DriveComponent(new ExternalDrive(stack.getDisplayName().getString()).toTag());
            stack.set(DeviceDataComponents.DRIVE.get(), driveComponent);
        }

        return stack.get(DeviceDataComponents.DRIVE.get()).tag().asCompound().orElse(new CompoundTag());
    }

    public static ExternalDrive getExternalDrive(ItemStack stack) {
        if (!stack.has(DeviceDataComponents.DRIVE.get())) {
            ExternalDrive externalDrive = new ExternalDrive(stack.getDisplayName().getString());
            stack.set(DataComponents.CUSTOM_NAME, Component.literal(externalDrive.getName()));
            stack.set(DeviceDataComponents.DRIVE.get(), new DriveComponent(externalDrive.toTag()));
            return externalDrive;
        }
        return ExternalDrive.fromTag(stack.get(DeviceDataComponents.DRIVE.get()).tag().asCompound().orElse(new CompoundTag()));
    }

    public CompoundTag toTag() {
        CompoundTag fileSystemTag = new CompoundTag();

        if (mainDrive != null)
            fileSystemTag.put("main_drive", mainDrive.toTag());

        ListTag list = new ListTag();
        additionalDrives.forEach((_, v) -> list.add(v.toTag()));
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
            return new Response(responseTag.getIntOr("status", 0), responseTag.getString("message").orElse(null));
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
