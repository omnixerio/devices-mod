package com.ultreon.devices.api.app;

import com.ultreon.devices.api.io.File;
import com.ultreon.devices.core.Settings;
import com.ultreon.devices.object.AppInfo;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.nbt.CompoundTag;

import java.util.Collection;

/**
 * @author MrCrayfish
 */
public interface System {
    /**
     * Open a context on the screen
     */
    void openContext(Layout layout, int x, int y);

    /**
     * Checks if the system has a context open
     *
     * @return has a context open
     */
    boolean hasContext();

    /**
     * Closes the current context on screen
     */
    void closeContext();

    /**
     * Gets the system settings
     *
     * @return the system settings
     */
    Settings getSettings();

    /**
     * Opens the specified application
     *
     * @param info the app info instance of the application to be opened
     */
    Application openApplication(AppInfo info);

    /**
     * Opens the specified application with an intent tag
     *
     * @param info      the app info instance of the application to be opened
     * @param intentTag the tag to pass data to the initialization of an application
     */
    Application openApplication(AppInfo info, CompoundTag intentTag);

    /**
     * Opens the specified application with a file
     *
     * @param info the app info instance of the application to be opened
     * @param file the file for the application to load
     */
    Pair<Application, Boolean> openApplication(AppInfo info, File file);

    /**
     * Closes the specified application
     *
     * @param info the app info instance of application to close
     */
    void closeApplication(AppInfo info);

    /**
     * Gets all the installed applications
     *
     * @return a collection of installed applications
     */
    Collection<AppInfo> getInstalledApplications();
}
