package com.ultreon.devices.api.app;

import com.ultreon.devices.api.app.component.Button;
import com.ultreon.devices.api.app.component.ItemList;
import com.ultreon.devices.api.app.component.Text;
import com.ultreon.devices.api.app.listener.ClickListener;
import com.ultreon.devices.api.app.renderer.ListItemRenderer;
import com.ultreon.devices.api.io.File;
import com.ultreon.devices.api.print.IPrint;
import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.api.task.TaskManager;
import com.ultreon.devices.api.utils.RenderUtil;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.core.Wrappable;
import com.ultreon.devices.core.io.FileSystem;
import com.ultreon.devices.core.network.NetworkDevice;
import com.ultreon.devices.core.network.task.TaskGetDevices;
import com.ultreon.devices.core.print.task.TaskPrint;
import com.ultreon.devices.init.ModTags;
import com.ultreon.devices.programs.system.component.FileBrowser;
import com.ultreon.devices.programs.system.object.ColorScheme;
import com.ultreon.devices.util.GLHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.function.Predicate;

public abstract class Dialog extends Wrappable {
    protected final Layout defaultLayout;
    private String title = "Message";
    private int width;
    private int height;
    private Layout customLayout;

    private boolean pendingLayoutUpdate = true;
    private boolean pendingClose = false;

    protected Dialog() {
        this.defaultLayout = new Layout(150, 40);
    }

    protected final void addComponent(com.ultreon.devices.api.app.Component c) {
        if (c != null) {
            defaultLayout.addComponent(c);
            c.init(defaultLayout);
        }
    }

    protected final void setLayout(Layout layout) {
        this.customLayout = layout;
        this.width = layout.width;
        this.height = layout.height;
        this.pendingLayoutUpdate = true;
        this.customLayout.handleLoad();
    }

    @Override
    public void init(@Nullable CompoundTag intent) {
        this.defaultLayout.clear();
        this.setLayout(defaultLayout);
    }

    @Override
    public void onTick() {
        if (pendingClose && getWindow().getDialogWindow() == null) {
            getWindow().close();
        }
        customLayout.handleTick();
    }

    @Override
    public void render(GuiGraphics graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active, float partialTicks) {
        GLHelper.pushScissor(x, y, width, height);
        customLayout.render(graphics, laptop, mc, x, y, mouseX, mouseY, active, partialTicks);
        GLHelper.popScissor();

        customLayout.renderOverlay(graphics, laptop, mc, mouseX, mouseY, active);

        // TODO - Port this to 1.18.2
//        RenderHelper.disableStandardItemLighting();
    }

    @Override
    public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
        customLayout.handleMouseClick(mouseX, mouseY, mouseButton);
    }

    @Override
    public void handleMouseDrag(int mouseX, int mouseY, int mouseButton) {
        customLayout.handleMouseDrag(mouseX, mouseY, mouseButton);
    }

    @Override
    public void handleMouseRelease(int mouseX, int mouseY, int mouseButton) {
        customLayout.handleMouseRelease(mouseX, mouseY, mouseButton);
    }

    @Override
    public void handleMouseScroll(int mouseX, int mouseY, double delta, boolean direction) {
        customLayout.handleMouseScroll(mouseX, mouseY, direction);
    }

    @Override
    public void handleKeyPressed(int keyCode, int scanCode, int modifiers) {
        customLayout.handleKeyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void handleKeyReleased(int keyCode, int scanCode, int modifiers) {
        customLayout.handleKeyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public void handleCharTyped(char character, int modifiers) {
        customLayout.handleCharTyped(character, modifiers);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getWindowTitle() {
        return title;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void markForLayoutUpdate() {
        this.pendingLayoutUpdate = true;
    }

    @Override
    public boolean isPendingLayoutUpdate() {
        return pendingLayoutUpdate;
    }

    @Override
    public void clearPendingLayout() {
        this.pendingLayoutUpdate = false;
    }

    @Override
    public void updateComponents(int x, int y) {
        customLayout.updateComponents(x, y);
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    public void close() {
        this.pendingClose = true;
    }

    /**
     * The response listener interface. Used for handling responses
     * from components. The generic is the returned value.
     *
     * @author MrCrayfish
     */
    public interface ResponseHandler<E> {
        /**
         * Called when a response is thrown.
         *
         * @param success if the executing task was successful
         */
        boolean onResponse(boolean success, E e);
    }

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    public static class Message extends Dialog {
        private final String messageText;

        private ClickListener positiveListener;
        private Button buttonPositive;

        public Message(String messageText) {
            this.messageText = messageText;
        }

        @Override
        public void init(@Nullable CompoundTag intent) {
            super.init(intent);

            int textHeight = Minecraft.getInstance().font.wordWrapHeight(messageText, getWidth() - 10);
            defaultLayout.height += textHeight;

            super.init(intent);

            defaultLayout.setBackground((graphics, mc, x, y, width, height, mouseX, mouseY, windowActive) -> graphics.fill(x, y, x + width, y + height, Color.LIGHT_GRAY.getRGB()));

            Text message = new Text(messageText, 5, 5, getWidth() - 10);
            this.addComponent(message);

            buttonPositive = new Button(getWidth() - 41, getHeight() - 20, "Close");
            buttonPositive.setSize(36, 16);
            buttonPositive.setClickListener((mouseX, mouseY, mouseButton) -> {
                if (positiveListener != null) {
                    positiveListener.onClick(mouseX, mouseY, mouseButton);
                }
                close();
            });
            this.addComponent(buttonPositive);

        }
    }

    /**
     * A simple confirmation dialog
     * <p>
     * This can be used to prompt as user to confirm whether a
     * task should run. For instance, the FileBrowser component
     * uses this dialog to prompt the user if it should override
     * a file.
     */
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    public static class Confirmation extends Dialog {
        private static final int DIVIDE_WIDTH = 15;

        private String messageText = "Are you sure?";
        private String positiveText = "Yes";
        private String negativeText = "No";

        private ClickListener positiveListener;
        private ClickListener negativeListener;

        private com.ultreon.devices.api.app.component.Button buttonPositive;
        private com.ultreon.devices.api.app.component.Button buttonNegative;

        public Confirmation() {
        }

        public Confirmation(String messageText) {
            this.messageText = messageText;
        }

        @Override
        public void init(@Nullable CompoundTag intent) {
            super.init(intent);

            int lines = Minecraft.getInstance().font.wordWrapHeight(messageText, getWidth() - 10);
            defaultLayout.height += (lines - 1) ;

            super.init(intent);

            defaultLayout.setBackground((graphics, mc, x, y, width, height, mouseX, mouseY, windowActive) -> graphics.fill(x, y, x + width, y + height, Color.LIGHT_GRAY.getRGB()));

            Text message = new Text(messageText, 5, 5, getWidth() - 10);
            this.addComponent(message);

            int positiveWidth = Minecraft.getInstance().font.width(positiveText);
            buttonPositive = new com.ultreon.devices.api.app.component.Button(getWidth() - positiveWidth - DIVIDE_WIDTH, getHeight() - 20, positiveText);
            buttonPositive.setSize(positiveWidth + 10, 16);
            buttonPositive.setClickListener((mouseX, mouseY, mouseButton) -> {
                if (positiveListener != null) {
                    positiveListener.onClick(mouseX, mouseY, mouseButton);
                }
                close();
            });
            this.addComponent(buttonPositive);

            int negativeWidth = Math.max(20, Minecraft.getInstance().font.width(negativeText));
            buttonNegative = new com.ultreon.devices.api.app.component.Button(getWidth() - DIVIDE_WIDTH - positiveWidth - DIVIDE_WIDTH - negativeWidth + 1, getHeight() - 20, negativeText);
            buttonNegative.setSize(negativeWidth + 10, 16);
            buttonNegative.setClickListener((mouseX, mouseY, mouseButton) -> {
                if (negativeListener != null) {
                    negativeListener.onClick(mouseX, mouseY, mouseButton);
                }
                close();
            });
            this.addComponent(buttonNegative);
        }

        /**
         * Sets the positive button text
         *
         * @param positiveText the text to set
         */
        @SuppressWarnings("ConstantConditions")
        public void setPositiveText(@NotNull String positiveText) {
            if (positiveText == null) throw new IllegalArgumentException("Text can't be null");
            this.positiveText = positiveText;
        }

        /**
         * Sets the negative button text
         *
         * @param negativeText the text to set
         */
        @SuppressWarnings("ConstantConditions")
        public void setNegativeText(@NotNull String negativeText) {
            if (negativeText == null) {
                throw new IllegalArgumentException("Text can't be null");
            }
            this.negativeText = negativeText;
        }

        public void setPositiveListener(ClickListener positiveListener) {
            this.positiveListener = positiveListener;
        }

        public void setNegativeListener(ClickListener negativeListener) {
            this.negativeListener = negativeListener;
        }

        public void setMessageText(String messageText) {
            this.messageText = messageText;
        }
    }

    /**
     * A simple dialog to retrieve text input from the user
     */
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    public static class Input extends Dialog {
        private static final int DIVIDE_WIDTH = 15;

        private String messageText = null;
        private String inputText = "";
        private String positiveText = "Okay";
        private String negativeText = "Cancel";

        private ResponseHandler<String> responseListener;

        private com.ultreon.devices.api.app.component.TextField textFieldInput;
        private com.ultreon.devices.api.app.component.Button buttonPositive;
        private com.ultreon.devices.api.app.component.Button buttonNegative;

        public Input() {
        }

        public Input(String messageText) {
            this.messageText = messageText;
        }

        @Override
        public void init(@Nullable CompoundTag intent) {
            super.init(intent);

            int offset = 0;

            if (messageText != null) {
                int lines = Minecraft.getInstance().font.wordWrapHeight(messageText, getWidth() - 10);
                defaultLayout.height += lines * 9 + 10;
                offset += lines * 9 + 5;
            }

            super.init(intent);

            defaultLayout.setBackground((graphics, mc, x, y, width, height, mouseX, mouseY, windowActive) -> graphics.fill(x, y, x + width, y + height, Color.LIGHT_GRAY.getRGB()));

            if (messageText != null) {
                Text message = new Text(messageText, 5, 5, getWidth() - 10);
                this.addComponent(message);
            }

            textFieldInput = new com.ultreon.devices.api.app.component.TextField(5, 5 + offset, getWidth() - 10);
            textFieldInput.setText(inputText);
            textFieldInput.setFocused(true);
            this.addComponent(textFieldInput);

            int positiveWidth = Minecraft.getInstance().font.width(positiveText);
            buttonPositive = new com.ultreon.devices.api.app.component.Button(getWidth() - positiveWidth - DIVIDE_WIDTH, getHeight() - 20, positiveText);
            buttonPositive.setSize(positiveWidth + 10, 16);
            buttonPositive.setClickListener((mouseX, mouseY, mouseButton) -> {
                if (!textFieldInput.getText().isEmpty()) {
                    boolean close = true;
                    if (responseListener != null) {
                        close = responseListener.onResponse(true, textFieldInput.getText().trim());
                    }
                    if (close) close();
                }
            });
            this.addComponent(buttonPositive);

            int negativeWidth = Minecraft.getInstance().font.width(negativeText);
            buttonNegative = new com.ultreon.devices.api.app.component.Button(getWidth() - DIVIDE_WIDTH - positiveWidth - DIVIDE_WIDTH - negativeWidth + 1, getHeight() - 20, negativeText);
            buttonNegative.setSize(negativeWidth + 10, 16);
            buttonNegative.setClickListener((mouseX, mouseY, mouseButton) -> close());
            this.addComponent(buttonNegative);
        }

        /**
         * Sets the initial text for the input text field
         *
         * @param inputText the text to set
         */
        @SuppressWarnings("ConstantConditions")
        public void setInputText(@NotNull String inputText) {
            if (inputText == null) {
                throw new IllegalArgumentException("Text can't be null");
            }
            this.inputText = inputText;
        }

        /**
         * Gets the input text field. This will be null if it has not been
         *
         * @return the input text field
         */
        @Nullable
        public com.ultreon.devices.api.app.component.TextField getTextFieldInput() {
            return textFieldInput;
        }

        /**
         * Sets the positive button text
         *
         * @param positiveText the text to set
         */
        @SuppressWarnings("ConstantConditions")
        public void setPositiveText(@NotNull String positiveText) {
            if (positiveText == null) {
                throw new IllegalArgumentException("Text can't be null");
            }
            this.positiveText = positiveText;
        }

        /**
         * Sets the negative button text
         *
         * @param negativeText the text to set
         */
        @SuppressWarnings("ConstantConditions")
        public void setNegativeText(@NotNull String negativeText) {
            if (negativeText == null) {
                throw new IllegalArgumentException("Text can't be null");
            }
            this.negativeText = negativeText;
        }

        /**
         * Sets the response handler. The handler is called when the positive
         * button is pressed and returns the value in the input text field. Returning
         * true in the handler indicates that the dialog should close.
         *
         * @param responseListener the response handler
         */
        public void setResponseHandler(ResponseHandler<String> responseListener) {
            this.responseListener = responseListener;
        }
    }

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    public static class OpenFile extends Dialog {
        private final Application app;

        private String positiveText = "Open";
        private String negativeText = "Cancel";

        private Layout main;
        private FileBrowser browser;
        private com.ultreon.devices.api.app.component.Button buttonPositive;
        private com.ultreon.devices.api.app.component.Button buttonNegative;

        private ResponseHandler<File> responseListener;
        private Predicate<File> filter;

        public OpenFile(Application app) {
            this.app = app;
            this.setTitle("Open File");
        }

        @Override
        public void init(@Nullable CompoundTag intent) {
            super.init(intent);

            main = new Layout(211, 126);

            browser = new FileBrowser(0, 0, app, FileBrowser.Mode.BASIC);
            browser.openFolder(FileSystem.DIR_HOME);
            browser.setFilter(file -> filter == null || filter.test(file) || file.isFolder());
            browser.setItemClickListener((file, index, mouseButton) -> {
                if (mouseButton == 0 && !file.isFolder()) {
                    buttonPositive.setEnabled(true);
                }
            });
            main.addComponent(browser);

            int positiveWidth = Minecraft.getInstance().font.width(positiveText);
            buttonPositive = new com.ultreon.devices.api.app.component.Button(172, 106, positiveText);
            buttonPositive.setSize(positiveWidth + 10, 16);
            buttonPositive.setEnabled(false);
            buttonPositive.setClickListener((mouseX, mouseY, mouseButton) -> {
                if (mouseButton == 0) {
                    File file = browser.getSelectedFile();
                    if (file != null) {
                        boolean close = true;
                        if (responseListener != null) {
                            close = responseListener.onResponse(true, file);
                        }
                        if (close) close();
                    }
                }
            });
            main.addComponent(buttonPositive);

            int negativeWidth = Minecraft.getInstance().font.width(negativeText);
            buttonNegative = new com.ultreon.devices.api.app.component.Button(125, 106, negativeText);
            buttonNegative.setSize(negativeWidth + 10, 16);
            buttonNegative.setClickListener((mouseX, mouseY, mouseButton) -> close());
            main.addComponent(buttonNegative);

            this.setLayout(main);
        }

        /**
         * Sets the positive button text
         *
         * @param positiveText the text to set
         */
        public void setPositiveText(String positiveText) {
            if (positiveText == null) {
                throw new IllegalArgumentException("Text can't be null");
            }
            this.positiveText = positiveText;
        }

        /**
         * Sets the negative button text
         *
         * @param negativeText the text to set
         */
        public void setNegativeText(String negativeText) {
            if (negativeText == null) {
                throw new IllegalArgumentException("Text can't be null");
            }
            this.negativeText = negativeText;
        }

        /**
         * Sets the response handler. The handler is called when the positive
         * button is pressed and returns the file that is selected. Returning
         * true in the handler indicates that the dialog should close.
         *
         * @param responseListener the response handler to handle the returned file
         */
        public void setResponseHandler(ResponseHandler<File> responseListener) {
            this.responseListener = responseListener;
        }

        /**
         * Sets the filter for the file list to show only files that match certain conditions.
         *
         * @param filter the predicate
         */
        public void setFilter(Predicate<File> filter) {
            this.filter = filter;
        }

        /**
         * Sets the filter for the file list to show only files that can open with the specified
         * application.
         *
         * @param app the predicate
         */
        public void setFilter(Application app) {
            this.filter = file -> app.getInfo().getFormattedId().equals(file.getOpeningApp());
        }
    }

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    public static class SaveFile extends Dialog {
        private final Application app;
        private final CompoundTag data;
        public ResponseHandler<File> responseHandler;
        private String name;
        private String positiveText = "Save";
        private String negativeText = "Cancel";
        private Layout main;
        private FileBrowser browser;
        private com.ultreon.devices.api.app.component.TextField textFieldFileName;
        private com.ultreon.devices.api.app.component.Button buttonPositive;
        private com.ultreon.devices.api.app.component.Button buttonNegative;
        private Predicate<File> filter;

        private String path = FileSystem.DIR_HOME;

        public SaveFile(Application app, CompoundTag data) {
            this.app = app;
            this.data = data;
            this.setTitle("Save File");
        }

        public SaveFile(Application app, File file) {
            this.app = app;
            this.name = file.getName();
            this.data = file.toTag();
            this.setTitle("Save File");
        }

        @Override
        public void init(@Nullable CompoundTag intent) {
            super.init(intent);
            main = new Layout(211, 145);

            browser = new FileBrowser(0, 0, app, FileBrowser.Mode.BASIC);
            browser.setFilter(file -> filter == null || filter.test(file) || file.isFolder());
            browser.openFolder(path);
            main.addComponent(browser);

            buttonPositive = new com.ultreon.devices.api.app.component.Button(172, 125, positiveText);
            buttonPositive.setClickListener((mouseX, mouseY, mouseButton) -> {
                if (mouseButton == 0 && !textFieldFileName.getText().isEmpty()) {
                    if (!FileSystem.PATTERN_FILE_NAME.matcher(textFieldFileName.getText()).matches()) {
                        Message dialog = new Message("File name may only contain letters, numbers, underscores and spaces.");
                        app.openDialog(dialog);
                        return;
                    }

                    File file;
                    if (name != null) {
                        file = File.fromTag(textFieldFileName.getText(), data);
                    } else {
                        file = new File(textFieldFileName.getText(), app, data.copy());
                    }

                    browser.addFile(file, (response, success) -> {
                        assert response != null;
                        if (response.getStatus() == FileSystem.Status.FILE_EXISTS) {
                            Confirmation dialog = new Confirmation("A file with that name already exists. Are you sure you want to override it?");
                            dialog.setPositiveText("Override");
                            dialog.setPositiveListener((mouseX1, mouseY1, mouseButton1) -> browser.addFile(file, true, (response1, success1) -> {
                                dialog.close();

                                //TODO Look into better handling. Get response from parent if should close. Maybe a response interface w/ generic
                                if (responseHandler != null) {
                                    responseHandler.onResponse(success1, file);
                                }
                                SaveFile.this.close();
                            }));
                            app.openDialog(dialog);
                        } else {
                            if (responseHandler != null) {
                                responseHandler.onResponse(true, file);
                            }
                            close();
                        }
                    });
                }
            });
            main.addComponent(buttonPositive);

            buttonNegative = new com.ultreon.devices.api.app.component.Button(126, 125, negativeText);
            buttonNegative.setClickListener((mouseX, mouseY, mouseButton) -> close());
            main.addComponent(buttonNegative);

            textFieldFileName = new com.ultreon.devices.api.app.component.TextField(26, 105, 180);
            textFieldFileName.setFocused(true);
            if (name != null) textFieldFileName.setText(name);
            main.addComponent(textFieldFileName);

            this.setLayout(main);
        }

        /**
         * Sets the positive button text
         *
         * @param positiveText the text to set
         */
        @SuppressWarnings("ConstantConditions")
        public void setPositiveText(@NotNull String positiveText) {
            if (positiveText == null) {
                throw new IllegalArgumentException("Text can't be null");
            }
            this.positiveText = positiveText;
        }

        /**
         * Sets the negative button text
         *
         * @param negativeText the text to set
         */
        @SuppressWarnings("ConstantConditions")
        public void setNegativeText(@NotNull String negativeText) {
            if (negativeText == null) {
                throw new IllegalArgumentException("Text can't be null");
            }
            this.negativeText = negativeText;
        }

        /**
         * Sets the response handler. The handler is called when the positive
         * button is pressed and returns the file that is selected. Returning
         * true in the handler indicates that the dialog should close.
         *
         * @param responseHandler the response handler to handle the returned file
         */
        public void setResponseHandler(ResponseHandler<File> responseHandler) {
            this.responseHandler = responseHandler;
        }

        /**
         * Sets the filter for the file list to show only files that match certain conditions.
         *
         * @param filter the predicate
         */
        public void setFilter(Predicate<File> filter) {
            this.filter = filter;
        }

        /**
         * Sets the filter for the file list to show only files that can open with the specified
         * application.
         *
         * @param app the predicate
         */
        public void setFilter(Application app) {
            this.filter = file -> app.getInfo().getFormattedId().equals(file.getOpeningApp());
        }

        /**
         * Sets the initial folder path to be shown when the dialog is opened
         *
         * @param path the initial folder path
         */
        public void setFolder(String path) {
            this.path = path;
        }
    }

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    public static class Print extends Dialog {
        private final IPrint print;

        private Layout layoutMain;
        private com.ultreon.devices.api.app.component.Label labelMessage;
        private com.ultreon.devices.api.app.component.Button buttonRefresh;
        private ItemList<NetworkDevice> itemListPrinters;
        private com.ultreon.devices.api.app.component.Button buttonPrint;
        private com.ultreon.devices.api.app.component.Button buttonCancel;
        private com.ultreon.devices.api.app.component.Button buttonInfo;

        public Print(IPrint print) {
            this.print = print;
            this.setTitle("Print");
        }

        @Override
        public void init(@Nullable CompoundTag intent) {
            super.init(intent);

            layoutMain = new Layout(150, 132);

            labelMessage = new com.ultreon.devices.api.app.component.Label("Select a Printer", 5, 5);
            layoutMain.addComponent(labelMessage);

            buttonRefresh = new com.ultreon.devices.api.app.component.Button(131, 2, Icons.RELOAD);
            buttonRefresh.setPadding(2);
            buttonRefresh.setToolTip("Refresh", "Retrieve an updated list of printers");
            buttonRefresh.setClickListener((mouseX, mouseY, mouseButton) -> {
                if (mouseButton == 0) {
                    itemListPrinters.setSelectedIndex(-1);
                    getPrinters(itemListPrinters);
                }
            });
            layoutMain.addComponent(buttonRefresh);

            itemListPrinters = new ItemList<>(5, 18, 140, 5);
            itemListPrinters.setListItemRenderer(new ListItemRenderer<>(16) {
                @Override
                public void render(GuiGraphics graphics, NetworkDevice networkDevice, Minecraft mc, int x, int y, int width, int height, boolean selected) {
                    ColorScheme colorScheme = Laptop.getSystem().getSettings().getColorScheme();
                    graphics.fill(x, y, x + width, y + height, selected ? colorScheme.getItemHighlightColor() : colorScheme.getItemBackgroundColor());
                    Icons.PRINTER.draw(graphics, mc, x + 3, y + 3);
                    RenderUtil.drawStringClipped(graphics, networkDevice.getName(), x + 18, y + 4, 118, Laptop.getSystem().getSettings().getColorScheme().getTextColor(), true);
                }
            });
            itemListPrinters.setItemClickListener((blockPos, index, mouseButton) -> {
                if (mouseButton == 0) {
                    buttonPrint.setEnabled(true);
                    buttonInfo.setEnabled(true);
                }
            });
            itemListPrinters.sortBy((o1, o2) -> {
                BlockPos laptopPos = Laptop.getPos();
                assert laptopPos != null;

                BlockPos pos1 = o1.getPos();
                assert pos1 != null;
                double distance1 = laptopPos.distToCenterSqr(pos1.getX() + 0.5, pos1.getY() + 0.5, pos1.getZ() + 0.5);

                BlockPos pos2 = o2.getPos();
                assert pos2 != null;
                double distance2 = laptopPos.distToCenterSqr(pos2.getX() + 0.5, pos2.getY() + 0.5, pos2.getZ() + 0.5);

                return Double.compare(distance1, distance2);
            });
            layoutMain.addComponent(itemListPrinters);

            buttonPrint = new com.ultreon.devices.api.app.component.Button(98, 108, "Print", Icons.CHECK);
            buttonPrint.setPadding(5);
            buttonPrint.setEnabled(false);
            buttonPrint.setClickListener((mouseX, mouseY, mouseButton) -> {
                if (mouseButton == 0) {
                    NetworkDevice networkDevice = itemListPrinters.getSelectedItem();
                    if (networkDevice != null) {
                        TaskPrint task = new TaskPrint(Laptop.getPos(), networkDevice, print);
                        task.setCallback((tag, success) -> {
                            if (success) {
                                close();
                            }
                        });
                        TaskManager.sendTask(task);
                    }
                }
            });
            layoutMain.addComponent(buttonPrint);

            buttonCancel = new com.ultreon.devices.api.app.component.Button(74, 108, Icons.CROSS);
            buttonCancel.setPadding(5);
            buttonCancel.setClickListener((mouseX, mouseY, mouseButton) -> {
                if (mouseButton == 0) {
                    close();
                }
            });
            layoutMain.addComponent(buttonCancel);

            buttonInfo = new com.ultreon.devices.api.app.component.Button(5, 108, Icons.HELP);
            buttonInfo.setEnabled(false);
            buttonInfo.setPadding(5);
            buttonInfo.setClickListener((mouseX, mouseY, mouseButton) -> {
                if (mouseButton == 0) {
                    NetworkDevice printerEntry = itemListPrinters.getSelectedItem();
                    if (printerEntry != null) {
                        Info info = new Info(printerEntry);
                        openDialog(info);
                    }
                }
            });
            layoutMain.addComponent(buttonInfo);

            setLayout(layoutMain);

            getPrinters(itemListPrinters);
        }

        private void getPrinters(ItemList<NetworkDevice> itemList) {
            itemList.removeAll();
            itemList.setLoading(true);
            Task task = new TaskGetDevices(Laptop.getPos(), ModTags.BlockEntityTypes.PRINTERS);
            task.setCallback((tag, success) -> {
                if (success) {
                    assert tag != null;
                    ListTag list = tag.getList("network_devices", Tag.TAG_COMPOUND);
                    for (int i = 0; i < list.size(); i++) {
                        itemList.addItem(NetworkDevice.fromTag(list.getCompound(i)));
                    }
                    itemList.setLoading(false);
                } else {
                    String reason = tag == null ? "${null}" : tag.getString("reason");
                    openDialog(new Message("Failed to load printers: " + reason));
                }
            });
            TaskManager.sendTask(task);
        }

        @SuppressWarnings({"FieldCanBeLocal", "unused"})
        private static class Info extends Dialog {
            private final NetworkDevice entry;

            private Layout layoutMain;
            private com.ultreon.devices.api.app.component.Label labelName;
            private com.ultreon.devices.api.app.component.Image imagePaper;
            private com.ultreon.devices.api.app.component.Label labelPaper;
            private com.ultreon.devices.api.app.component.Label labelPosition;
            private com.ultreon.devices.api.app.component.Button buttonClose;

            private Info(NetworkDevice entry) {
                this.entry = entry;
                this.setTitle("Details");
            }

            @Override
            public void init(@Nullable CompoundTag intent) {
                super.init(intent);

                layoutMain = new Layout(120, 70);

                labelName = new com.ultreon.devices.api.app.component.Label(ChatFormatting.GOLD.toString() + ChatFormatting.BOLD + entry.getName(), 5, 5);
                layoutMain.addComponent(labelName);

                labelPaper = new com.ultreon.devices.api.app.component.Label(ChatFormatting.DARK_GRAY + "Paper: " + ChatFormatting.RESET + 0, 5, 18); //TODO fix paper count
                labelPaper.setAlignment(Component.ALIGN_LEFT);
                labelPaper.setShadow(false);
                layoutMain.addComponent(labelPaper);

                assert entry.getPos() != null;
                String position = ChatFormatting.DARK_GRAY + "X: " + ChatFormatting.RESET + entry.getPos().getX() + " " + ChatFormatting.DARK_GRAY + "Y: " + ChatFormatting.RESET + entry.getPos().getY() + " " + ChatFormatting.DARK_GRAY + "Z: " + ChatFormatting.RESET + entry.getPos().getZ();
                labelPosition = new com.ultreon.devices.api.app.component.Label(position, 5, 30);
                labelPosition.setShadow(false);
                layoutMain.addComponent(labelPosition);

                buttonClose = new Button(5, 49, "Close");
                buttonClose.setClickListener((mouseX, mouseY, mouseButton) -> {
                    if (mouseButton == 0) {
                        close();
                    }
                });
                layoutMain.addComponent(buttonClose);

                setLayout(layoutMain);
            }
        }
    }

}
