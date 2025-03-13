package dev.ultreon.devices;

import dev.ultreon.devices.api.ApplicationManager;
import dev.ultreon.devices.programs.BoatRacersApp;
import dev.ultreon.devices.programs.NoteStashApp;
import dev.ultreon.devices.programs.PixelPainterApp;
import dev.ultreon.devices.programs.auction.MineBayApp;
import dev.ultreon.devices.programs.email.EmailApp;
import dev.ultreon.devices.programs.gitweb.GitWebApp;
import dev.ultreon.devices.programs.snake.SnakeApp;
import dev.ultreon.devices.programs.system.*;
import dev.ultreon.devices.programs.themes.ThemesApp;
import dev.ultreon.mods.xinexlib.platform.Services;
import net.minecraft.resources.ResourceLocation;

public class BuiltinApps {
    public static void registerBuiltinApps() {
        ApplicationManager.registerApplication(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "diagnostics"), () -> DiagnosticsApp::new, true);
        ApplicationManager.registerApplication(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "settings"), () -> SettingsApp::new, true);
        ApplicationManager.registerApplication(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "file_browser"), () -> FileBrowserApp::new, true);
        ApplicationManager.registerApplication(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "gitweb"), () -> GitWebApp::new, false);
        ApplicationManager.registerApplication(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "note_stash"), () -> NoteStashApp::new, false);
        ApplicationManager.registerApplication(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "pixel_painter"), () -> PixelPainterApp::new, false);
        ApplicationManager.registerApplication(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "ender_mail"), () -> EmailApp::new, false);
        ApplicationManager.registerApplication(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "app_store"), () -> AppStore::new, true);

        if (Services.isDevelopmentEnvironment() || Devices.EARLY_CONFIG.enableBetaApps) {
            ApplicationManager.registerApplication(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "bank"), () -> BankApp::new, false);
            ApplicationManager.registerApplication(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "boat_racers"), () -> BoatRacersApp::new, false);
            ApplicationManager.registerApplication(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "mine_bay"), () -> MineBayApp::new, false);
            ApplicationManager.registerApplication(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "snake"), () -> SnakeApp::new, false);
            ApplicationManager.registerApplication(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "themes"), () -> ThemesApp::new, false);
        }

        ApplicationManager.registerApplication(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "vulnerability"), () -> VulnerabilityApp::new, true);
    }
}
