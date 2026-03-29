package dev.ultreon.devices.programs.system;


import dev.ultreon.devices.UltreonDevicesCommon;
import dev.ultreon.devices.api.ApplicationManager;
import dev.ultreon.devices.api.app.Icons;
import dev.ultreon.devices.core.Laptop;
import dev.ultreon.devices.core.io.FileSystem;
import dev.ultreon.devices.object.AppInfo;
import dev.ultreon.devices.object.TrayItem;
import dev.ultreon.devices.programs.system.component.FileBrowser;
import net.minecraft.client.input.MouseButtonEvent;
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
            super(Icons.FOLDER, UltreonDevicesCommon.id("file_browser"));
        }

        @Override
        public void handleClick(MouseButtonEvent event) {
            AppInfo info = ApplicationManager.getApplication(UltreonDevicesCommon.id("file_browser"));
            if (info != null) {
                Laptop.getSystem().openApplication(info);
            }
        }
    }
}
