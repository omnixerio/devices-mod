package com.jab125.testmod;

import dev.ultreon.devices.api.app.Application;
import dev.ultreon.devices.api.app.component.Text;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public class TestApp extends Application {
    @Override
    public void init(@Nullable CompoundTag intent) {
        this.addComponent(new Text("it works!", 0, 0, 1000));
    }

    @Override
    public void load(CompoundTag tag) {

    }

    @Override
    public void save(CompoundTag tag) {

    }
}
