package mastef_chief.gitwebbuilder.app;

import com.mojang.blaze3d.platform.ClipboardManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ultreon.devices.api.app.Application;
import com.ultreon.devices.api.app.Dialog;
import com.ultreon.devices.api.app.Icons;
import com.ultreon.devices.api.app.ScrollableLayout;
import com.ultreon.devices.api.app.component.Button;
import com.ultreon.devices.api.app.component.*;
import com.ultreon.devices.api.app.component.Image;
import com.ultreon.devices.api.app.component.Label;
import com.ultreon.devices.api.app.component.TextArea;
import com.ultreon.devices.api.app.interfaces.IHighlight;
import com.ultreon.devices.api.io.File;
import com.ultreon.devices.api.task.TaskManager;
import com.ultreon.devices.api.utils.OnlineRequest;
import com.ultreon.devices.api.utils.RenderUtil;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.core.io.FileSystem;
import com.ultreon.devices.programs.gitweb.component.GitWebFrame;
import com.ultreon.devices.programs.system.layout.StandardLayout;
import mastef_chief.gitwebbuilder.app.components.MenuButton;
import mastef_chief.gitwebbuilder.app.components.ModuleCreatorDialog;
import mastef_chief.gitwebbuilder.app.components.PasteBinCompleteDialog;
//import mastef_chief.gitwebbuilder.app.models.GWBLogoModel;
import mastef_chief.gitwebbuilder.app.models.GWBLogoModel;
import mastef_chief.gitwebbuilder.app.tasks.TaskNotificationCopiedCode;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.joml.Quaternionf;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.Predicate;

public class GWBApp extends Application {

    //Todo module dialog, scrollable layout for design view, custom layouts which take in module instance, which type of module

    private static final Predicate<File> PREDICATE_FILE_SITE = file -> !file.isFolder()
            && file.getData().contains("content", Tag.TAG_STRING);

    public static final IHighlight CODE_HIGHLIGHT = text ->
    {
        if (text.startsWith("#"))
            return asArray(ChatFormatting.GREEN);

        if (text.startsWith("\"") && text.endsWith("\""))
            return asArray(ChatFormatting.AQUA);

        switch (text) {
            case "text":
            case "image":
                return asArray(ChatFormatting.BLUE);
            default:
                return asArray(ChatFormatting.WHITE);
        }
    };


    Minecraft mc = Minecraft.getInstance();

    Toolkit toolkit = Toolkit.getDefaultToolkit();

    private GitWebFrame liveGitWebFrame;

    private float rotationCounter = 0;
    private int tickCounter = 0;

    //Todo work on save method to enable and disable save button
    private String savedData;

    private boolean isSaved;
    private boolean autoSave = true;

    private File currentFile;

    private StandardLayout layoutMain;
    private StandardLayout layoutSettings;
    private StandardLayout layoutCodeView;
    private StandardLayout layoutDesignView;
    private StandardLayout layoutLiveView;

    private ScrollableLayout moduleSelctionLayout;

    private MenuButton newSiteButton;
    private MenuButton loadSiteButton;
    private MenuButton settingsButton;
    private MenuButton loadSiteFromListButton;
    private Button backToMenuButton1;
    private Button saveAsSiteButton;
    private Button saveSiteButton;
    private Button exportToPastebinButton;
    private Button importButton;
    private Button copyToClipboardButton;

    //Module Buttons
    private Button paragraphModuleButton;
    private Button navigationModuleButton;
    private Button brewingModuleButton;
    private Button downloadModuleButton;
    private Button furnaceModuleButton;
    private Button footerModuleButton;
    private Button dividerModuleButton;
    private Button craftingModuleButton;
    private Button anvilModuleButton;
    private Button headerModuleButton;
    private Button bannerModuleButton;

    //16 Color Buttons
    private Button[] formattingButtons = new Button[ChatFormatting.values().length - 1];

    //Formatting Buttons
    private Button resetButton;

    private ComboBox.List<String> textFormattingSelectionList;

    private ItemList<Sites> sitesList;

    private Label recentSitesLabel;
    private Label autoSaveLabel;


    private CheckBox autoSaveOnCheckBox;
    private CheckBox autoSaveOffCheckBox;
    private CheckBox codeViewCheckBox;
    private CheckBox designViewCheckBox;
    private CheckBox liveViewCheckBox;

    private TextArea siteBuilderTextArea;

    private GWBLogoModel gwbLogoModel = null;

    private static final ResourceLocation logo = new ResourceLocation("gitwebbuilder:textures/app/gui/logo.png");
    private static final ResourceLocation ud = new ResourceLocation("gitwebbuilder:textures/app/gui/ud.png");

    /**
     * The default initialization method. Clears any components in the default
     * layout and sets it as the current layout. If you override this method and
     * are using the default layout, make sure you call it using
     * <code>super.init(x, y)</code>
     * <p>
     * The parameters passed are the x and y location of the top left corner or
     * your application window.
     */
    @Override
    public void init(CompoundTag nbtTagCompound) {

        /*--------------------------------------------------------------------------*/
        layoutMain = new StandardLayout("Menu", 363, 165, this, null);
        layoutMain.setIcon(Icons.HOME);
        layoutMain.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
        {
            Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
            gui.fill(x, y + 21, x + width, y + 164, Color.GRAY.getRGB());
        });

        layoutMain.setInitListener(() ->
        {
            sitesList.getItems().clear();
            FileSystem.getApplicationFolder(this, (folder, success) ->
            {
                if (success) {
                    folder.search(file -> file.isForApplication(this)).forEach(file ->
                    {
                        sitesList.addItem(Sites.fromFile(file));
                    });
                } else {
                    this.openDialog(new Dialog.Message("Error creating app directory"));
                }
            });
        });

        newSiteButton = new MenuButton(160, 45, 75, 16, "New Site", Icons.NEW_FILE);
        newSiteButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                this.setCurrentLayout(layoutCodeView);
                siteBuilderTextArea.setFocused(true);
            }
        });

        layoutMain.addComponent(newSiteButton);
        loadSiteButton = new MenuButton(185, 85, 75, 16, "Load Site", Icons.LOAD);
        loadSiteButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                Dialog.OpenFile openDialog = new Dialog.OpenFile(this);
                openDialog.setResponseHandler((success, file) ->
                {
                    if (file.isForApplication(this)) {
                        CompoundTag data = file.getData();
                        siteBuilderTextArea.setText(data.getString("content").replace("\n\n", "\n"));
                        currentFile = file;
                        //Todo Testing Code
                        /*saveSiteButton.setEnabled(false);
                        savedData = siteBuilderTextArea.getText();*/
                        this.setCurrentLayout(layoutCodeView);
                        return true;
                    } else {
                        Dialog.Message errorDialog = new Dialog.Message("Invalid file for GitWeb Builder");
                        openDialog(errorDialog);
                    }
                    return false;
                });
                this.openDialog(openDialog);
            }
        });

        layoutMain.addComponent(loadSiteButton);

        settingsButton = new MenuButton(210, 125, 75, 16, "Settings", Icons.WRENCH);
        settingsButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                //this.setCurrentLayout(layoutSettings);
            }
        });
        layoutMain.addComponent(settingsButton);

        recentSitesLabel = new Label("\u00A7n\u00A7lRecent Sites", 273, 29);
        layoutMain.addComponent(recentSitesLabel);

        sitesList = new ItemList(265, 40, 90, 5);
        sitesList.setItemClickListener((e, index, mouseButton) -> {
            if (mouseButton == 0) {
                loadSiteFromListButton.setEnabled(true);
            }
        });
        layoutMain.addComponent(sitesList);

        loadSiteFromListButton = new MenuButton(265, 110, 90, 16, "Load", Icons.LOAD);
        loadSiteFromListButton.setEnabled(false);
        loadSiteFromListButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                if (sitesList.getSelectedIndex() != -1) {
                    Sites sites = sitesList.getSelectedItem();
                    siteBuilderTextArea.setText(sites.getContent().replace("\n\n", "\n"));
                    currentFile = sites.getSource();
                    setCurrentLayout(layoutCodeView);
                }
            }
        });
        layoutMain.addComponent(loadSiteFromListButton);

        this.setCurrentLayout(layoutMain);
        /*----------------------------------------------------------------------------------------------------------------------------------------*/

        layoutSettings = new StandardLayout("Settings", 363, 165, this, layoutMain) {
            @Override
            protected void handleUnload() {
                super.handleUnload();
            }
        };
        layoutSettings.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
        {
            Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
            gui.fill(x, y + 21, x + width, y + 164, Color.GRAY.getRGB());
        });

        int autoSaveX = 10;
        int autoSaveY = 30;
        autoSaveLabel = new Label("\u00A7n\u00A7lAuto Save", autoSaveX, autoSaveY);
        layoutSettings.addComponent(autoSaveLabel);
        RadioGroup autoSaveToggle = new RadioGroup();

        autoSaveOnCheckBox = new CheckBox("On", autoSaveX, autoSaveY + 15);
        autoSaveOnCheckBox.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                autoSave = true;
                markDirty();
            }
        });
        autoSaveOnCheckBox.setRadioGroup(autoSaveToggle);
        layoutSettings.addComponent(autoSaveOnCheckBox);

        autoSaveOffCheckBox = new CheckBox("Off", autoSaveX + 30, autoSaveY + 15);
        autoSaveOffCheckBox.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                autoSave = false;
                markDirty();
            }
        });
        autoSaveOffCheckBox.setRadioGroup(autoSaveToggle);
        layoutSettings.addComponent(autoSaveOffCheckBox);


        /*----------------------------------------------------------------------------------------------------------------------------------------*/

        layoutCodeView = new StandardLayout("Code View", 363, 165, this, null);
        layoutCodeView.setIcon(Icons.EARTH);

        backToMenuButton1 = new Button(100, 2, Icons.ARROW_LEFT);
        backToMenuButton1.setToolTip("Back To Menu", "Will take you back to the main menu");
        backToMenuButton1.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                if (currentFile == null) {
                    if (siteBuilderTextArea.getText().isEmpty()) {
                        this.setCurrentLayout(layoutMain);
                    } else {
                        Dialog.Confirmation saveCheckDialog = new Dialog.Confirmation("You have not saved your site, would you like to save?");
                        saveCheckDialog.setPositiveText("Yes");
                        saveCheckDialog.setNegativeText("No");
                        this.openDialog(saveCheckDialog);
                        saveCheckDialog.setPositiveListener((mouseX1, mouseY1, mouseButton1) -> {
                            if (mouseButton1 == 0) {
                                CompoundTag data = new CompoundTag();
                                data.putString("content", siteBuilderTextArea.getText());
                                Dialog.SaveFile saveDialog = new Dialog.SaveFile(this, data);
                                this.openDialog(saveDialog);
                                saveDialog.setResponseHandler((success, file) -> {
                                    siteBuilderTextArea.clear();
                                    this.setCurrentLayout(layoutMain);
                                    return true;
                                });
                            }
                        });
                        saveCheckDialog.setNegativeListener((mouseX1, mouseY1, mouseButton1) -> {
                            if (mouseButton1 == 0) {
                                siteBuilderTextArea.clear();
                                this.setCurrentLayout(layoutMain);
                            }
                        });
                    }
                } else {
                    if (currentFile.getData().getString("content").equals(siteBuilderTextArea.getText())) {
                        siteBuilderTextArea.clear();
                        this.setCurrentLayout(layoutMain);
                        currentFile = null;
                    } else {
                        Dialog.Confirmation saveCheckDialog = new Dialog.Confirmation("You have unsaved changes, would you like to save?");
                        this.openDialog(saveCheckDialog);
                        saveCheckDialog.setPositiveListener((mouseX1, mouseY1, mouseButton1) -> {
                            if (mouseButton1 == 0) {
                                CompoundTag data = new CompoundTag();
                                data.putString("content", siteBuilderTextArea.getText());
                                currentFile.setData(data, (v, success) -> {
                                });
                                siteBuilderTextArea.clear();
                                this.setCurrentLayout(layoutMain);
                                currentFile = null;
                            }
                        });
                        saveCheckDialog.setNegativeListener((mouseX2, mouseY2, mouseButton2) -> {
                            if (mouseButton2 == 0) {
                                saveCheckDialog.close();
                                siteBuilderTextArea.clear();
                                this.setCurrentLayout(layoutMain);
                                currentFile = null;
                            }
                        });
                    }
                }
            }
        });
        layoutCodeView.addComponent(backToMenuButton1);

        saveAsSiteButton = new Button(118, 2, Icons.SAVE);
        saveAsSiteButton.setToolTip("Save As", "Saves your site to a new file");
        saveAsSiteButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                CompoundTag data = new CompoundTag();
                data.putString("content", siteBuilderTextArea.getText());

                Dialog.SaveFile saveDialog = new Dialog.SaveFile(this, data);
                saveDialog.setFolder(getApplicationFolderPath());
                saveDialog.setResponseHandler((success, file) -> {
                    currentFile = file;
                    return true;
                });
                this.openDialog(saveDialog);
            }
        });
        layoutCodeView.addComponent(saveAsSiteButton);

        saveSiteButton = new Button(136, 2, Icons.SAVE);
        saveSiteButton.setToolTip("Save", "Saves your site to the current file");
        saveSiteButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                if (currentFile != null) {
                    CompoundTag data = new CompoundTag();
                    data.putString("content", siteBuilderTextArea.getText());
                    currentFile.setData(data, (v, success) -> {
                        if (success) {

                        }
                    });
                } else {
                    CompoundTag data = new CompoundTag();
                    data.putString("content", siteBuilderTextArea.getText());

                    Dialog.SaveFile saveDialog = new Dialog.SaveFile(this, data);
                    saveDialog.setFolder(getApplicationFolderPath());
                    saveDialog.setResponseHandler((success, file) -> {
                        currentFile = file;
                        return true;
                    });
                    this.openDialog(saveDialog);
                }

            }
        });
        layoutCodeView.addComponent(saveSiteButton);
        exportToPastebinButton = new Button(154, 2, Icons.EXPORT);
        exportToPastebinButton.setToolTip("Export To  PasteBin", "Exports code to GitWeb Buidler's Pastebin");
        exportToPastebinButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                Dialog.Input exportDialog = new Dialog.Input("Site Title:");
                exportDialog.setTitle("Export Site");
                exportDialog.setPositiveText("Export");
                this.openDialog(exportDialog);
                exportDialog.setResponseHandler((success, v) ->
                {
                    if (success) {
                        createPastebin(exportDialog.getTextFieldInput().getText(), siteBuilderTextArea.getText().replace("\n\n", "\n").replace("&", "%26"));
                    }
                    return true;
                });
            }
        });
        layoutCodeView.addComponent(exportToPastebinButton);

        importButton = new Button(172, 2, Icons.IMPORT);
        importButton.setToolTip("Import", "Import an existing site into GitWeb Builder");
        importButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {

                Dialog.Input importDialog = new Dialog.Input("Insert URL to the raw text of the site");
                importDialog.setTitle("Import Site");
                importDialog.setPositiveText("Import");
                this.openDialog(importDialog);
                importDialog.setResponseHandler((success, s) -> {
                    if (success) {

                        OnlineRequest.getInstance().make(importDialog.getTextFieldInput().getText().toString(), (success1, response) -> {
                            if (success1) {
                                siteBuilderTextArea.setText(response.replace("§", "&"));
                            }
                        });
                        importDialog.close();
                    }
                    return false;
                });


            }
        });
        layoutCodeView.addComponent(importButton);

        copyToClipboardButton = new Button(190, 2, Icons.COPY);
        copyToClipboardButton.setToolTip("Copy to Clipboard", "Copy's code to clipboard with correct formatting for GitWeb");
        copyToClipboardButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                TextFieldHelper.setClipboardContents(this.mc, siteBuilderTextArea.getText().replace("\n\n", "\n"));
                TaskManager.sendTask(new TaskNotificationCopiedCode());
            }
        });
        layoutCodeView.addComponent(copyToClipboardButton);

        RadioGroup viewGroup = new RadioGroup();

        codeViewCheckBox = new CheckBox("Code", 240, 5);
        codeViewCheckBox.setSelected(true);
        codeViewCheckBox.setRadioGroup(viewGroup);
        codeViewCheckBox.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {

                this.setCurrentLayout(layoutCodeView);

            }
        });
        layoutCodeView.addComponent(codeViewCheckBox);

        designViewCheckBox = new CheckBox("Design", 280, 5);
        designViewCheckBox.setRadioGroup(viewGroup);
        designViewCheckBox.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {

                this.setCurrentLayout(layoutDesignView);

            }
        });
        layoutCodeView.addComponent(designViewCheckBox);

        liveViewCheckBox = new CheckBox("Live", 327, 5);
        liveViewCheckBox.setRadioGroup(viewGroup);
        liveViewCheckBox.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                try {
                    liveGitWebFrame.loadRaw(renderFormatting(siteBuilderTextArea.getText()));

                } catch (NumberFormatException e) {
                    this.openDialog(new Dialog.Message(ChatFormatting.RED + "Error \n" + ChatFormatting.RESET + e.getLocalizedMessage() + "\nCheck logs for more info." ));
                    e.printStackTrace();

                }

                this.setCurrentLayout(layoutLiveView);


            }
        });
        layoutCodeView.addComponent(liveViewCheckBox);

        siteBuilderTextArea = new TextArea(0, 21, layoutCodeView.width - 75, layoutCodeView.height - 22);
        //siteBuilderTextArea.setHighlight(CODE_HIGHLIGHT);
        layoutCodeView.addComponent(siteBuilderTextArea);

        for (int i = 0; i < formattingButtons.length; i++) {
            int x = 290 + (i % 4) * 18;
            int y = 39 + (i / 4) * 18;
            final int index = i;
            Button button = new Button(x, y, 16, 16, ChatFormatting.values()[i] + "A");
            button.setClickListener((mouseX, mouseY, mouseButton) -> {
                if (mouseButton == 0) {
                    siteBuilderTextArea.writeText("&" + ChatFormatting.values()[index].toString().substring(1));
                    siteBuilderTextArea.setFocused(true);
                }
            });
            layoutCodeView.addComponent(button);
            formattingButtons[i] = button;
        }

        String[] formattingType = new String[]{"Formatting", "Modules"};
        textFormattingSelectionList = new ComboBox.List<>(290, 23, 70, formattingType);
        textFormattingSelectionList.setChangeListener((oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                if (newValue.equals("Formatting")) {
                    for (Button formattingButton : formattingButtons) {
                        formattingButton.setVisible(true);
                    }
                    resetButton.setVisible(true);
                    moduleSelctionLayout.setVisible(false);
                } else if (newValue.equals("Modules")) {
                    for (Button formattingButton : formattingButtons) {
                        formattingButton.setVisible(false);
                    }
                    resetButton.setVisible(false);
                    moduleSelctionLayout.setVisible(true);
                    moduleSelctionLayout.resetScroll();
                }
            }
        });
        layoutCodeView.addComponent(textFormattingSelectionList);

        resetButton = new Button(308, 129, 52, 16, "Reset");
        resetButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                siteBuilderTextArea.writeText("&r");
                siteBuilderTextArea.setFocused(true);
            }
        });
        layoutCodeView.addComponent(resetButton);

        moduleSelctionLayout = new ScrollableLayout(290, 39, 70, 200, 123);
        moduleSelctionLayout.setVisible(false);
        moduleSelctionLayout.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
        {
            Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
            gui.fill(x, y, x + 100, y + 300, Color.gray.getRGB());
        });
        layoutCodeView.addComponent(moduleSelctionLayout);

        paragraphModuleButton = new Button(1, 1, 62, 16, "Paragraph");
        paragraphModuleButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                ModuleCreatorDialog moduleCreatorDialog = new ModuleCreatorDialog("Paragraph", siteBuilderTextArea);
                this.openDialog(moduleCreatorDialog);
            }
        });
        moduleSelctionLayout.addComponent(paragraphModuleButton);

        navigationModuleButton = new Button(1, 19, 62, 16, "Navigation");
        navigationModuleButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                ModuleCreatorDialog moduleCreatorDialog = new ModuleCreatorDialog("Navigation", siteBuilderTextArea);
                this.openDialog(moduleCreatorDialog);
            }
        });
        moduleSelctionLayout.addComponent(navigationModuleButton);

        brewingModuleButton = new Button(1, 37, 62, 16, "Brewing");
        brewingModuleButton.setToolTip("Unavailable", "This module is currently being worked on.");
        brewingModuleButton.setEnabled(false);
        brewingModuleButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                ModuleCreatorDialog moduleCreatorDialog = new ModuleCreatorDialog("Brewing", siteBuilderTextArea);
                this.openDialog(moduleCreatorDialog);
            }
        });
        moduleSelctionLayout.addComponent(brewingModuleButton);

        downloadModuleButton = new Button(1, 55, 62, 16, "Download");
        downloadModuleButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                ModuleCreatorDialog moduleCreatorDialog = new ModuleCreatorDialog("Download", siteBuilderTextArea);
                this.openDialog(moduleCreatorDialog);
            }
        });
        moduleSelctionLayout.addComponent(downloadModuleButton);

        furnaceModuleButton = new Button(1, 73, 62, 16, "Furnace");
        furnaceModuleButton.setToolTip("Unavailable", "This module is currently being worked on.");
        furnaceModuleButton.setEnabled(false);
        moduleSelctionLayout.addComponent(furnaceModuleButton);

        footerModuleButton = new Button(1, 91, 62, 16, "Footer");
        footerModuleButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                ModuleCreatorDialog moduleCreatorDialog = new ModuleCreatorDialog("Footer", siteBuilderTextArea);
                this.openDialog(moduleCreatorDialog);
            }
        });
        moduleSelctionLayout.addComponent(footerModuleButton);

        dividerModuleButton = new Button(1, 109, 62, 16, "Divider");
        dividerModuleButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                ModuleCreatorDialog moduleCreatorDialog = new ModuleCreatorDialog("Divider", siteBuilderTextArea);
                this.openDialog(moduleCreatorDialog);
            }
        });
        moduleSelctionLayout.addComponent(dividerModuleButton);

        craftingModuleButton = new Button(1, 127, 62, 16, "Crafting");
        craftingModuleButton.setToolTip("Unavailable", "This module is currently being worked on.");
        craftingModuleButton.setEnabled(false);
        moduleSelctionLayout.addComponent(craftingModuleButton);

        anvilModuleButton = new Button(1, 145, 62, 16, "Anvil");
        anvilModuleButton.setToolTip("Unavailable", "This module is currently being worked on.");
        anvilModuleButton.setEnabled(false);
        moduleSelctionLayout.addComponent(anvilModuleButton);

        headerModuleButton = new Button(1, 163, 62, 16, "Header");
        headerModuleButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                ModuleCreatorDialog moduleCreatorDialog = new ModuleCreatorDialog("Header", siteBuilderTextArea);
                this.openDialog(moduleCreatorDialog);
            }
        });
        moduleSelctionLayout.addComponent(headerModuleButton);

        bannerModuleButton = new Button(1, 181, 62, 16, "Banner");
        bannerModuleButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                ModuleCreatorDialog moduleCreatorDialog = new ModuleCreatorDialog("Banner", siteBuilderTextArea);
                this.openDialog(moduleCreatorDialog);
            }
        });
        moduleSelctionLayout.addComponent(bannerModuleButton);



        //Todo Add search for code

        /*---------------------------------------------------------------------------------------------------------------*/

        layoutDesignView = new StandardLayout("Design View", 363, 165, this, null);
        layoutDesignView.setIcon(Icons.EDIT);
        layoutDesignView.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
        {
            Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
            gui.fill(x, y + 21, x + width, y + 164, Color.darkGray.getRGB());
        });

        layoutDesignView.addComponent(codeViewCheckBox);
        layoutDesignView.addComponent(designViewCheckBox);
        layoutDesignView.addComponent(liveViewCheckBox);



        //Todo work on design view

        /*---------------------------------------------------------------------------------------------------------------*/

        layoutLiveView = new StandardLayout("Live View", 363, 165, this, null);
        layoutLiveView.setIcon(Icons.PLAY);
        layoutLiveView.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
        {
            Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
            gui.fill(x, y + 21, x + width, y + 164, Color.GRAY.getRGB());
        });

        layoutLiveView.addComponent(codeViewCheckBox);
        layoutLiveView.addComponent(designViewCheckBox);
        layoutLiveView.addComponent(liveViewCheckBox);

        liveGitWebFrame = new GitWebFrame(this, 0, 21, layoutLiveView.width, layoutLiveView.height - 22);

        layoutLiveView.addComponent(liveGitWebFrame);
    }



    @Override
    public void render(GuiGraphics graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active, float partialTicks) {

        super.render(graphics, laptop, mc, x, y, mouseX, mouseY, active, partialTicks);


        if (this.getCurrentLayout() == layoutMain) {
            if (gwbLogoModel == null) {
                gwbLogoModel = new GWBLogoModel(GWBLogoModel.createContext().bakeLayer(GWBLogoModel.LAYER_LOCATION));
            }
            graphics.pose().pushPose();

            MultiBufferSource.BufferSource immediate = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            VertexConsumer vertexConsumer = immediate.getBuffer(this.gwbLogoModel.renderType(logo));
            gwbLogoModel.renderToBuffer(graphics.pose(), vertexConsumer, 0xF000F0, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
            immediate.endBatch();
//            {
//                RenderSystem.enableDepthTest();
//                Lighting.setupForFlatItems();
//                graphics.pose().translate(x + 135, y - 43, 250);
//                graphics.pose().scale((float) -8.0, (float) -8.0, (float) -8.0);
//                graphics.pose().mulPose(new Quaternionf( 5F, 1, 0, 0));
//                graphics.pose().mulPose(new Quaternionf(200F, 0, 0, 1));
//                graphics.pose().mulPose(new Quaternionf(-rotationCounter - partialTicks, 0, 1, 0));
//                RenderSystem.setShaderTexture(0, logo);
//
//                Lighting.setupFor3DItems();
//                RenderSystem.disableDepthTest();
//
//            }
            graphics.pose().popPose();
        }

        if (this.getCurrentLayout() == layoutDesignView) {

            RenderSystem.setShaderTexture(0, ud);
            RenderUtil.drawRectWithTexture(ud, graphics, (double) (x + 46), (double) (y + 19), 0.0F, 0.0F, 250, 145, 250, 250);

        }


    }

    @Override
    public void onTick() {
        super.onTick();

        rotationCounter++;
        rotationCounter %= 360;


        if (autoSave) {
            if (this.getCurrentLayout().equals(layoutCodeView) || this.getCurrentLayout().equals(layoutDesignView)) {
                if (currentFile != null) {
                    if (tickCounter == 100) {
                        CompoundTag data = new CompoundTag();
                        data.putString("content", siteBuilderTextArea.getText());
                        currentFile.setData(data, (v, success) -> {
                        });
                        tickCounter = 0;
                    }
                    tickCounter++;
                }
            }
        }
    }

    public void createPastebin(String title, String code) {
        String apikey = "12bfae53f22fc3d7bd73eb515f5a147f";
        String option = "paste";
        try {
            URL url = new URL("https://pastebin.com/api/api_post.php");
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

            writer.write("api_dev_key=" + apikey + "&api_option=" + option + "&api_paste_code=" + code + "&api_paste_name=" + title + "&api_paste_private=" + "0");
            writer.flush();
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = reader.readLine()) != null) {
                this.openDialog(new PasteBinCompleteDialog(line));
            }
            writer.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public String renderFormatting(String content) {

        return content
                //new line fix
                .replace("\n\n", "\n")
                //color conversion to unicode format
                .replace("&0", "\u00A70").replace("&1", "\u00A71").replace("&2", "\u00A72").replace("&3", "\u00A73")
                .replace("&4", "\u00A74").replace("&5", "\u00A75").replace("&6", "\u00A76").replace("&7", "\u00A77")
                .replace("&8", "\u00A78").replace("&9", "\u00A79").replace("&a", "\u00A7a").replace("&b", "\u00A7b")
                .replace("&c", "\u00A7c").replace("&d", "\u00A7d").replace("&e", "\u00A7e").replace("&f", "\u00A7f")
                //Formatting conversion to unicode format
                .replace("&k", "\u00A7k").replace("&l", "\u00A7l").replace("&m", "\u00A7m").replace("&n", "\u00A7n")
                .replace("&o", "\u00A7o").replace("&r", "\u00A7r");

    }

    @Override
    public void handleKeyTyped(char character, int code) {
        super.handleKeyTyped(character, code);
        /* TODO

        if (this.getCurrentLayout().equals(layoutCodeView)) {
            if (GuiScreen.isCtrlKeyDown() && code == Keyboard.KEY_S) {

                if (currentFile != null) {
                    NBTTagCompound data = new NBTTagCompound();
                    data.setString("content", siteBuilderTextArea.getText());
                    currentFile.setData(data, (v, success) -> {
                        if (success) {

                        }
                    });
                } else {
                    NBTTagCompound data = new NBTTagCompound();
                    data.setString("content", siteBuilderTextArea.getText());

                    Dialog.SaveFile saveDialog = new Dialog.SaveFile(this, data);
                    saveDialog.setFolder(getApplicationFolderPath());
                    saveDialog.setResponseHandler((success, file) -> {
                        currentFile = file;
                        return true;
                    });
                    this.openDialog(saveDialog);
                }
            }
            if (GuiScreen.isCtrlKeyDown() && GuiScreen.isShiftKeyDown() && code == Keyboard.KEY_S) {

                NBTTagCompound data = new NBTTagCompound();
                data.setString("content", siteBuilderTextArea.getText());

                Dialog.SaveFile saveDialog = new Dialog.SaveFile(this, data);
                saveDialog.setFolder(getApplicationFolderPath());
                saveDialog.setResponseHandler((success, file) -> {
                    currentFile = file;
                    return true;
                });
                this.openDialog(saveDialog);
            }
        }

        /*if (code == Keyboard.KEY_DELETE) {
            siteBuilderTextArea.moveCursorRight(1);
            siteBuilderTextArea.performBackspace();
        }*//*

        if (this.getCurrentLayout().equals(layoutCodeView) || this.getCurrentLayout().equals(layoutDesignView) || this.getCurrentLayout().equals(layoutLiveView)) {
            if (GuiScreen.isCtrlKeyDown()) {
                if (code == Keyboard.KEY_LEFT) {
                    if (this.getCurrentLayout().equals(layoutCodeView)) {
                        liveGitWebFrame.loadRaw(renderFormatting(siteBuilderTextArea.getText()));
                        this.setCurrentLayout(layoutLiveView);
                        codeViewCheckBox.setSelected(false);
                        designViewCheckBox.setSelected(false);
                        liveViewCheckBox.setSelected(true);
                    } else if (this.getCurrentLayout().equals(layoutDesignView)) {
                        this.setCurrentLayout(layoutCodeView);
                        codeViewCheckBox.setSelected(true);
                        designViewCheckBox.setSelected(false);
                        liveViewCheckBox.setSelected(false);
                    } else if (this.getCurrentLayout().equals(layoutLiveView)) {
                        this.setCurrentLayout(layoutDesignView);
                        codeViewCheckBox.setSelected(false);
                        designViewCheckBox.setSelected(true);
                        liveViewCheckBox.setSelected(false);
                    }
                }

                if (code == Keyboard.KEY_RIGHT) {
                    if (this.getCurrentLayout().equals(layoutCodeView)) {
                        this.setCurrentLayout(layoutDesignView);
                        codeViewCheckBox.setSelected(false);
                        designViewCheckBox.setSelected(true);
                        liveViewCheckBox.setSelected(false);
                    } else if (this.getCurrentLayout().equals(layoutDesignView)) {
                        liveGitWebFrame.loadRaw(renderFormatting(siteBuilderTextArea.getText()));
                        this.setCurrentLayout(layoutLiveView);
                        codeViewCheckBox.setSelected(false);
                        designViewCheckBox.setSelected(false);
                        liveViewCheckBox.setSelected(true);
                    } else if (this.getCurrentLayout().equals(layoutLiveView)) {
                        this.setCurrentLayout(layoutCodeView);
                        codeViewCheckBox.setSelected(true);
                        designViewCheckBox.setSelected(false);
                        liveViewCheckBox.setSelected(false);
                    }
                }

            }
        }

*/
    }

    @Override
    public boolean handleFile(File file) {
        if (!PREDICATE_FILE_SITE.test(file))
            return false;

        currentFile = file;

        CompoundTag data = file.getData();
        siteBuilderTextArea.setText(data.getString("content").replace("\n\n", "\n"));
        this.setCurrentLayout(layoutCodeView);

        return true;

    }

    @Override
    public void onClose() {
        super.onClose();


        //Todo add save on close if not saved already


        currentFile = null;


    }

    private static <T extends Object> T[] asArray(T... t) {
        return t;
    }


    @Override
    public void load(CompoundTag tagCompound) {

        if (tagCompound.contains("autoSave", Tag.TAG_BYTE)) {
            autoSave = tagCompound.getBoolean("autoSave");
        }

        //Todo fix issue with autosave in settings
        if (autoSave) {
            autoSaveOnCheckBox.setSelected(true);
        } else {
            autoSaveOffCheckBox.setSelected(true);
        }

    }

    /**
     * Allows you to save data from your application. This is only called if
     * {@link #isDirty()} returns true. You can mark your application as dirty
     * by calling {@link #markDirty()}.
     *
     * @param tagCompound the tag compound to save your data to
     */
    @Override
    public void save(CompoundTag tagCompound) {

        tagCompound.putBoolean("autoSave", autoSave);

    }

    private static class Sites {
        private File source;
        private String fileName;
        private String content;

        public Sites(String fileName, String content) {
            this.fileName = fileName;
            this.content = content;
        }

        public File getSource() {
            return this.source;
        }

        public String getFileName() {
            return this.fileName;
        }

        public String getContent() {
            return this.content;
        }

        public String toString() {
            return this.fileName;
        }

        public static Sites fromFile(File file) {
            Sites note = new Sites(file.getName(), file.getData().getString("content"));
            note.source = file;
            return note;
        }
    }

}
