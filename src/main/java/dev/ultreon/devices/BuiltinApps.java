package dev.ultreon.devices;

import dev.ultreon.devices.api.ApplicationManager;
import dev.ultreon.devices.programs.NoteStashApp;
import dev.ultreon.devices.programs.PixelPainterApp;
import dev.ultreon.devices.programs.auction.MineBayApp;
import dev.ultreon.devices.programs.email.EmailApp;
import dev.ultreon.devices.programs.gitweb.GitWebApp;
import dev.ultreon.devices.programs.snake.SnakeApp;
import dev.ultreon.devices.programs.system.*;
import dev.ultreon.devices.programs.themes.ThemesApp;
import net.fabricmc.loader.api.FabricLoader;

public class BuiltinApps {
    public static void registerBuiltinApps() {
        ApplicationManager.registerApplication(OmnixerioDevicesCommon.id("diagnostics"), () -> DiagnosticsApp::new, true);
        ApplicationManager.registerApplication(OmnixerioDevicesCommon.id("settings"), () -> SettingsApp::new, true);
        ApplicationManager.registerApplication(OmnixerioDevicesCommon.id("file_browser"), () -> FileBrowserApp::new, true);
        ApplicationManager.registerApplication(OmnixerioDevicesCommon.id("gitweb"), () -> GitWebApp::new, false);
        ApplicationManager.registerApplication(OmnixerioDevicesCommon.id("note_stash"), () -> NoteStashApp::new, false);
        ApplicationManager.registerApplication(OmnixerioDevicesCommon.id("pixel_painter"), () -> PixelPainterApp::new, false);
        ApplicationManager.registerApplication(OmnixerioDevicesCommon.id("ender_mail"), () -> EmailApp::new, false);
        ApplicationManager.registerApplication(OmnixerioDevicesCommon.id("app_store"), () -> AppStore::new, true);

        if (FabricLoader.getInstance().isDevelopmentEnvironment() || OmnixerioDevicesCommon.EARLY_CONFIG.enableBetaApps) {
            ApplicationManager.registerApplication(OmnixerioDevicesCommon.id("bank"), () -> BankApp::new, false);
            ApplicationManager.registerApplication(OmnixerioDevicesCommon.id("mine_bay"), () -> MineBayApp::new, false);
            ApplicationManager.registerApplication(OmnixerioDevicesCommon.id("snake"), () -> SnakeApp::new, false);
            ApplicationManager.registerApplication(OmnixerioDevicesCommon.id("themes"), () -> ThemesApp::new, false);
        }

        ApplicationManager.registerApplication(OmnixerioDevicesCommon.id("vulnerability"), () -> VulnerabilityApp::new, true);
    }
}
