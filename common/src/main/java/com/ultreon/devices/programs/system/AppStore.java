package com.ultreon.devices.programs.system;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.ultreon.devices.Devices;
import com.ultreon.devices.Reference;
import com.ultreon.devices.api.ApplicationManager;
import com.ultreon.devices.api.app.Component;
import com.ultreon.devices.api.app.Icons;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.api.app.ScrollableLayout;
import com.ultreon.devices.api.app.component.Button;
import com.ultreon.devices.api.app.component.Label;
import com.ultreon.devices.api.app.component.Spinner;
import com.ultreon.devices.api.utils.OnlineRequest;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.object.AppInfo;
import com.ultreon.devices.object.TrayItem;
import com.ultreon.devices.programs.system.component.AppGrid;
import com.ultreon.devices.programs.system.layout.LayoutAppPage;
import com.ultreon.devices.programs.system.layout.LayoutSearchApps;
import com.ultreon.devices.programs.system.object.AppEntry;
import com.ultreon.devices.programs.system.object.RemoteEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppStore extends SystemApp {
    public static final String CERTIFICATES_BASE_URL = "https://raw.githubusercontent.com/Ultreon/device-mod-certificates/master";

    public static final int LAYOUT_WIDTH = 250;
    public static final int LAYOUT_HEIGHT = 150;
    public List<AppEntry> certifiedApps = new ArrayList<>();
    public List<AppEntry> localAppList = new ArrayList<>();
    private Layout layoutMain;
    private AppInfo queuedApp;

    @Override
    public void init(@Nullable CompoundTag intent) {
        layoutMain = new Layout(LAYOUT_WIDTH, LAYOUT_HEIGHT);

        var q = ApplicationManager.getAvailableApplications().size();
        var rows = (int)Math.round(Math.ceil(q/3D));

        ScrollableLayout homePageLayout = new ScrollableLayout(0, 0, LAYOUT_WIDTH, 368-160+80*rows, LAYOUT_HEIGHT);
        homePageLayout.setScrollSpeed(10);
        homePageLayout.setBackground((graphics, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
            Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getBackgroundColor());
            int offset = 60;
            graphics.fill(x, y + offset, x + LAYOUT_WIDTH, y + offset + 1, color.brighter().getRGB());
            graphics.fill(x, y + offset + 1, x + LAYOUT_WIDTH, y + offset + 19, color.getRGB());
            graphics.fill(x, y + offset + 19, x + LAYOUT_WIDTH, y + offset + 20, color.darker().getRGB());

            offset = 172;
            graphics.fill(x, y + offset, x + LAYOUT_WIDTH, y + offset + 1, color.brighter().getRGB());
            graphics.fill(x, y + offset + 1, x + LAYOUT_WIDTH, y + offset + 19, color.getRGB());
            graphics.fill(x, y + offset + 19, x + LAYOUT_WIDTH, y + offset + 20, color.darker().getRGB());
        });

        com.ultreon.devices.api.app.component.Image imageBanner = new com.ultreon.devices.api.app.component.Image(0, 0, LAYOUT_WIDTH, 60);
        imageBanner.setImage(new ResourceLocation(Reference.MOD_ID, "textures/gui/app_market_background.png"));
        imageBanner.setDrawFull(true);
        homePageLayout.addComponent(imageBanner);

        Button btnSearch = new Button(5, 5, Icons.SEARCH);
        btnSearch.setToolTip("Search", "Find a specific application");
        btnSearch.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                this.setCurrentLayout(new LayoutSearchApps(this, getCurrentLayout()));
            }
        });
        homePageLayout.addComponent(btnSearch);

        Button btnManageApps = new Button(23, 5, Icons.HAMMER);
        btnManageApps.setToolTip("Manage Apps", "Manage your installed applications");
        homePageLayout.addComponent(btnManageApps);

        com.ultreon.devices.api.app.component.Image image = new com.ultreon.devices.api.app.component.Image(5, 33, 20, 20, Icons.SHOP);
        homePageLayout.addComponent(image);

        Label labelBanner = new Label("App Market", 32, 35);
        labelBanner.setScale(2);
        homePageLayout.addComponent(labelBanner);

        Label labelCertified = new Label(ChatFormatting.WHITE + ChatFormatting.BOLD.toString() + "Certified Apps", 10, 66);
        homePageLayout.addComponent(labelCertified);

        Label labelCertifiedDesc = new Label(ChatFormatting.GRAY + "Verified by Ultreon Team", LAYOUT_WIDTH - 10, 66);
        labelCertifiedDesc.setAlignment(Component.ALIGN_RIGHT);
        labelCertifiedDesc.setScale(1d);
        labelCertifiedDesc.setShadow(false);
        homePageLayout.addComponent(labelCertifiedDesc);

        Spinner spinner = new Spinner((LAYOUT_WIDTH - 12) / 2, 120);
        homePageLayout.addComponent(spinner);

        OnlineRequest.getInstance().make(CERTIFICATES_BASE_URL + "/certified_apps.json", (success, response) -> {
            certifiedApps.clear();
            spinner.setVisible(false);
            if (success) {
                Minecraft.getInstance().doRunTask(() -> {
                    AppGrid grid = new AppGrid(0, 81, 3, 1, this);
                    certifiedApps.addAll(parseJson(response));
                    shuffleAndShrink(certifiedApps, 3).forEach(grid::addEntry);
                    homePageLayout.addComponent(grid);
                    grid.reloadIcons();
                });
            } else {
                // TODO error handling
            }
        });

        Label labelOther = new Label(ChatFormatting.WHITE + ChatFormatting.BOLD.toString() + "Other Apps", 10, 178);
        homePageLayout.addComponent(labelOther);

        Label labelOtherDesc = new Label(ChatFormatting.GRAY + "Community Created", LAYOUT_WIDTH - 10, 178);
        labelOtherDesc.setAlignment(Component.ALIGN_RIGHT);
        labelOtherDesc.setScale(1d);
        labelOtherDesc.setShadow(false);
        homePageLayout.addComponent(labelOtherDesc);

        AppGrid other = new AppGrid(0, 192, 3, rows, this);
        shuffleAndShrink(ApplicationManager.getAvailableApplications(), q).forEach(a -> localAppList.add(other.addEntry(a)));
        homePageLayout.addComponent(other);

        layoutMain.addComponent(homePageLayout);

        this.setCurrentLayout(layoutMain);
    }

    @Override
    public void onTick() {
        super.onTick();
        if (this.queuedApp != null) {
            for (AppEntry appEntry : localAppList) {
                if (appEntry.id().equals(this.queuedApp.getId().toString())) {
                    this.openApplication(appEntry);
                    this.queuedApp = null;
                    break;
                }
            }
        }
    }

    @Override
    public void load(CompoundTag tag) {

    }

    @Override
    public void save(CompoundTag tag) {

    }

    public void queueOpen(AppInfo info) {
        this.queuedApp = info;
    }

    public List<RemoteEntry> parseJson(String json) {
        List<RemoteEntry> entries = new ArrayList<>();
        JsonArray array = JsonParser.parseString(json).getAsJsonArray();
        Gson gson = new Gson();
        array.forEach(element -> entries.add(gson.fromJson(element, new TypeToken<RemoteEntry>() {
        }.getType())));
        return entries;
    }

    public void openApplication(AppEntry entry) {
        Layout layout = new LayoutAppPage(getLaptop(), entry, this);
        this.setCurrentLayout(layout);
        Button btnPrevious = new Button(2, 2, Icons.ARROW_LEFT);
        btnPrevious.setClickListener((mouseX1, mouseY1, mouseButton1) -> this.setCurrentLayout(layoutMain));
        layout.addComponent(btnPrevious);
    }

    private <T> List<T> shuffleAndShrink(List<T> list, int newSize) {
        Collections.shuffle(list);
        return list.subList(0, Math.min(list.size(), newSize));
    }

    public static class StoreTrayItem extends TrayItem {
        public StoreTrayItem() {
            super(Icons.SHOP, Devices.id("app_store"));
        }

        @Override
        public void handleClick(int mouseX, int mouseY, int mouseButton) {
            AppInfo info = ApplicationManager.getApplication(Devices.id("app_store"));
            if (info != null) {
                Laptop.getSystem().openApplication(info);
            }
        }
    }
}
