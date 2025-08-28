package com.ultreon.devices.programs.gitweb.module;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.api.app.component.Label;
import com.ultreon.devices.api.app.component.Text;
import com.ultreon.devices.programs.gitweb.component.GitWebFrame;
import com.ultreon.devices.programs.gitweb.component.container.ContainerBox;
import com.ultreon.devices.programs.gitweb.component.container.CraftingBox;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

/**
 * @author MrCrayfish
 */
public abstract class ContainerModule extends Module {
    @Override
    public String[] getRequiredData() {
        return new String[0];
    }

    @Override
    public String[] getOptionalData() {
        return new String[]{"title", "desc"};
    }

    @Override
    public final int calculateHeight(Map<String, String> data, int width) {
        int height = getHeight() + 22;
        if (data.containsKey("desc")) {
            String desc = GitWebFrame.parseFormatting(data.get("desc"));
            Text text = new Text(desc, 0, data.containsKey("title") ? 12 : 5, width - CraftingBox.WIDTH - 5);
            text.setPadding(5);
            height += Math.max(0, (text.getHeight() + text.top) - height);
        }
        return height;
    }

    @Override
    public final void generate(GitWebFrame frame, Layout layout, int width, Map<String, String> data) {
        int craftingX = (width - ContainerBox.WIDTH) / 2;
        int craftingY = 5;

        if (data.containsKey("title") || data.containsKey("desc")) {
            if (data.containsKey("title")) {
                String s = GitWebFrame.parseFormatting(data.get("title"));
                Label label = new Label(ChatFormatting.BOLD + s, 5, 5);
                layout.addComponent(label);
            }
            if (data.containsKey("desc")) {
                String s = GitWebFrame.parseFormatting(data.get("desc"));
                Text text = new Text(s, 0, data.containsKey("title") ? 12 : 5, width - ContainerBox.WIDTH - 5);
                text.setPadding(5);
                layout.addComponent(text);
            }
            craftingX = width - ContainerBox.WIDTH - 5;
        }

        ContainerBox box = createContainer(data);
        box.left = craftingX;
        box.top = craftingY;
        layout.addComponent(box);
    }

    public abstract int getHeight();

    public abstract ContainerBox createContainer(Map<String, String> data);

    protected static ItemStack getItem(Map<String, String> data, String key) {
        if (data.containsKey(key)) {
            try {
                return ItemStack.of(TagParser.parseTag(data.get(key)));
            } catch (CommandSyntaxException e) {
                return ItemStack.EMPTY;
            }
        }
        return ItemStack.EMPTY;
    }
}
