package com.ultreon.devices.programs.gitweb.module;

import com.ultreon.devices.api.app.Component;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.api.app.component.Image;
import com.ultreon.devices.api.app.component.Text;
import com.ultreon.devices.programs.gitweb.component.GitWebFrame;
import com.ultreon.devices.programs.gitweb.layout.ModuleLayout;

import java.util.Map;

/**
 * @author MrCrayfish
 */
public class ParagraphModule extends Module {
    @Override
    public String[] getRequiredData() {
        return new String[]{"text"};
    }

    @Override
    public String[] getOptionalData() {
        return new String[]{"padding", "image"};
    }

    @Override
    public int calculateHeight(Map<String, String> data, int width) {
        int height = make(data, width).getHeight();
        if (data.containsKey("image")) {
            int padding = data.containsKey("padding") ? Integer.parseInt(data.get("padding")) : 5;
            int size = width / 4;
            return Math.max(width / 4 + padding * 2, make(data, width - size - padding).getHeight());
        }
        return height;
    }

    @Override
    public void generate(GitWebFrame frame, Layout layout, int width, Map<String, String> data) {
        if (data.containsKey("image")) {
            int size = width / 4;
            int padding = data.containsKey("padding") ? Integer.parseInt(data.get("padding")) : 5;
            Image image = new Image(width - size - padding, padding, size, size);
            image.setImage(data.get("image"));
            layout.addComponent(image);
            width -= (size + 5);
        }
        layout.addComponent(make(data, width));
    }

    @Override
    public void modify(GitWebFrame frame, ModuleLayout layout, int width, Map<String, String> data) {
        for (Component component : layout.components) {

            if (component instanceof Image image && data.containsKey("image")) {
                int size = width / 4;
                int padding = data.containsKey("padding") ? Integer.parseInt(data.get("padding")) : 5;
                image.left = width - size - padding;
                image.top = padding;
                image.componentWidth = size;
                image.componentHeight = size;
                image.setImage(data.get("image"));
                width -= (size + 5);
            }


            if (component instanceof Text text) {
                text.setText(GitWebFrame.parseFormatting(data.get("text")));
                text.width = width;
                int padding = data.containsKey("padding") ? Integer.parseInt(data.get("padding")) : 5;
                text.setPadding(padding);
            }
        }
    }

    private Text make(Map<String, String> data, int width) {
        String s = GitWebFrame.parseFormatting(data.get("text"));
        Text text = new Text(s, 0, 0, width);
        int padding = data.containsKey("padding") ? Integer.parseInt(data.get("padding")) : 5;
        text.setPadding(padding);
        return text;
    }
}
