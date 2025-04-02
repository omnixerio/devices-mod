package com.ultreon.devices.programs.terminal;

import com.ultreon.devices.api.app.Application;
import com.ultreon.devices.core.Laptop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public class TerminalApp extends Application {
    private final TerminalLayout terminalLayout = new TerminalLayout();

    @Override
    public void init(@Nullable CompoundTag intent) {
        terminalLayout.setTitle("Terminal");
        setCurrentLayout(terminalLayout);

        terminalLayout.init();

        this.setDefaultWidth(terminalLayout.width);
        this.setDefaultHeight(terminalLayout.height);
    }

    @Override
    public void load(CompoundTag tag) {

    }

    @Override
    public void save(CompoundTag tag) {

    }
}
