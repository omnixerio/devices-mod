package com.ultreon.devices.programs.gitweb;

import com.ultreon.devices.api.app.System;
import com.ultreon.devices.api.app.*;
import com.ultreon.devices.api.app.component.Button;
import com.ultreon.devices.api.app.component.Spinner;
import com.ultreon.devices.api.app.component.TextField;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.programs.gitweb.component.GitWebFrame;
import com.ultreon.devices.programs.gitweb.layout.TextLayout;
import com.ultreon.devices.programs.system.SettingsApp;
import com.ultreon.devices.programs.system.layout.StandardLayout;
import com.ultreon.devices.util.DataHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.nbt.CompoundTag;

import org.jetbrains.annotations.Nullable;
import java.awt.*;
import java.util.Optional;

/**
 * The Device Mod implementations of an internet browser. Originally created by MinecraftDoodler.
 * Licensed under GPL 3d
 */
public class GitWebApp extends Application implements SystemAccessor, DataHandler {
    private Layout layoutBrowser;
    private Layout layoutPref;

    private Button btnSearch;
    private Button btnHome;
    private Button btnSettings;

    private GitWebFrame webFrame;
    private TextField textFieldAddress;
    private Spinner spinnerLoading;
    private TextLayout scrollable;

    private System system;

    public Optional<System> getSystem() {
        return Optional.ofNullable(system);
    }

    @Override
    public void init(@Nullable CompoundTag intent) {
        layoutBrowser = new StandardLayout(null, 362, 240, this, null);
        layoutBrowser.setBackground((graphics, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
            Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
            graphics.fill(x, y + 21, x + width, y + 164, color.getRGB());
        });

        layoutPref = new SettingsApp.Menu("Preferences");
        setUpPreferences();

        textFieldAddress = new TextField(2, 2, 304);
        textFieldAddress.setPlaceholder("Enter Address");
        textFieldAddress.setKeyListener(c -> {
            if (c == '\r') {
                webFrame.loadWebsite(this.getAddress());
                return false;
            }
            return true;
        });
        layoutBrowser.addComponent(textFieldAddress);

        spinnerLoading = new Spinner(291, 4);
        spinnerLoading.setVisible(false);
        layoutBrowser.addComponent(spinnerLoading);

        btnSearch = new Button(308, 2, 16, 16, Icons.ARROW_RIGHT);
        btnSearch.setToolTip("Refresh", "Loads the entered address.");
        btnSearch.setClickListener((mouseX, mouseY, mouseButton) -> webFrame.loadWebsite(this.getAddress()));
        layoutBrowser.addComponent(btnSearch);

        btnHome = new Button(326, 2, 16, 16, Icons.HOME);
        btnHome.setToolTip("Home", "Loads page set in settings.");
        btnHome.setClickListener((mouseX, mouseY, mouseButton) -> webFrame.loadWebsite("welcome.official"));
        layoutBrowser.addComponent(btnHome);

        btnSettings = new Button(344, 2, 16, 16, Icons.WRENCH);
        btnSettings.setToolTip("Settings", "Change your preferences.");
        btnSettings.setClickListener((mouseX, mouseY, mouseButton) -> this.setCurrentLayout(layoutPref));
        layoutBrowser.addComponent(btnSettings);

        webFrame = new GitWebFrame(this, 0, 21, 362, 143);
        webFrame.loadWebsite("welcome.official");
        webFrame.setLoadingCallback((s, success) -> {
            spinnerLoading.setVisible(true);
            textFieldAddress.setFocused(false);
            textFieldAddress.setEditable(false);
            textFieldAddress.setText(s);
            btnSearch.setEnabled(false);
        });
        webFrame.setLoadedCallback((s, success) -> {
            spinnerLoading.setVisible(false);
            textFieldAddress.setEditable(true);
            btnSearch.setEnabled(true);
        });
        layoutBrowser.addComponent(webFrame);

        this.setCurrentLayout(layoutBrowser);
    }

    private void setUpPreferences() {
        var backBtn = new Button(2, 2, Icons.ARROW_LEFT);
        backBtn.setVisible(true);
        backBtn.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if (mouseButton == 0) {
                this.setCurrentLayout(layoutBrowser);
            }
        });
        layoutPref.addComponent(backBtn);
    }

    @Override
    @Deprecated
    public void handleKeyTyped(char character, int code) {
        super.handleKeyTyped(character, code);
    }

    private String getAddress() {
        return textFieldAddress.getText().replace("\\s+", "");
    }

    @Override
    public void load(CompoundTag tag) {
    }

    @Override
    public void save(CompoundTag tag) {
    }

    @Override
    public void sendSystem(System system) {
        this.system = system;
    }
}
