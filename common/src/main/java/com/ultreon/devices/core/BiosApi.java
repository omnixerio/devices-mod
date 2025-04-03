package com.ultreon.devices.core;

import org.graalvm.polyglot.Value;

import java.io.IOException;
import java.util.Map;

@SuppressWarnings("unused")
@Api
public class BiosApi {
    private final Laptop laptop;
    private InventoryApi inventory;
    private UiApi uiApi;

    public BiosApi(Laptop laptop) {
        this.laptop = laptop;

        this.inventory = new InventoryApi(laptop);
        this.uiApi = new UiApi(laptop);
    }

    @Api
    public boolean isWorldLess() {
        return Laptop.isWorldLess();
    }

    @Api
    public InventoryApi getInventoryApi() {
        return inventory;
    }

    @Api
    public UiApi getUiApi() {
        return uiApi;
    }

    @Api
    public ProcessApi spawnProcess(Value modules, String init, String[] command, Map<String, String> env) throws IOException {
        return laptop.spawnProcess(modules, init, command, env).api();
    }

    @Api
    public ProcessApi getProcess(int pid) {
        PyProcess process = laptop.getProcess(pid);
        if (process == null) return null;
        return process.api();
    }

    @Api
    public Gpu getGpu() {
        return laptop.getGpu();
    }
}
