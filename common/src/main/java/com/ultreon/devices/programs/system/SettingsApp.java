package com.ultreon.devices.programs.system;

import com.google.common.base.CaseFormat;
import com.ultreon.devices.Devices;
import com.ultreon.devices.Reference;
import com.ultreon.devices.api.ApplicationManager;
import com.ultreon.devices.api.app.Dialog;
import com.ultreon.devices.api.app.Icons;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.api.app.ScrollableLayout;
import com.ultreon.devices.api.app.component.*;
import com.ultreon.devices.api.app.component.Button;
import com.ultreon.devices.api.app.renderer.ItemRenderer;
import com.ultreon.devices.api.app.renderer.ListItemRenderer;
import com.ultreon.devices.api.utils.OnlineRequest;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.core.Settings;
import com.ultreon.devices.object.AppInfo;
import com.ultreon.devices.object.TrayItem;
import com.ultreon.devices.programs.system.component.Palette;
import com.ultreon.devices.programs.system.object.ColorScheme;
import com.ultreon.devices.programs.system.object.ColorSchemePresetRegistry;
import com.ultreon.devices.programs.system.object.Preset;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.awt.*;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Stack;

@SuppressWarnings("FieldCanBeLocal")
public class SettingsApp extends SystemApp {
    private Button backBtn;

    private Layout layoutMain;
    private Layout layoutGeneral;
    private CheckBox checkBoxShowApps;

    private Layout layoutPersonalise;
    private Layout layoutWallpaper;
    private Button prevWallpaperBtn;
    private Button nextWallpaperBtn;
    private Button urlWallpaperBtn;

    private Layout layoutColorScheme;
    private Layout layoutColorSchemes;
    private Button buttonColorSchemeApply;

    private final Stack<Layout> predecessor = new Stack<>();
    private ComboBox.List<PredefinedResolution> comboDisplayResolutions;

    private void resetColorSchemeClick(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            Laptop.getSystem().getSettings().getColorScheme().resetDefault();
        }
    }

    @Override
    public void init(@Nullable CompoundTag intent) {
        backBtn = new Button(2, 2, Icons.ARROW_LEFT);
        backBtn.setVisible(false);
        backBtn.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if (mouseButton == 0) {
                if (!predecessor.isEmpty()) {
                    setCurrentLayout(predecessor.pop());
                }
                if (predecessor.isEmpty()) {
                    backBtn.setVisible(false);
                }
            }
        });

        layoutMain = addMainLayout();
        setCurrentLayout(layoutMain);
    }

    /**
     * Creates the main layout of the settings app
     *
     * @return the main layout.
     */
    private Menu addMainLayout() {
        Menu layoutMain = new Menu("Home");

        Button aboutButton = createAboutButton(layoutMain);
        layoutMain.addComponent(aboutButton);
        //aboutButton.setToolTip("About", "When to call emergency services because you just lost all of your NFTs to a scammer");

        this.layoutPersonalise = createPersonaliseLayout();
        this.layoutColorSchemes = createColorSchemesLayout();
        this.layoutGeneral = createGeneralLayout();

        Button buttonColorScheme = new Button(5, 26+20+4, "Personalise", Icons.EDIT);
        buttonColorScheme.setSize(90, 20);
        buttonColorScheme.setToolTip("Personalise", "Change the wallpaper, UI colors, and more!");
        buttonColorScheme.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                showMenu(layoutPersonalise);
            }
        });

        layoutMain.addComponent(buttonColorScheme);

        Button buttonColorSchemes = new Button(5, 26+26+20+4, "Themes", Icons.WRENCH);
        buttonColorSchemes.setSize(90, 20);
        buttonColorSchemes.setToolTip("Color Schemes", "Change the color scheme using presets or choose a custom one.");
        buttonColorSchemes.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                showMenu(layoutColorSchemes);
            }
        });
        layoutMain.addComponent(buttonColorSchemes);

        Button buttonGeneral = new Button(5, 26+26+26+20+4, "Advanced", Icons.WRENCH);
        buttonGeneral.setSize(90, 20);
        buttonGeneral.setToolTip("General", "General settings.");
        buttonGeneral.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                showMenu(layoutGeneral);
            }
        });
        layoutMain.addComponent(buttonGeneral);

        return layoutMain;
    }

    @NotNull
    private Button createAboutButton(Menu layoutMain) {
        Button aboutButton = new Button(5, 26, "About", Icons.INFO);
        aboutButton.setSize(90, 20);
        aboutButton.setClickListener((mouseX, mouseY, mouseButton) -> {
            var qq = new Menu("About");
            qq.addComponent(backBtn);
            var l = new ScrollableLayout(layoutMain.width, layoutMain.height, 124);
            l.top = 26;
            l = ScrollableLayout.create(0, 26, layoutMain.width, 124, MessageFormat.format("""
                    Version: {0} ({1})
                    """
//                    Model: CD1
//                    STORAGE: 32MB
//                    RAM: 512KB
//
//                    Credits:
//                    - MrCrayfish (https://mrcrayfish.com/)
//                    - Qboi123
//                    - Jab125
//                    - lizterzapzap
//                    - MrBean6000
//                    - them
//                    - alfff
//                    - 6
//                    - あ
/*                    """*/, Reference.getVerInfo()[0], Reference.getVerInfo()[1]));
            //l.height = 124;
            qq.addComponent(l);
            this.showMenu(qq);
        });
        return aboutButton;
    }

    private Layout createGeneralLayout() {
        var layoutGeneral = new Menu("General");
        layoutGeneral.addComponent(backBtn);

        checkBoxShowApps = new CheckBox("Show All Apps", 5, 26);
        checkBoxShowApps.setSelected(Settings.isShowAllApps());
        checkBoxShowApps.setClickListener(this::showAllAppsClick);
        layoutGeneral.addComponent(checkBoxShowApps);

        comboDisplayResolutions = new ComboBox.List<>(5, 26 + 20 + 4, PredefinedResolution.getResolutionList());
        comboDisplayResolutions.setListItemRenderer(new ListItemRenderer<>(20) {
            @Override
            public void render(GuiGraphics graphics, PredefinedResolution resolution, Minecraft mc, int x, int y, int width, int height, boolean selected) {
                graphics.drawString(Minecraft.getInstance().font, resolution.getDisplayName(), x + 5, y + 5, 0xFFFFFF);
            }
        });
        comboDisplayResolutions.setChangeListener((oldValue, newValue) -> {
            if (newValue != null) {
                getLaptop().setDisplayResolution(newValue);
            }
        });

        layoutGeneral.addComponent(comboDisplayResolutions);

        return layoutGeneral;
    }

    /**
     * Create the layout for personalising the laptop
     *
     * @return the menu layout.
     */
    private Layout createPersonaliseLayout() {
        Layout layoutPersonalise = new Menu("Personalise");
        layoutPersonalise.addComponent(backBtn);

        // Wallpaper button on personalise menu.
        Button buttonWallpaper = new Button(5, 26, "Wallpaper", Icons.EDIT);
        buttonWallpaper.setSize(90, 20);
        //buttonWallpaper.top = this.getHeight()-buttonWallpaper.getHeight()-5;
        buttonWallpaper.setToolTip("Wallpaper", "Manage the wallpaper.");
        buttonWallpaper.setClickListener(this::wallpaperClick);
        layoutPersonalise.addComponent(buttonWallpaper);

        //****************************//
        //     Wallpaper settings     //
        //****************************//
        layoutWallpaper = addWallpaperLayout();

        // Reset color scheme button on personalise menu.
        Button buttonReset = new Button(6, 100, "Reset Color Scheme");
        buttonReset.setClickListener(this::resetColorSchemeClick);
        buttonReset.top = layoutPersonalise.height - buttonReset.getHeight() - 5;
        layoutPersonalise.addComponent(buttonReset);
        layoutPersonalise.addComponent(backBtn);

        //***********************//
        //     Color schemes     //
        //***********************//
        layoutColorScheme = createColorSchemeLayout();

        // Reset color scheme button on personalise menu.
        Button buttonColorScheme = new Button(6, 80, "Color scheme");
        buttonColorScheme.setClickListener(this::colorSchemeClick);
        buttonColorScheme.top = layoutPersonalise.height - buttonColorScheme.getHeight() - 25;
        layoutPersonalise.addComponent(buttonColorScheme);

        return layoutPersonalise;
    }

    private Layout createColorSchemesLayout() {
        final Layout layoutColorSchemes = new Menu("Themes");
        layoutColorSchemes.addComponent(backBtn);

        Preset custom = new Preset(null, Devices.id("custom"));

        ItemList<Preset> list = new ItemList<>(0, 21, layoutColorSchemes.width, layoutColorSchemes.height - 21);
        for (Preset colorScheme : ColorSchemePresetRegistry.getValues()) {
            list.addItem(colorScheme);
        }
        list.addItem(custom);

        list.setItemClickListener((preset, index, button) -> {
            if (preset == custom) preset = null;
            Laptop.getSystem().getSettings().setPreset(preset);
        });

        list.setListItemRenderer(new ListItemRenderer<>(20) {
            @Override
            public void render(GuiGraphics graphics, Preset scheme, Minecraft mc, int x, int y, int width, int height, boolean selected) {
                ResourceLocation key = ColorSchemePresetRegistry.getKey(scheme);
                if (key == null) key = Devices.id("custom");
                graphics.drawString(mc.font, CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, key.getPath()).replaceAll("[A-Z]", " $0").substring(1), x + 5, y + 5, Color.WHITE.getRGB());
            }
        });

        layoutColorSchemes.addComponent(list);

        return layoutColorSchemes;
    }

    /**
     * Create the layout for the color schemes
     *
     * @return the layout.
     */
    private Layout createColorSchemeLayout() {
        final Layout layoutColorScheme = new Menu("UI Colors");
        layoutColorScheme.addComponent(backBtn);

        ComboBox.Custom<Integer> comboBoxTextColor = createColorPicker(145, 26);
        comboBoxTextColor.setValue(Laptop.getSystem().getSettings().getColorScheme().getTextColor());
        layoutColorScheme.addComponent(comboBoxTextColor);

        ComboBox.Custom<Integer> comboBoxTextSecondaryColor = createColorPicker(145, 44);
        comboBoxTextSecondaryColor.setValue(Laptop.getSystem().getSettings().getColorScheme().getTextSecondaryColor());
        layoutColorScheme.addComponent(comboBoxTextSecondaryColor);

        ComboBox.Custom<Integer> comboBoxHeaderColor = createColorPicker(145, 62);
        comboBoxHeaderColor.setValue(Laptop.getSystem().getSettings().getColorScheme().getHeaderColor());
        layoutColorScheme.addComponent(comboBoxHeaderColor);

        ComboBox.Custom<Integer> comboBoxBackgroundColor = createColorPicker(145, 80);
        comboBoxBackgroundColor.setValue(Laptop.getSystem().getSettings().getColorScheme().getBackgroundColor());
        layoutColorScheme.addComponent(comboBoxBackgroundColor);

        ComboBox.Custom<Integer> comboBoxBackgroundSecondaryColor = createColorPicker(145, 98);
        comboBoxBackgroundSecondaryColor.setValue(Laptop.getSystem().getSettings().getColorScheme().getBackgroundSecondaryColor());
        layoutColorScheme.addComponent(comboBoxBackgroundSecondaryColor);

        ComboBox.Custom<Integer> comboBoxItemBackgroundColor = createColorPicker(145, 116);
        comboBoxItemBackgroundColor.setValue(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
        layoutColorScheme.addComponent(comboBoxItemBackgroundColor);

        ComboBox.Custom<Integer> comboBoxItemHighlightColor = createColorPicker(145, 134);
        comboBoxItemHighlightColor.setValue(Laptop.getSystem().getSettings().getColorScheme().getItemHighlightColor());
        layoutColorScheme.addComponent(comboBoxItemHighlightColor);

        buttonColorSchemeApply = new Button(5, 79, Icons.CHECK);
        buttonColorSchemeApply.setEnabled(false);
        buttonColorSchemeApply.setToolTip("Apply", "Set these colors as the new color scheme");
        buttonColorSchemeApply.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if (mouseButton == 0) {
                ColorScheme colorScheme = Laptop.getSystem().getSettings().getColorScheme();
                colorScheme.setTextColor(comboBoxTextColor.getValue());
                colorScheme.setTextSecondaryColor(comboBoxTextSecondaryColor.getValue());
                colorScheme.setHeaderColor(comboBoxHeaderColor.getValue());
                colorScheme.setBackgroundColor(comboBoxBackgroundColor.getValue());
                colorScheme.setBackgroundSecondaryColor(comboBoxBackgroundSecondaryColor.getValue());
                colorScheme.setItemBackgroundColor(comboBoxItemBackgroundColor.getValue());
                colorScheme.setItemHighlightColor(comboBoxItemHighlightColor.getValue());
                buttonColorSchemeApply.setEnabled(false);
            }
        });
        layoutColorScheme.addComponent(buttonColorSchemeApply);

        return layoutColorScheme;
    }

    /**
     * Create the layout for the wallpaper settings
     *
     * @return the layout.
     */
    private Layout addWallpaperLayout() {
        // Create layout.
        Layout wallpaperLayout = new Menu("Wallpaper");

        // Wallpaper image.
        var image = new com.ultreon.devices.api.app.component.Image(6, 29, 6+122, 29+70);
        image.setBorderThickness(1);
        image.setBorderVisible(true);
        image.setImage(Objects.requireNonNull(getLaptop()).getCurrentWallpaper());
        wallpaperLayout.addComponent(image);

        // Previous wallpaper button.
        prevWallpaperBtn = new Button(135, 27, Icons.ARROW_LEFT);
        prevWallpaperBtn.setSize(25, 20);
        prevWallpaperBtn.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton != 0)
                return;

            Laptop laptop = getLaptop();
            if (laptop != null) {
                laptop.prevWallpaper();
                image.setImage(getLaptop().getCurrentWallpaper());
            }
        });
        prevWallpaperBtn.setEnabled(getLaptop().getCurrentWallpaper().isBuiltIn());
        wallpaperLayout.addComponent(prevWallpaperBtn);

        // Next wallpaper button.
        nextWallpaperBtn = new Button(165, 27, Icons.ARROW_RIGHT);
        nextWallpaperBtn.setSize(25, 20);
        nextWallpaperBtn.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton != 0)
                return;

            Laptop laptop = getLaptop();
            if (laptop != null) {
                laptop.nextWallpaper();
                image.setImage(getLaptop().getCurrentWallpaper());
            }
        });
        nextWallpaperBtn.setEnabled(getLaptop().getCurrentWallpaper().isBuiltIn());
        wallpaperLayout.addComponent(nextWallpaperBtn);

        // Reset wallpaper button.
        Button resetWallpaperBtn = new Button(6, 100, "Reset Wallpaper");
        resetWallpaperBtn.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                getLaptop().setWallpaper(0);
                image.setImage(getLaptop().getCurrentWallpaper());
                prevWallpaperBtn.setEnabled(getLaptop().getCurrentWallpaper().isBuiltIn());
                nextWallpaperBtn.setEnabled(getLaptop().getCurrentWallpaper().isBuiltIn());
            }
        });
        resetWallpaperBtn.top = wallpaperLayout.height - resetWallpaperBtn.getHeight() - 5;
        wallpaperLayout.addComponent(resetWallpaperBtn);

        // Add back button.
        wallpaperLayout.addComponent(backBtn);

        // Add wallpaper load from url button.
        urlWallpaperBtn = new Button(135, 52, "Load", Icons.EARTH);
        urlWallpaperBtn.setSize(55, 20);
        urlWallpaperBtn.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton != 0)
                return;

            Dialog.Input dialog = new Dialog.Input("Enter the URL of the image");
            dialog.setResponseHandler((success, string) -> {
                if (getLaptop() != null) {
                    if (!OnlineRequest.isSafeAddress(string)) {
                        openDialog(new Dialog.Message("Unsafe website."));
                        return false;
                    }
                    getLaptop().setWallpaper(string);
                    image.setImage(getLaptop().getCurrentWallpaper());
                    prevWallpaperBtn.setEnabled(getLaptop().getCurrentWallpaper().isBuiltIn());
                    nextWallpaperBtn.setEnabled(getLaptop().getCurrentWallpaper().isBuiltIn());
                }
                return success;
            });
            openDialog(dialog);
        });
        wallpaperLayout.addComponent(urlWallpaperBtn);
        var wallpaperText = new Text("Wallpaper", image.left+3, image.top+3, image.componentWidth-6);
        wallpaperText.setShadow(true);
        wallpaperText.setTextColor(new Color(getLaptop().getSettings().getColorScheme().getTextColor()));
        wallpaperLayout.addComponent(wallpaperText);

        return wallpaperLayout;
    }

    @Override
    public void load(CompoundTag tag) {

    }

    @Override
    public void save(CompoundTag tag) {

    }

    private void showMenu(Layout layout) {
        predecessor.push(getCurrentLayout());
        backBtn.setVisible(true);
        setCurrentLayout(layout);
    }

    @Override
    public void onClose() {
        super.onClose();
        predecessor.clear();
    }

    private void wallpaperClick(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            showMenu(layoutWallpaper);
        }
    }

    private void colorSchemeClick(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            showMenu(layoutColorScheme);
        }
    }

    private void showAllAppsClick(int mouseX, int mouseY, int mouseButton) {
        Settings.setShowAllApps(checkBoxShowApps.isSelected());
        Laptop laptop = getLaptop();
        assert laptop != null;
        laptop.getTaskBar().setupApplications(laptop.getApplications());
    }

    public static class Menu extends Layout {
        private final String title;

        public Menu(String title) {
            super(200, 150);
            this.title = title;
        }

        @Override
        public void render(GuiGraphics graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
            Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getHeaderColor());
            graphics.fill(x, y, x + width, y + 20, color.getRGB());
            graphics.fill(x, y + 20, x + width, y + 21, color.darker().getRGB());
            graphics.drawString(mc.font, title, x + 22, y + 6, Color.WHITE.getRGB());
            super.render(graphics, laptop, mc, x, y, mouseX, mouseY, windowActive, partialTicks);
        }
    }

    public ComboBox.Custom<Integer> createColorPicker(int left, int top) {
        ComboBox.Custom<Integer> colorPicker = new ComboBox.Custom<>(left, top, 50, 100, 100);
        colorPicker.setValue(Color.RED.getRGB());
        colorPicker.setItemRenderer(new ItemRenderer<>() {
            @Override
            public void render(GuiGraphics graphics, Integer integer, Minecraft mc, int x, int y, int width, int height) {
                if (integer != null) {
                    graphics.fill(x, y, x + width, y + height, integer);
                }
            }
        });
        colorPicker.setChangeListener((oldValue, newValue) ->
        {if (buttonColorSchemeApply != null) buttonColorSchemeApply.setEnabled(true);});

        Palette palette = new Palette(5, 5, colorPicker);
        Layout layout = colorPicker.getLayout();
        layout.addComponent(palette);

        return colorPicker;
    }

    public static class SettingsTrayItem extends TrayItem {
        public SettingsTrayItem() {
            super(Icons.WRENCH, Devices.id("settings"));
        }

        @Override
        public void handleClick(int mouseX, int mouseY, int mouseButton) {
            AppInfo info = ApplicationManager.getApplication(Devices.id("settings"));
            if (info != null) {
                Laptop.getSystem().openApplication(info);
            }
        }
    }
}
