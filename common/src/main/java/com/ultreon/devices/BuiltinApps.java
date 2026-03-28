package com.ultreon.devices;

import com.ultreon.devices.api.ApplicationManager;
import com.ultreon.devices.programs.NoteStashApp;
import com.ultreon.devices.programs.PixelPainterApp;
import com.ultreon.devices.programs.auction.MineBayApp;
import com.ultreon.devices.programs.email.EmailApp;
import com.ultreon.devices.programs.gitweb.GitWebApp;
import com.ultreon.devices.programs.snake.SnakeApp;
import com.ultreon.devices.programs.system.*;
import com.ultreon.devices.programs.themes.ThemesApp;
import dev.ultreon.mods.xinexlib.platform.XinexPlatform;

public class BuiltinApps {
    public static void registerBuiltinApps() {
        ApplicationManager.registerApplication(Devices.id("diagnostics"), () -> DiagnosticsApp::new, true);
        ApplicationManager.registerApplication(Devices.id("settings"), () -> SettingsApp::new, true);
        ApplicationManager.registerApplication(Devices.id("file_browser"), () -> FileBrowserApp::new, true);
        ApplicationManager.registerApplication(Devices.id("gitweb"), () -> GitWebApp::new, false);
        ApplicationManager.registerApplication(Devices.id("note_stash"), () -> NoteStashApp::new, false);
        ApplicationManager.registerApplication(Devices.id("pixel_painter"), () -> PixelPainterApp::new, false);
        ApplicationManager.registerApplication(Devices.id("ender_mail"), () -> EmailApp::new, false);
        ApplicationManager.registerApplication(Devices.id("app_store"), () -> AppStore::new, true);

        if (XinexPlatform.isDevelopmentEnvironment() || Devices.EARLY_CONFIG.enableBetaApps) {
            ApplicationManager.registerApplication(Devices.id("bank"), () -> BankApp::new, false);
            ApplicationManager.registerApplication(Devices.id("boat_racers"), () -> BoatRacersApp::new, false);
            ApplicationManager.registerApplication(Devices.id("mine_bay"), () -> MineBayApp::new, false);
            ApplicationManager.registerApplication(Devices.id("snake"), () -> SnakeApp::new, false);
            ApplicationManager.registerApplication(Devices.id("themes"), () -> ThemesApp::new, false);
        }

        ApplicationManager.registerApplication(Devices.id("vulnerability"), () -> VulnerabilityApp::new, true);
    }
}
