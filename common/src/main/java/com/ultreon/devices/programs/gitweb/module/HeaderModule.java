package com.ultreon.devices.programs.gitweb.module;

import com.ultreon.devices.api.app.Component;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.api.app.component.Label;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.programs.gitweb.component.GitWebFrame;

import java.util.Map;

/**
 * @author MrCrayfish
 */
public class HeaderModule extends Module {
    @Override
    public String[] getRequiredData() {
        return new String[]{"text"};
    }

    @Override
    public String[] getOptionalData() {
        return new String[]{"scale", "padding", "align"};
    }

    @Override
    public int calculateHeight(Map<String, String> data, int width) {
        if (data.containsKey("scale")) {
            return (int) (Double.parseDouble(data.get("scale")) * Laptop.getFont().lineHeight + (data.containsKey("padding") ? Integer.parseInt(data.get("padding")) : 5) * 2);
        }
        return Laptop.getFont().lineHeight + (data.containsKey("padding") ? Integer.parseInt(data.get("padding")) : 5) * 2;
    }

    @Override
    public void generate(GitWebFrame frame, Layout layout, int width, Map<String, String> data) {
        int padding = (data.containsKey("padding") ? Integer.parseInt(data.get("padding")) : 5);
        String s = GitWebFrame.parseFormatting(data.get("text"));
        Label label = new Label(s, width / 2, padding);
        label.setAlignment(Component.ALIGN_CENTER);

        double scale = 1;
        if (data.containsKey("scale")) {
            scale = Double.parseDouble(data.get("scale"));
        }
        label.setScale(scale);

        String align = data.getOrDefault("align", "center");
        if ("left".equals(align)) {
            label.left = padding;
            label.setAlignment(Component.ALIGN_LEFT);
        } else if ("right".equals(align)) {
            label.left = width - padding;
            label.setAlignment(Component.ALIGN_RIGHT);
        }

        layout.addComponent(label);
    }
}
