package com.ultreon.devices.programs.system.component;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.devices.api.ApplicationManager;
import com.ultreon.devices.api.app.Component;
import com.ultreon.devices.api.app.Icons;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.api.app.component.Image;
import com.ultreon.devices.api.app.component.Label;
import com.ultreon.devices.api.utils.RenderUtil;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.object.AppInfo;
import com.ultreon.devices.programs.system.AppStore;
import com.ultreon.devices.programs.system.object.AppEntry;
import com.ultreon.devices.programs.system.object.LocalEntry;
import com.ultreon.devices.programs.system.object.RemoteEntry;
import com.ultreon.devices.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author MrCrayfish
 */
public class AppGrid extends Component {
    private final int padding = 5;
    private final int horizontalItems;
    private final int verticalItems;
    private final List<AppEntry> entries = new ArrayList<>();
    private final AppStore store;

    private final int itemWidth;
    private final int itemHeight;

    private long lastClick = 0;
    private int clickedIndex;

    private Layout container;

    public AppGrid(int left, int top, int horizontalItems, int verticalItems, AppStore store) {
        super(left, top);
        this.horizontalItems = horizontalItems;
        this.verticalItems = verticalItems;
        this.store = store;
        this.itemWidth = (AppStore.LAYOUT_WIDTH - padding * 2 - padding * (horizontalItems - 1)) / horizontalItems;
        this.itemHeight = 80;
    }

    @Override
    protected void init(Layout layout) {
        container = new Layout(0, 0, AppStore.LAYOUT_WIDTH, horizontalItems * itemHeight + (horizontalItems + 1) * padding);
        int size = Math.min(entries.size(), verticalItems * horizontalItems);
        for (int i = 0; i < size; i++) {
            AppEntry entry = entries.get(i);
            int itemX = left + (i % horizontalItems) * (itemWidth + padding) + padding;
            int itemY = top + (i / horizontalItems) * (itemHeight + padding) + padding;
            container.addComponent(generateAppTile(entry, itemX, itemY));
        }
        layout.addComponent(container);
    }

    @Override
    protected void render(GuiGraphics graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        int size = Math.min(entries.size(), verticalItems * horizontalItems);
        for (int i = 0; i < size; i++) {
            int itemX = x + (i % horizontalItems) * (itemWidth + padding) + padding;
            int itemY = y + (i / horizontalItems) * (itemHeight + padding) + padding;
            if (GuiHelper.isMouseWithin(mouseX, mouseY, itemX, itemY, itemWidth, itemHeight)) {
                graphics.fill(itemX, itemY, itemX + itemWidth, itemY + itemHeight, Color.GRAY.getRGB());
                graphics.fill(itemX + 1, itemY + 1, itemX + itemWidth - 1, itemY + itemHeight - 1, Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
            }
        }
    }

    @Override
    protected void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
        int size = Math.min(entries.size(), verticalItems * horizontalItems);
        for (int i = 0; i < size; i++) {
            int itemX = xPosition + (i % horizontalItems) * (itemWidth + padding) + padding;
            int itemY = yPosition + (i / horizontalItems) * (itemHeight + padding) + padding;
            if (GuiHelper.isMouseWithin(mouseX, mouseY, itemX, itemY, itemWidth, itemHeight)) {
                if (System.currentTimeMillis() - this.lastClick <= 200 && clickedIndex == i) {
                    this.lastClick = 0;
                    store.openApplication(entries.get(i));
                } else {
                    this.lastClick = System.currentTimeMillis();
                    this.clickedIndex = i;
                }
            }
        }
    }

    public AppEntry addEntry(AppInfo info) {
        var a = new LocalEntry(info);
        this.entries.add(a);
        return a;
    }

    public AppEntry addEntry(AppEntry entry) {
        var a = adjustEntry(entry);
        this.entries.add(a);
        return a;
    }

    private AppEntry adjustEntry(AppEntry entry) {
        AppInfo info = ApplicationManager.getApplication(ResourceLocation.tryParse(entry.id()));
        if (info != null) {
            return new LocalEntry(info);
        }
        return entry;
    }

    private Layout generateAppTile(AppEntry entry, int left, int top) {
        Layout layout = new Layout(left, top, itemWidth, itemHeight);

        int iconOffset = (itemWidth - 14 * 3) / 2;
        if (entry instanceof LocalEntry localEntry) {
            Image.AppImage appImage = new Image.AppImage(iconOffset, padding, 14*3, 14*3, localEntry.info());
         //   com.ultreon.devices.api.app.component.Image image = new com.ultreon.devices.api.app.component.Image(iconOffset, padding, 14 * 3, 14 * 3, localEntry.info().getIconU(), localEntry.info().getIconV(), 14, 14, 224, 224, Laptop.ICON_TEXTURES);
            layout.addComponent(appImage);
        } else if (entry instanceof RemoteEntry remoteEntry) {
            ResourceLocation resource = new ResourceLocation(remoteEntry.id);
            com.ultreon.devices.api.app.component.Image image = new com.ultreon.devices.api.app.component.Image(iconOffset, padding, 14 * 3, 14 * 3, AppStore.CERTIFICATES_BASE_URL + "/assets/" + resource.getNamespace() + "/" + resource.getPath() + "/icon.png");
            layout.addComponent(image);
        }

        String clippedName = RenderUtil.clipStringToWidth(entry.name(), itemWidth - padding * 2);
        Label labelName = new Label(clippedName, itemWidth / 2, 50);
        labelName.setAlignment(Component.ALIGN_CENTER);
        layout.addComponent(labelName);

        String clippedAuthor = RenderUtil.clipStringToWidth(entry.author(), itemWidth - padding * 2);
        Label labelAuthor = new Label(clippedAuthor, itemWidth / 2, 62);
        labelAuthor.setAlignment(Component.ALIGN_CENTER);
        labelAuthor.setShadow(false);
        layout.addComponent(labelAuthor);

        if (store.certifiedApps.contains(entry)) {
            com.ultreon.devices.api.app.component.Image certifiedIcon = new com.ultreon.devices.api.app.component.Image(15, 38, Icons.VERIFIED);
            layout.addComponent(certifiedIcon);
        }

        if (entry instanceof LocalEntry) {
            AppInfo info = ((LocalEntry) entry).info();
            if (Laptop.getSystem().getInstalledApplications().contains(info)) {
                com.ultreon.devices.api.app.component.Image installedIcon = new com.ultreon.devices.api.app.component.Image(itemWidth - 10 - 15, 38, Icons.CHECK);
                layout.addComponent(installedIcon);
            }
        }
        return layout;
    }

    public void reloadIcons() {
        if (container != null) {
            reloadIcons(container);
        }
    }

    private void reloadIcons(Layout layout) {
        layout.components.forEach(component -> {
            if (component instanceof Layout) {
                reloadIcons((Layout) component);
            } else if (component instanceof com.ultreon.devices.api.app.component.Image) {
                ((com.ultreon.devices.api.app.component.Image) component).reload();
            }
        });
    }
}
