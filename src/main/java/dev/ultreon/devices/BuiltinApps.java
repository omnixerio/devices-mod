package dev.ultreon.devices;

import dev.ultreon.devices.api.ApplicationManager;
import dev.ultreon.devices.programs.NoteStashApp;
import dev.ultreon.devices.programs.PixelPainterApp;
import dev.ultreon.devices.programs.auction.MineBayApp;
import dev.ultreon.devices.programs.email.EmailApp;
import dev.ultreon.devices.programs.gitweb.GitWebApp;
import dev.ultreon.devices.programs.snake.SnakeApp;
import dev.ultreon.devices.programs.system.*;
import dev.ultreon.devices.programs.system.*;
import dev.ultreon.devices.programs.themes.ThemesApp;
import dev.ultreon.mods.xinexlib.platform.XinexPlatform;

public class BuiltinApps {
    public static void registerBuiltinApps() {
        ApplicationManager.registerApplication(UltreonDevicesCommon.id("diagnostics"), () -> DiagnosticsApp::new, true);
        ApplicationManager.registerApplication(UltreonDevicesCommon.id("settings"), () -> SettingsApp::new, true);
        ApplicationManager.registerApplication(UltreonDevicesCommon.id("file_browser"), () -> FileBrowserApp::new, true);
        ApplicationManager.registerApplication(UltreonDevicesCommon.id("gitweb"), () -> GitWebApp::new, false);
        ApplicationManager.registerApplication(UltreonDevicesCommon.id("note_stash"), () -> NoteStashApp::new, false);
        ApplicationManager.registerApplication(UltreonDevicesCommon.id("pixel_painter"), () -> PixelPainterApp::new, false);
        ApplicationManager.registerApplication(UltreonDevicesCommon.id("ender_mail"), () -> EmailApp::new, false);
        ApplicationManager.registerApplication(UltreonDevicesCommon.id("app_store"), () -> AppStore::new, true);

        if (XinexPlatform.isDevelopmentEnvironment() || UltreonDevicesCommon.EARLY_CONFIG.enableBetaApps) {
            ApplicationManager.registerApplication(UltreonDevicesCommon.id("bank"), () -> BankApp::new, false);
            ApplicationManager.registerApplication(UltreonDevicesCommon.id("mine_bay"), () -> MineBayApp::new, false);
            ApplicationManager.registerApplication(UltreonDevicesCommon.id("snake"), () -> SnakeApp::new, false);
            ApplicationManager.registerApplication(UltreonDevicesCommon.id("themes"), () -> ThemesApp::new, false);
        }

        ApplicationManager.registerApplication(UltreonDevicesCommon.id("vulnerability"), () -> VulnerabilityApp::new, true);
    }
}
