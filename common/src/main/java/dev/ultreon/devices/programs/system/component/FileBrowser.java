package dev.ultreon.devices.programs.system.component;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.ultreon.devices.api.ApplicationManager;
import dev.ultreon.devices.api.app.Component;
import dev.ultreon.devices.api.app.Dialog;
import dev.ultreon.devices.api.app.*;
import dev.ultreon.devices.api.app.component.Button;
import dev.ultreon.devices.api.app.component.Label;
import dev.ultreon.devices.api.app.component.*;
import dev.ultreon.devices.api.app.listener.ItemClickListener;
import dev.ultreon.devices.api.app.renderer.ListItemRenderer;
import dev.ultreon.devices.api.io.Drive;
import dev.ultreon.devices.api.task.Callback;
import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.api.task.TaskManager;
import dev.ultreon.devices.api.utils.RenderUtil;
import dev.ultreon.devices.core.ComputerScreen;
import dev.ultreon.devices.core.Window;
import dev.ultreon.devices.core.Wrappable;
import dev.ultreon.devices.core.io.FileSystem;
import dev.ultreon.devices.core.io.Path;
import dev.ultreon.devices.core.io.task.TaskSetupFileBrowser;
import dev.ultreon.devices.object.AppInfo;
import dev.ultreon.devices.programs.system.SystemApp;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.lang.System;
import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/// Created by Casey on 20-Jun-17.
@SuppressWarnings("FieldCanBeLocal")
public class FileBrowser extends Component {
    private static final ResourceLocation ASSETS = ResourceLocation.parse("devices:textures/gui/file_browser.png");

    private static final Color HEADER_BACKGROUND = Color.decode("0x535861");
    private static final Color ITEM_BACKGROUND = Color.decode("0x9E9E9E");
    private static final Color ITEM_SELECTED = Color.decode("0x757575");
    private static final Color PROTECTED_FILE = new Color(155, 237, 242);

    private static final ListItemRenderer<FileInfo> ITEM_RENDERER = new ListItemRenderer<>(18) {
        @Override
        public void render(GuiGraphics graphics, FileInfo file, Minecraft mc, int x, int y, int width, int height, boolean selected) {
            Color bgColor = new Color(ComputerScreen.getSystem().getSettings().getColorScheme().getBackgroundColor(), true);
            graphics.fill(x, y, x + width, y + height, selected ? bgColor.brighter().brighter().getRGB() : bgColor.brighter().getRGB());

            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            RenderSystem.setShaderTexture(0, ASSETS);
            if (file.isFolder()) {
                RenderUtil.drawRectWithTexture(ASSETS, graphics, x + 3, y + 2, 0, 0, 14, 14, 14, 14);
            } else {
                if (file.getOpeningApp() == null) return;
                AppInfo info = ApplicationManager.getApplication(ResourceLocation.tryParse(file.getOpeningApp()));
                RenderUtil.drawApplicationIcon(graphics, info, x + 3, y + 2);
            }
            graphics.drawString(Minecraft.getInstance().font, file.getName(), x + 22, y + 5, file.protectedFile() ? PROTECTED_FILE.getRGB() : ComputerScreen.getSystem().getSettings().getColorScheme().getTextColor());
        }
    };

    public static boolean refreshList = false;

    private final Wrappable wrappable;
    private final Mode mode;

    private Layout layoutMain;
    private ItemList<FileInfo> fileList;
    private Button btnPreviousFolder;
    private Button btnNewFolder;
    private Button btnRename;
    private Button btnCopy;
    private Button btnCut;
    private Button btnPaste;
    private Button btnDelete;

    private ComboBox.List<Drive> comboBoxDrive;
    private Label labelPath;

    private Layout layoutLoading;
    private Spinner spinnerLoading;

    private final Stack<FileInfo> history = new Stack<>();

    private FileInfo currentPath;

    private FileInfo clipboardFile;
    private FileInfo clipboardDir;

    private Path initialFolder = FileSystem.DIR_ROOT;
    private boolean loadedStructure = false;

    private long lastClick = 0;

    private ItemClickListener<FileInfo> itemClickListener;

    private Predicate<FileInfo> filter;
    private final Path path = Path.of("/");

    /// The default constructor for a component. For your component to
    /// be laid out correctly, make sure you use the x and y parameters
    /// from [#init(CompoundTag)] and pass them into the
    /// x and y arguments of this constructor.
    ///
    /// Laying out the components is a simple relative positioning. So for left (x position),
    /// specific how many pixels from the left of the application window you want
    /// it to be positioned at. The top is the same, but obviously from the top (y position).
    ///
    /// @param left how many pixels from the left
    /// @param top  how many pixels from the top
    public FileBrowser(int left, int top, Wrappable wrappable, Mode mode) {
        super(left, top);
        this.wrappable = wrappable;
        this.mode = mode;
    }

    @Override
    public void init(Layout layout) {
        layoutMain = new Layout(mode.getWidth(), mode.getHeight());
        layoutMain.setBackground((graphics, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
            Color color = new Color(ComputerScreen.getSystem().getSettings().getColorScheme().getHeaderColor());
            graphics.fill(x, y, x + width, y + 20, color.getRGB());
            graphics.fill(x, y + 20, x + width, y + 21, color.darker().getRGB());
        });

        btnPreviousFolder = new Button(5, 2, Icons.ARROW_LEFT);
        btnPreviousFolder.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                goToPreviousFolder();
            }
        });
        btnPreviousFolder.setToolTip("Previous Folder", "Go back to the previous folder");
        btnPreviousFolder.setEnabled(false);
        layoutMain.addComponent(btnPreviousFolder);

        int btnIndex = 0;

        btnNewFolder = new Button(5, 25, Icons.NEW_FOLDER);
        btnNewFolder.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                createFolder();
            }
        });
        btnNewFolder.setEnabled(false);
        btnNewFolder.setToolTip("New Folder", "Creates a new folder in this directory");
        layoutMain.addComponent(btnNewFolder);

        btnIndex++;

        btnRename = new Button(5, 25 + btnIndex * 20, Icons.RENAME);
        btnRename.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                renameSelectedFile();
            }
        });
        btnRename.setToolTip("Rename", "Change the name of the selected file or folder");
        btnRename.setEnabled(false);
        layoutMain.addComponent(btnRename);

        if (mode == Mode.FULL) {
            btnIndex++;

            btnCopy = new Button(5, 25 + btnIndex * 20, Icons.COPY);
            btnCopy.setClickListener((mouseX, mouseY, mouseButton) -> {
                if (mouseButton == 0) {
                    setClipboardFileToSelected();
                }
            });
            btnCopy.setToolTip("Copy", "Copies the selected file or folder");
            btnCopy.setEnabled(false);
            layoutMain.addComponent(btnCopy);

            btnIndex++;

            btnCut = new Button(5, 25 + btnIndex * 20, Icons.CUT);
            btnCut.setClickListener((mouseX, mouseY, mouseButton) -> {
                if (mouseButton == 0) {
                    cutSelectedFile();
                }
            });
            btnCut.setToolTip("Cut", "Cuts the selected file or folder");
            btnCut.setEnabled(false);
            layoutMain.addComponent(btnCut);

            btnIndex++;

            btnPaste = new Button(5, 25 + btnIndex * 20, Icons.CLIPBOARD);
            btnPaste.setClickListener((mouseX, mouseY, mouseButton) -> {
                if (mouseButton == 0) {
                    pasteClipboardFile();
                }
            });
            btnPaste.setToolTip("Paste", "Pastes the copied file into this directory");
            btnPaste.setEnabled(false);
            layoutMain.addComponent(btnPaste);
        }

        btnIndex++;

        btnDelete = new Button(5, 25 + btnIndex * 20, Icons.TRASH);
        btnDelete.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                deleteSelectedFile();
            }
        });
        btnDelete.setToolTip("Delete", "Deletes the selected file or folder");
        btnDelete.setEnabled(false);
        layoutMain.addComponent(btnDelete);

        fileList = new ItemList<>(mode.getOffset(), 25, 180, mode.getVisibleItems());
        fileList.setListItemRenderer(ITEM_RENDERER);
        fileList.sortBy(FileInfo.SORT_BY_NAME);
        fileList.setItemClickListener((file, index, mouseButton) -> {
            if (mouseButton == 0) {
                btnRename.setEnabled(true);
                btnDelete.setEnabled(true);
                if (mode == Mode.FULL) {
                    btnCopy.setEnabled(true);
                    btnCut.setEnabled(true);
                }
                if (System.currentTimeMillis() - this.lastClick <= 200) {
                    if (file.isFolder()) {
                        fileList.setSelectedIndex(-1);
                        openFolder(file, true, (folder, success) -> {
                            if (mode == Mode.FULL) {
                                btnRename.setEnabled(false);
                                btnCopy.setEnabled(false);
                                btnCut.setEnabled(false);
                                btnDelete.setEnabled(false);
                            }
                        });
                    } else if (mode == Mode.FULL && wrappable instanceof SystemApp systemApp) {
                        ComputerScreen computerScreen = systemApp.getLaptop();
                        if (computerScreen != null) {
                            //TODO change to check if application is installed
                            String openingApp = file.getOpeningApp();
                            if (openingApp == null) {
                                createErrorDialog("This file does not have an application associated with it.");
                                return;
                            }
                            Application targetApp = computerScreen.getApplication(openingApp);
                            if (targetApp == null) {
                                createErrorDialog("This file does not have an application associated with it.");
                                return;
                            }
                            if (!computerScreen.isApplicationInstalled(targetApp.getInfo()))
                                createErrorDialog(targetApp.getInfo().getName() + " is not installed.");
                            if (computerScreen.isApplicationInstalled(targetApp.getInfo())) {
                                computerScreen.launchApp(targetApp.getInfo(), file, launcherResponse -> {
                                    if (launcherResponse.error() != null) {
                                        createErrorDialog(launcherResponse.error());
                                        return;
                                    }
                                    if (!launcherResponse.success()) {
                                        createErrorDialog(targetApp.getInfo().getName() + " was unable to open the file.");
                                        return;
                                    }

                                    computerScreen.sendApplicationToFront(targetApp.getInfo());
                                });
                            } else {
                                createErrorDialog("This file could not be open because the application '" + ChatFormatting.YELLOW + targetApp.getInfo().getName() + ChatFormatting.RESET + "' is not installed.");
                            }
                        }
                    }
                } else {
                    this.lastClick = System.currentTimeMillis();
                }
            }
            if (itemClickListener != null) {
                itemClickListener.onClick(file, index, mouseButton);
            }
        });
        layoutMain.addComponent(fileList);

        comboBoxDrive = new ComboBox.List<>(26, 3, 44, 100, new Drive[]{});
        comboBoxDrive.setChangeListener((oldValue, newValue) -> openDrive(newValue));
        comboBoxDrive.setListItemRenderer(new ListItemRenderer<>(12) {
            @Override
            public void render(GuiGraphics graphics, Drive drive, Minecraft mc, int x, int y, int width, int height, boolean selected) {
                Color bgColor = new Color(getColorScheme().getBackgroundColor(), true);
                graphics.fill(x, y, x + width, y + height, selected ? bgColor.brighter().brighter().getRGB() : bgColor.brighter().getRGB());
                RenderSystem.setShaderTexture(0, ASSETS);
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                RenderUtil.drawRectWithTexture(ASSETS, graphics, x + 2, y + 2, drive.getType().ordinal() * 8, 30, 8, 8, 8, 8);

                String text = drive.getName();
                if (mc.font.width(text) > 87) {
                    text = mc.font.plainSubstrByWidth(drive.getName(), 78) + "...";
                }
                graphics.drawString(mc.font, text, x + 13, y + 2, Color.WHITE.getRGB());
            }
        });
        layoutMain.addComponent(comboBoxDrive);

        labelPath = new Label("/", 72, 6);
        layoutMain.addComponent(labelPath);
        layout.addComponent(layoutMain);

        layoutLoading = new Layout(mode.getOffset(), 25, fileList.getWidth(), fileList.getHeight());
        layoutLoading.setBackground((graphics, mc, x, y, width, height, mouseX, mouseY, windowActive) -> graphics.fill(x, y, x + width, y + height, Window.COLOR_WINDOW_DARK));
        layoutLoading.setVisible(false);

        spinnerLoading = new Spinner((layoutLoading.width - 12) / 2, (layoutLoading.height - 12) / 2);
        layoutLoading.addComponent(spinnerLoading);
        layout.addComponent(layoutLoading);

        setLoading(true);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void handleLoad() {
        if (!loadedStructure) {
            setLoading(true);
            Task task = new TaskSetupFileBrowser(ComputerScreen.getPos(), ComputerScreen.getMainDrive() == null);
            task.setCallback((tag, success) -> {
                if (success) {
                    if (ComputerScreen.getMainDrive() == null) {
                        assert tag != null;
                        if (!tag.contains("main_drive", Tag.TAG_COMPOUND)) {
                            createErrorDialog("Unable to find main drive.");
                            return;
                        }
                        Drive drive = new Drive(tag.getCompound("main_drive"));
                        ComputerScreen.setMainDrive(drive);
                    }

                    assert tag != null;
                    ListTag driveList = tag.getList("available_drives", Tag.TAG_COMPOUND);
                    Drive[] drives = new Drive[driveList.size() + 1];
                    Drive currentDrive1;
                    drives[0] = currentDrive1 = ComputerScreen.getMainDrive();
                    for (int i = 0; i < driveList.size(); i++) {
                        CompoundTag driveTag = driveList.getCompound(i);
                        drives[i + 1] = new Drive(driveTag);
                    }
                    comboBoxDrive.setItems(drives);

                    currentDrive1.exists(initialFolder, (response) -> {
                        if (response.data()) {
                            initial(currentDrive1);
                        } else {
                            currentDrive1.createDirectory(initialFolder.getParent(), initialFolder.getFileName().toString(), response1 -> {
                                if (!response1.success()) {
                                    createErrorDialog("Unable to create directory '" + initialFolder + "': " + response1.message());
                                    return;
                                }
                                initial(currentDrive1);
                            });
                        }
                    });

                } else {
                    createErrorDialog("A critical error occurred while initializing.");
                }
            });
            TaskManager.sendTask(task);
            loadedStructure = true;
        } else {
            setLoading(false);
        }
    }

    private void initial(Drive currentDrive1) {
        currentDrive1.info(initialFolder, (response1) -> {
            if (!response1.success()) {
                createErrorDialog("Unable to open directory '" + initialFolder + "': " + response1.message());
                return;
            }

            currentPath = response1.data();
            openFolder(response1.data(), true, (path, success2) -> {
                if (!success2) {
                    createErrorDialog("A critical error occurred while initializing.");
                }

                setLoading(false);
            });
        });
    }

    @Override
    public void handleTick() {
        if (refreshList) {
            currentPath.list((response) -> {
                if (response.success()) {
                    fileList.removeAll();
                    fileList.setItems(response.data());
                } else {
                    createErrorDialog(response.message());
                }
            });
        }
    }

    public void openFolder(Path directory) {
        this.initialFolder = directory;
    }

    private void openDrive(Drive drive) {
        history.clear();
        setLoading(true);
        drive.open((response) -> {
            if (response.success()) {
                fileList.removeAll();
                fileList.setItems(response.data().files());
                currentPath = response.data().info();
                updatePath();
                setLoading(false);
                return;
            }

            createErrorDialog(response.message());
            setLoading(false);
        });
    }

    private void openFolder(FileInfo info, boolean push, Callback<FileInfo> callback) {
        BlockPos pos = ComputerScreen.getPos();
        if (pos == null) {
            if (callback != null) {
                callback.execute(info, false);
            }
            return;
        }

        setLoading(true);
        if (!info.isFolder()) {
            if (callback != null) {
                callback.execute(info, false);
            }

            createErrorDialog("Path is not a folder.");
            return;
        }

        info.list((response) -> {
            if (!response.success()) {
                setLoading(false);
                if (callback != null) {
                    callback.execute(info, false);
                }
                return;
            }

            List<FileInfo> files = response.data();
            if (filter != null) {
                files = files.stream().filter(filter).toList();
            }

            fileList.removeAll();
            fileList.setItems(files);
            updatePath();
            setLoading(false);

            fileList.setItems(files);
            if (push) history.push(info);
            callback.execute(info, true);
            updatePath();
        });
    }

    private void setCurrentFolder(FileInfo path, boolean push) {
        if (push) {
            history.push(path);
            btnPreviousFolder.setEnabled(true);
        }


        currentPath = path;
        fileList.removeAll();

        setLoading(true);
        currentPath.list((response) -> {
            if (!response.success()) {
                createErrorDialog(response.message());
                setLoading(false);
                return;
            }

            List<FileInfo> files = response.data();
            if (filter != null) {
                files = files.stream().filter(filter).collect(Collectors.toList());
            }
            fileList.setItems(files);

            updatePath();
        });
    }

    private void createFolder() {
        if (currentPath == null) {
            createErrorDialog("Browser not loaded yet...");
            return;
        }
        Dialog.Input dialog = new Dialog.Input("Enter a name");
        dialog.setResponseHandler((success, v) -> {
            if (success) {
                if (currentPath == null) {
                    createErrorDialog("Browser not loaded yet...");
                    return false;
                }
                currentPath.createDirectory(v, (response) -> {
                    if (response == null) {
                        createErrorDialog("Unable to create folder");
                    } else {
                        if (response.success()) {
                            openFolder(response.data(), true, (folder, success2) -> {
                                if (!success2) {
                                    createErrorDialog("Unable to open folder");
                                }
                            });
                            return;
                        }

                        createErrorDialog(response.message());
                    }
                });
            }
            return true;
        });
        dialog.setTitle("Create a Folder");
        dialog.setPositiveText("Create");
        wrappable.openDialog(dialog);
    }

    private void goToPreviousFolder() {
        if (!history.isEmpty()) {
            setLoading(true);
            FileInfo folder = history.pop();
            openFolder(folder, false, (folder2, success) -> {
                if (success) {
                    if (isRootFolder()) {
                        btnPreviousFolder.setEnabled(false);
                    }
                    updatePath();
                } else {
                    createErrorDialog("Unable to open previous folder");
                }
                setLoading(false);
            });
        }
    }

    public @Nullable FileInfo getSelectedFile() {
        return fileList.getSelectedItem();
    }

    public void addFile(FileInfo file) {

    }

    private void deleteSelectedFile() {
        @Nullable FileInfo file = fileList.getSelectedItem();
        if (file != null) {
            if (file.isProtected()) {
                String message = "This " + (file.isFolder() ? "folder" : "file") + " is protected and can not be deleted.";
                Dialog.Message dialog = new Dialog.Message(message);
                wrappable.openDialog(dialog);
                return;
            }

            Dialog.Confirmation dialog = new Dialog.Confirmation();
            StringBuilder builder = new StringBuilder();
            builder.append("Are you sure you want to delete this ");
            if (file.isFolder()) {
                builder.append("folder");
            } else {
                builder.append("file");
            }
            builder.append(" '").append(file.getName()).append("'?");
            dialog.setMessageText(builder.toString());
            dialog.setTitle("Delete");
            dialog.setPositiveText("Yes");
            dialog.setPositiveListener((mouseX, mouseY, mouseButton) -> {
                removeFile(fileList.getSelectedIndex());
                btnRename.setEnabled(false);
                btnDelete.setEnabled(false);
                if (mode == Mode.FULL) {
                    btnCopy.setEnabled(false);
                    btnCut.setEnabled(false);
                }
            });
            wrappable.openDialog(dialog);
        }
    }

    private void removeFile(int index) {
        FileInfo file = fileList.getItem(index);
        if (file != null) {
            if (file.isProtected()) {
                String message = "This " + (file.isFolder() ? "folder" : "file") + " is protected and can not be deleted.";
                Dialog.Message dialog = new Dialog.Message(message);
                wrappable.openDialog(dialog);
                return;
            }
            setLoading(true);
            file.delete((response) -> {
                if (response.success()) {
                    fileList.removeItem(index);
                    FileBrowser.refreshList = true;
                } else {
                    createErrorDialog(response.message());
                }
                setLoading(false);
            });
        }
    }

    public void removeFile(String name) {
        setLoading(true);
        currentPath.list(response -> {
            if (!response.success()) {
                createErrorDialog(response.message());
                setLoading(false);
                return;
            }
            List<FileInfo> files = response.data();
            this.fileList.setItems(files);
            for (int i = 0; i < files.size(); i++) {
                FileInfo file = files.get(i);
                if (!file.getName().equals(name)) continue;
                if (file.isProtected()) {
                    createErrorDialog("This file is protected and can not be deleted.");
                    setLoading(false);
                    return;
                }
                removeFile(i);
                setLoading(false);
                return;
            }

            createErrorDialog("File not found");
            setLoading(false);

        });
    }

    private void setClipboardFileToSelected() {
        @Nullable FileInfo file = fileList.getSelectedItem();
        if (file != null) {
            if (file.isProtected()) {
                String message = "This " + (file.isFolder() ? "folder" : "file") + " is protected and can not be copied.";
                Dialog.Message dialog = new Dialog.Message(message);
                wrappable.openDialog(dialog);
                return;
            }
            clipboardFile = file;
            btnPaste.setEnabled(true);
        } else {
            Dialog.Message dialog = new Dialog.Message("The file/folder you are trying to copy does not exist.");
            wrappable.openDialog(dialog);
        }
    }

    private void cutSelectedFile() {
        @Nullable FileInfo file = fileList.getSelectedItem();
        if (file != null) {
            if (file.isProtected()) {
                String message = "This " + (file.isFolder() ? "folder" : "file") + " is protected and can not be moved.";
                Dialog.Message dialog = new Dialog.Message(message);
                wrappable.openDialog(dialog);
                return;
            }

            clipboardDir = currentPath;
            clipboardFile = file;
            btnPaste.setEnabled(true);
        } else {
            Dialog.Message dialog = new Dialog.Message("The file/folder you are trying to cut does not exist.");
            wrappable.openDialog(dialog);
        }
    }

    private void pasteClipboardFile() {
        if (clipboardFile != null) {
            if (canPasteHere()) {
                handleCopyCut(false);
            } else {
                Dialog.Message dialog = new Dialog.Message("Destination folder can't be a subfolder");
                wrappable.openDialog(dialog);
            }
        }
    }

    private void handleCopyCut(boolean override) {
        setLoading(true);
        if (clipboardDir != null) {
            clipboardFile.moveTo(currentPath.getPath(), override, (response) -> {
                if (response.status() == FileSystem.Status.FILE_EXISTS) {
                    Dialog.Confirmation dialog = new Dialog.Confirmation("A file with the same name already exists in this directory. Do you want to override it?");
                    dialog.setPositiveText("Override");
                    dialog.setPositiveListener((mouseX, mouseY, mouseButton) -> {
                        if (mouseButton == 0) {
                            handleCopyCut(true);
                        }
                    });
                    wrappable.openDialog(dialog);
                    return;
                }

                if (!response.success()) {
                    createErrorDialog(response.message());
                    return;
                }

                resetClipboard();
            });
        } else {
            clipboardFile.copyTo(currentPath.getPath(), override, response -> {
                if (response.status() == FileSystem.Status.FILE_EXISTS) {
                    Dialog.Confirmation dialog = new Dialog.Confirmation("A file with the same name already exists in this directory. Do you want to override it?");
                    dialog.setPositiveText("Override");
                    dialog.setPositiveListener((mouseX, mouseY, mouseButton) -> {
                        if (mouseButton == 0) {
                            handleCopyCut(true);
                        }
                    });
                    wrappable.openDialog(dialog);
                    return;
                }

                if (!response.success()) {
                    createErrorDialog(response.message());
                    return;
                }
                resetClipboard();
            });
        }
    }

    private void resetClipboard() {
        if (clipboardDir != null) {
            clipboardDir = null;
            clipboardFile = null;
            btnPaste.setEnabled(false);
        }
        currentPath.list((response) -> {
            if (!response.success()) {
                createErrorDialog(response.message());
                setLoading(false);
                return;
            }
            if (mode == Mode.FULL) {
                btnRename.setEnabled(false);
                btnCopy.setEnabled(false);
                btnCut.setEnabled(false);
                btnDelete.setEnabled(false);
            }
        });
    }

    private boolean canPasteHere() {
        if (clipboardFile != null) {
            if (clipboardFile.isFolder()) {
                return !history.contains(clipboardFile) && currentPath != clipboardFile;
            }
        }
        return true;
    }

    private boolean isRootFolder() {
        return history.isEmpty();
    }

    private void updatePath() {
        String path = currentPath.getPath().toString();
        path = path.replace("/", ChatFormatting.GOLD + "/" + ChatFormatting.RESET);
        int width = Minecraft.getInstance().font.width(path);
        if (width > 144) {
            path = "..." + Minecraft.getInstance().font.plainSubstrByWidth(path, 144, true);
        }
        labelPath.setText(path);
    }

    public void setLoading(boolean loading) {
        layoutLoading.setVisible(loading);
        if (loading) {
            disableAllButtons();
        } else {
            updateButtons();
        }
    }

    private void updateButtons() {
        boolean hasSelectedFile = fileList.getSelectedIndex() != -1;
        btnNewFolder.setEnabled(true);
        btnRename.setEnabled(hasSelectedFile);
        btnDelete.setEnabled(hasSelectedFile);
        if (mode == Mode.FULL) {
            btnCopy.setEnabled(hasSelectedFile);
            btnCut.setEnabled(hasSelectedFile);
            btnPaste.setEnabled(clipboardFile != null);
        }
        btnPreviousFolder.setEnabled(!isRootFolder());
    }

    private void disableAllButtons() {
        btnPreviousFolder.setEnabled(false);
        btnNewFolder.setEnabled(false);
        btnRename.setEnabled(false);
        btnDelete.setEnabled(false);
        if (mode == Mode.FULL) {
            btnCopy.setEnabled(false);
            btnCut.setEnabled(false);
            btnPaste.setEnabled(false);
        }
    }

    private void renameSelectedFile() {
        @Nullable FileInfo file = fileList.getSelectedItem();
        if (file != null) {
            if (file.protectedFile()) {
                String message = "This " + (file.isFolder() ? "folder" : "file") + " is protected and can not be renamed.";
                Dialog.Message dialog = new Dialog.Message(message);
                wrappable.openDialog(dialog);
                return;
            }

            Dialog.Input dialog = new Dialog.Input("Enter a name");
            dialog.setResponseHandler((success, s) -> {
                if (success) {
                    setLoading(true);
                    file.rename(s, (response) -> {
                        assert response != null;
                        if (response.status() == FileSystem.Status.SUCCESSFUL) {
                            dialog.close();
                        } else {
                            createErrorDialog(response.message());
                        }
                        setLoading(false);
                    });
                }
                return false;
            });
            dialog.setTitle("Rename " + (file.isFolder() ? "Folder" : "File"));
            dialog.setInputText(file.getName());
            wrappable.openDialog(dialog);
        }
    }

    private void createErrorDialog(String message) {
        Dialog.Message dialog = new Dialog.Message(message);
        dialog.setTitle("Error");
        wrappable.openDialog(dialog);
    }

    public void setFilter(Predicate<FileInfo> filter) {
        this.filter = filter;
    }

    public void setItemClickListener(ItemClickListener<FileInfo> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public enum Mode {
        FULL(225, 145, 26, 6), BASIC(211, 105, 26, 4);

        private final int width;
        private final int height;
        private final int offset;
        private final int visibleItems;

        Mode(int width, int height, int offset, int visibleItems) {
            this.width = width;
            this.height = height;
            this.offset = offset;
            this.visibleItems = visibleItems;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getOffset() {
            return offset;
        }

        public int getVisibleItems() {
            return visibleItems;
        }
    }
}
