package dev.ultreon.devices.programs.system;


import dev.ultreon.devices.Devices;
import dev.ultreon.devices.api.ApplicationManager;
import dev.ultreon.devices.api.app.Icons;
import dev.ultreon.devices.core.ComputerScreen;
import dev.ultreon.devices.core.io.FileSystem;
import dev.ultreon.devices.object.AppInfo;
import dev.ultreon.devices.object.TrayItem;
import dev.ultreon.devices.programs.system.component.FileBrowser;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public class FileBrowserApp extends SystemApp {
    private FileBrowser browser;

    public FileBrowserApp() {
        this.setDefaultWidth(211);
        this.setDefaultHeight(145);
    }

    @Override
    public void init(@Nullable CompoundTag intent) {
        browser = new FileBrowser(0, 0, this, FileBrowser.Mode.FULL);
        browser.openFolder(FileSystem.DIR_HOME);
        this.addComponent(browser);
    }

    @Override
    public void load(CompoundTag tag) {

    }

    @Override
    public void save(CompoundTag tag) {

    }

    public static class FileBrowserTrayItem extends TrayItem {
        public FileBrowserTrayItem() {
            super(Icons.FOLDER, Devices.id("file_browser"));
        }

        @Override
        public void handleClick(int mouseX, int mouseY, int mouseButton) {
            AppInfo info = ApplicationManager.getApplication(Devices.id("file_browser"));
            if (info != null) {
                ComputerScreen.getSystem().launchApp(info);
            }
        }
    }
}
