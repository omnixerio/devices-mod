package dev.ultreon.devices.programs.system;

import dev.ultreon.devices.api.app.Application;
import dev.ultreon.devices.core.ComputerScreen;
import org.jetbrains.annotations.Nullable;

/// Created by Casey on 03-Aug-17.
public abstract class SystemApp extends Application {
    private ComputerScreen computerScreen;

    SystemApp() {
    }

    public void setLaptop(@Nullable ComputerScreen computerScreen) {
        this.computerScreen = computerScreen;
    }

    @Nullable
    public ComputerScreen getLaptop() {
        return computerScreen;
    }
}
