package com.ultreon.devices.programs.gitweb.module;

import java.util.Map;

/**
 * @author MrCrayfish
 */
public class ModuleEntry {
    private final Module module;
    private Map<String, String> data;

    private String id;

    public final void setId(String id) {
        this.id = id;
    }

    public final String getId() {
        return id;
    }

    public ModuleEntry(Module module, Map<String, String> data) {
        this.module = module;
        this.data = data;
        setId(data.getOrDefault("id", null));
    }


    public Module getModule() {
        return module;
    }

    public Map<String, String> getData() {
        return data;
    }
    public void setData(Map<String, String> map) {
        this.data = map;
    }
}
