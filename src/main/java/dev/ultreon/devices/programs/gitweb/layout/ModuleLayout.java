package dev.ultreon.devices.programs.gitweb.layout;

import dev.ultreon.devices.api.app.Layout;
import dev.ultreon.devices.programs.gitweb.component.GitWebFrame;
import dev.ultreon.devices.programs.gitweb.module.ModuleEntry;

public class ModuleLayout extends Layout {
    public ModuleEntry entry;
    private final GitWebFrame frame;

    public ModuleLayout(int left, int top, int width, GitWebFrame frame, ModuleEntry entry) {
        super(left, top, width, entry.getModule().calculateHeight(entry.getData(), width));
        this.entry = entry;
        this.frame = frame;
    }

    public void modify() {
        //this.components.clear();
        entry.getModule().modify(frame, this, width, entry.getData());
    }

    @Override
    public void init() {
        super.init();
        entry.getModule().generate(frame, this, width, entry.getData());
    }

    public void _tick() {
        //DebugLog.log("TICKING " + entry.getModule());
        entry.getModule().tick(frame, this, width, entry.getData());
    }
}
