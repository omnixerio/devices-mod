package dev.ultreon.devices.core.io;

import dev.ultreon.devices.Devices;
import dev.ultreon.devices.api.app.Application;
import dev.ultreon.devices.api.io.Drive;
import dev.ultreon.devices.api.io.FSResponse;
import dev.ultreon.devices.api.task.Callback;
import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.api.task.TaskManager;
import dev.ultreon.devices.block.entity.computer.ComputerBlockEntity;
import dev.ultreon.devices.block.entity.DriveInfo;
import dev.ultreon.devices.core.DeviceFSException;
import dev.ultreon.devices.core.DriveManager;
import dev.ultreon.devices.core.Ext2FS;
import dev.ultreon.devices.core.ComputerScreen;
import dev.ultreon.devices.core.io.action.FileAction;
import dev.ultreon.devices.core.io.drive.AbstractDrive;
import dev.ultreon.devices.core.io.drive.ExternalDrive;
import dev.ultreon.devices.core.io.drive.InternalDrive;
import dev.ultreon.devices.core.io.task.TaskGetMainDrive;
import dev.ultreon.devices.core.io.task.TaskSendAction;
import dev.ultreon.devices.debug.DebugLog;
import dev.ultreon.devices.init.DeviceDataComponents;
import dev.ultreon.devices.init.DeviceItems;
import dev.ultreon.devices.item.FlashDriveItem;
import dev.ultreon.devices.programs.system.component.FileInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class FileSystem {
    public static final Pattern PATTERN_FILE_NAME = Pattern.compile("^[\\w'.:_ ]{1,32}$");
    public static final Pattern PATTERN_DIRECTORY = Pattern.compile("^(/)|(/[\\w'.:_ ]{1,32})*$");

    public static final Path DIR_ROOT = Path.of("/");
    public static final Path DIR_APPLICATION_DATA = Path.of("/ApplicationData");
    public static final Path DIR_HOME = Path.of("/Home");
    public static final String LAPTOP_DRIVE_NAME = "Main";

    private AbstractDrive mainDrive = null;
    private final Map<UUID, AbstractDrive> additionalDrives = new HashMap<>();
    private AbstractDrive attachedDrive = null;
    private DyeColor attachedDriveColor = DyeColor.WHITE;

    private final ComputerBlockEntity blockEntity;

    public FileSystem(ComputerBlockEntity blockEntity) {
        this.blockEntity = blockEntity;

        setupDefault();
    }

    public FileSystem(ComputerBlockEntity blockEntity, CompoundTag tag) {
        this.blockEntity = blockEntity;

        load(tag);
    }

    @Deprecated
    public static void sendAction(UUID drive, FileAction<?> action, @Nullable Callback<Response> callback) {
        if (ComputerScreen.getPos() != null) {
            DebugLog.log("Sending action " + action + " to " + drive);
            Task task = new TaskSendAction(drive, action);
            task.setCallback((tag, success) -> {
                DebugLog.log("Action " + action + " sent to " + drive + ": " + success);
                if (callback != null) {
                    assert tag != null;
                    Tag response = tag.get("response");
                    DebugLog.log("Callback: " + (response == null ? "null" : response));
                    callback.execute(Response.fromTag(tag.getCompound("response")), success);
                }
            });
            TaskManager.sendTask(task);
        } else {
            DebugLog.log("Sending action " + action + " to " + drive + " failed: Laptop not found");
        }
    }

    public static <T> void request(UUID drive, FileAction<T> action, @Nullable Consumer<FSResponse<T>> callback) {
        if (ComputerScreen.getPos() != null) {
            DebugLog.log("Sending action " + action + " to " + drive);
            Task task = new TaskSendAction(drive, action);
            task.setCallback((tag, success) -> {
                DebugLog.log("Action " + action + " sent to " + drive + ": " + success);
                if (callback != null) {
                    assert tag != null;
                    CompoundTag  response = tag.getCompound("response");
                    DebugLog.log("Callback: " + response);
                    int status = response.contains("status", Tag.TAG_INT) ? response.getInt("status") : Status.FAILED;
                    if (status != Status.SUCCESSFUL) {
                        success = false;
                    }
                    callback.accept(new FSResponse<T>(success, status, success ? action.deserialize(response.getCompound("data")) : null, response.getString("message")));
                }
            });
            TaskManager.sendTask(task);
        } else {
            DebugLog.log("Sending action " + action + " to " + drive + " failed: Laptop not found");
        }
    }

    public static void getApplicationFolder(Application app, Consumer<FSResponse<FileInfo>> callback) {
        if (Devices.hasAllowedApplications() && !Devices.getAllowedApplications().contains(app.getInfo())) {
            callback.accept(new FSResponse<>(false, Status.ACCESS_DENIED, null, "Application not allowed"));
            return;
        }

        if (ComputerScreen.getMainDrive() == null) {
            Task task = new TaskGetMainDrive(ComputerScreen.getPos());
            task.setCallback((tag, success) -> {
                if (success) setupApplicationFolder(app, callback);
                else callback.accept(new FSResponse<>(false, Status.DRIVE_UNAVAILABLE, null, "Drive unavailable"));
            });

            TaskManager.sendTask(task);
        } else {
            setupApplicationFolder(app, callback);
        }
    }

    private static void setupApplicationFolder(Application app, Consumer<FSResponse<FileInfo>> callback) {
        Drive mainDrive = ComputerScreen.getMainDrive();
        assert mainDrive != null;
        mainDrive.info(FileSystem.DIR_APPLICATION_DATA.resolve(app.getInfo().getFormattedId()), (info) -> mainDrive.exists(FileSystem.DIR_APPLICATION_DATA.resolve(app.getInfo().getFormattedId()), (appDirExist) -> {
            if (!appDirExist.data()) {
                info.data().createDirectory(app.getInfo().getFormattedId(), callback);
                return;
            }

            callback.accept(new FSResponse<>(false, Status.FAILED, null, "Application folder still does not exist"));
        }));
    }

    public static Response createSuccessResponse() {
        return new Response(Status.SUCCESSFUL);
    }

    public static Response createResponse(int status, String message) {
        return new Response(status, message);
    }

    public static Response createResponse(int status, String message, CompoundTag data) {
        return new Response(status, message, data);
    }

    public static UUID getMainDriveId() {
        if (ComputerScreen.getMainDrive() != null)
            return ComputerScreen.getMainDrive().getUUID();
        return null;
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
            mainDrive = new InternalDrive(LAPTOP_DRIVE_NAME);
            blockEntity.setChanged();
        }
    }

    @Deprecated
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

    private boolean createSystemDirectory(String name) {
        try {
            Ext2FS fs = mainDrive.getFS();
            Path path = Path.of(name);
            fs.createDirectory(path);
            fs.setReadOnly(path, true);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
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
            stack.set(DataComponents.CUSTOM_NAME, Component.literal(attachedDrive.getName()));
            stack.set(DeviceDataComponents.DISK.get(), attachedDrive.getUuid());
            attachedDrive = null;
            return stack;
        }
        return null;
    }

    @Deprecated
    public static CompoundTag getExternalDriveTag(ItemStack stack) {
        if (!stack.has(DeviceDataComponents.DISK.get())) {
            ExternalDrive externalDrive = new ExternalDrive(stack.getDisplayName().getString());
            stack.set(DeviceDataComponents.DISK.get(), externalDrive.getUuid());
        }
        return new CompoundTag();
    }

    public static UUID getExternalDriveId(ItemStack stack) {
        if (!stack.has(DeviceDataComponents.DISK.get())) {
            ExternalDrive externalDrive = new ExternalDrive(stack.getDisplayName().getString());
            stack.set(DeviceDataComponents.DISK.get(), externalDrive.getUuid());
            return externalDrive.getUuid();
        }
        return stack.get(DeviceDataComponents.DISK.get());
    }

    public static ExternalDrive getExternalDrive(ItemStack stack) {
        if (stack.has(DeviceDataComponents.DISK.get())) {
            UUID uuid = stack.get(DeviceDataComponents.DISK.get());
            return DriveManager.getExternalDrive(uuid);
        }

        ExternalDrive externalDrive = new ExternalDrive(stack.getDisplayName().getString());
        try {
            DriveManager.registerExternalDrive(externalDrive);
        } catch (IOException e) {
            throw new DeviceFSException("Failed to register external drive", e);
        }
        stack.set(DeviceDataComponents.DISK.get(), externalDrive.getUuid());
        return externalDrive;
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

    public Map<UUID, DriveInfo> getDrives() {
        Map<UUID, DriveInfo> drives = new HashMap<>();
        if (mainDrive != null) drives.put(mainDrive.getUuid(), new DriveInfo(mainDrive.getName(), mainDrive.getUuid(), Drive.Type.INTERNAL, true));
        additionalDrives.forEach((k, v) -> drives.put(v.getUuid(), new DriveInfo(v.getName(), v.getUuid(), Drive.Type.INTERNAL)));
        if (attachedDrive != null) drives.put(attachedDrive.getUuid(), new DriveInfo(attachedDrive.getName(), attachedDrive.getUuid(), Drive.Type.EXTERNAL));
        return drives;
    }

    public static class Response {
        private final int status;
        private String message = "";
        private CompoundTag data = new CompoundTag();

        private Response(int status) {
            this.status = status;
        }

        private Response(int status, String message) {
            this.status = status;
            this.message = message;
        }

        private Response(int status, String message, CompoundTag data) {
            this.status = status;
            this.message = message;
            this.data = data;
        }

        public static Response fromTag(CompoundTag responseTag) {
            return new Response(responseTag.getInt("status"), responseTag.getString("message"), responseTag.getCompound("data"));
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public CompoundTag getData() {
            return data;
        }

        public CompoundTag toTag() {
            CompoundTag responseTag = new CompoundTag();
            responseTag.putInt("status", status);
            responseTag.putString("message", message);
            responseTag.put("data", data);
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
        public static final int ACCESS_DENIED = 8;
        public static final int TOO_LARGE = 9;
        public static final int FILE_DOES_NOT_EXIST = 10;
    }
}
