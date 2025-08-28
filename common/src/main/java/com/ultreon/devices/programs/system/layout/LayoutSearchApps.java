package com.ultreon.devices.programs.system.layout;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.devices.api.ApplicationManager;
import com.ultreon.devices.api.app.Icons;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.api.app.component.Button;
import com.ultreon.devices.api.app.component.ItemList;
import com.ultreon.devices.api.app.component.TextField;
import com.ultreon.devices.api.app.renderer.ListItemRenderer;
import com.ultreon.devices.api.utils.RenderUtil;
import com.ultreon.devices.object.AppInfo;
import com.ultreon.devices.programs.system.AppStore;
import com.ultreon.devices.programs.system.object.LocalEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author MrCrayfish
 */
public class LayoutSearchApps extends StandardLayout {
    private static final Color ITEM_BACKGROUND = Color.decode("0x9E9E9E");
    private static final Color ITEM_SELECTED = Color.decode("0x757575");
    private final AppStore appStore;
    private long lastClick = 0;

    public LayoutSearchApps(AppStore appStore, Layout previous) {
        super("Search", AppStore.LAYOUT_WIDTH, AppStore.LAYOUT_HEIGHT, appStore, previous);
        this.appStore = appStore;
    }

    @Override
    public void init() {
        super.init();

        ItemList<AppInfo> itemListResults = new ItemList<>(5, 48, AppStore.LAYOUT_WIDTH - 10, 5, true);
        itemListResults.setItems(ApplicationManager.getAvailableApplications());
        itemListResults.sortBy(Comparator.comparing(AppInfo::getName));
        itemListResults.setListItemRenderer(new ListItemRenderer<>(18) {
            @Override
            public void render(GuiGraphics graphics, AppInfo info, Minecraft mc, int x, int y, int width, int height, boolean selected) {
                graphics.fill(x, y, x + width, y + height, selected ? ITEM_SELECTED.getRGB() : ITEM_BACKGROUND.getRGB());

                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                RenderUtil.drawApplicationIcon(graphics, info, x + 2, y + 2);
                RenderUtil.drawStringClipped(graphics, info.getName() + ChatFormatting.GRAY + " - " + ChatFormatting.DARK_GRAY + info.getDescription(), x + 20, y + 5, itemListResults.getWidth() - 22, Color.WHITE.getRGB(), false);
            }
        });
        itemListResults.setItemClickListener((info, index, mouseButton) -> {
            if (mouseButton == 0) {
                if (System.currentTimeMillis() - this.lastClick <= 200) {
                    openApplication(info);
                } else {
                    this.lastClick = System.currentTimeMillis();
                }
            }
        });
        this.addComponent(itemListResults);

        TextField textFieldSearch = new TextField(5, 26, AppStore.LAYOUT_WIDTH - 10);
        textFieldSearch.setIcon(Icons.SEARCH);
        textFieldSearch.setPlaceholder("...");
        textFieldSearch.setKeyListener(c -> {
            Predicate<AppInfo> FILTERED = info -> StringUtils.containsIgnoreCase(info.getName(), textFieldSearch.getText()) || StringUtils.containsIgnoreCase(info.getDescription(), textFieldSearch.getText());
            List<AppInfo> filteredItems = ApplicationManager.getAvailableApplications().stream().filter(FILTERED).collect(Collectors.toList());
            itemListResults.setItems(filteredItems);
            return false;
        });
        this.addComponent(textFieldSearch);
    }

    private void openApplication(AppInfo info) {
        Layout layout = new LayoutAppPage(appStore.getLaptop(), new LocalEntry(info), appStore);
        app.setCurrentLayout(layout);
        Button btnPrevious = new Button(2, 2, Icons.ARROW_LEFT);
        btnPrevious.setClickListener((mouseX1, mouseY1, mouseButton1) -> app.setCurrentLayout(this));
        layout.addComponent(btnPrevious);
    }
}
