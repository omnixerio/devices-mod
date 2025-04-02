package com.ultreon.devices.core;

import org.graalvm.polyglot.Value;

import java.io.IOException;
import java.util.Map;

public class BiosApi {
    private final Laptop laptop;
    private InventoryApi inventory;
    private UiApi uiApi;

    public BiosApi(Laptop laptop) {
        this.laptop = laptop;

        this.inventory = new InventoryApi(laptop);
        this.uiApi = new UiApi(laptop);
    }

    public boolean isWorldLess() {
        return Laptop.isWorldLess();
    }

    public InventoryApi getInventoryApi() {
        return inventory;
    }

    public UiApi getUiApi() {
        return uiApi;
    }

    public PyProcess spawnProcess(Value modules, String[] command, Map<String, String> env) throws IOException {
        return laptop.spawnProcess(modules, command, env);
    }
}
