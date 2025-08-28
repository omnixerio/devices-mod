package com.ultreon.devices;

import com.ultreon.devices.api.ApplicationManager;
import com.ultreon.devices.programs.BoatRacersApp;
import com.ultreon.devices.programs.NoteStashApp;
import com.ultreon.devices.programs.PixelPainterApp;
import com.ultreon.devices.programs.auction.MineBayApp;
import com.ultreon.devices.programs.email.EmailApp;
import com.ultreon.devices.programs.gitweb.GitWebApp;
import com.ultreon.devices.programs.snake.SnakeApp;
import com.ultreon.devices.programs.system.*;
import com.ultreon.devices.programs.themes.ThemesApp;
import dev.architectury.platform.Platform;
import net.minecraft.resources.ResourceLocation;

public class BuiltinApps {
    public static void registerBuiltinApps() {
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "diagnostics"), () -> DiagnosticsApp::new, true);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "settings"), () -> SettingsApp::new, true);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "file_browser"), () -> FileBrowserApp::new, true);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "gitweb"), () -> GitWebApp::new, false);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "note_stash"), () -> NoteStashApp::new, false);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "pixel_painter"), () -> PixelPainterApp::new, false);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "ender_mail"), () -> EmailApp::new, false);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "app_store"), () -> AppStore::new, true);

        if (Platform.isDevelopmentEnvironment() || Devices.EARLY_CONFIG.enableBetaApps) {
            ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "bank"), () -> BankApp::new, false);
            ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "boat_racers"), () -> BoatRacersApp::new, false);
            ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "mine_bay"), () -> MineBayApp::new, false);
            ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "snake"), () -> SnakeApp::new, false);
            ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "themes"), () -> ThemesApp::new, false);
        }

        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "vulnerability"), () -> VulnerabilityApp::new, true);
    }
}
