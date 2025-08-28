package com.ultreon.devices.programs.gitweb.module;

import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.programs.gitweb.component.GitWebFrame;
import net.minecraft.client.gui.Gui;

import java.util.Map;

/**
 * @author MrCrayfish
 */
public class DividerModule extends Module {
    @Override
    public String[] getRequiredData() {
        return new String[]{"size"};
    }

    @Override
    public String[] getOptionalData() {
        return new String[]{"color"};
    }

    @Override
    public int calculateHeight(Map<String, String> data, int width) {
        return Math.max(0, Integer.parseInt(data.get("size")));
    }

    @Override
    public void generate(GitWebFrame frame, Layout layout, int width, Map<String, String> data) {
        if (data.containsKey("color")) {
            int color = Integer.parseInt(data.get("color"));
            layout.setBackground((graphics, mc, x, y, width1, height, mouseX, mouseY, windowActive) ->
                    graphics.fill(x, y, x + width1, y + height, color));
        }
    }
}
