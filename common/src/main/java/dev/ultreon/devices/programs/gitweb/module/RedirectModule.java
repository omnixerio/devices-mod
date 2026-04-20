package dev.ultreon.devices.programs.gitweb.module;

import dev.ultreon.devices.api.app.Layout;
import dev.ultreon.devices.programs.gitweb.component.GitWebFrame;
import dev.ultreon.devices.programs.gitweb.layout.ModuleLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RedirectModule extends Module {
    @Override
    public String[] getRequiredData() {
        List<String> requiredData = new ArrayList<>();
        requiredData.add("url");
        return requiredData.toArray(new String[0]);
    }

    @Override
    public String[] getOptionalData() {
        return new String[0];
    }

    @Override
    public int calculateHeight(Map<String, String> data, int width) {
        return 1; // does not matter
    }

    @Override
    public void generate(GitWebFrame frame, Layout layout, int width, Map<String, String> data) {
        if (data.containsKey("url"))
            frame.loadWebsite(data.get("url"));
    }

    @Override
    public void modify(GitWebFrame frame, ModuleLayout layout, int width, Map<String, String> data) {
        // NO OP
    }
}
