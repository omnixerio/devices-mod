package dev.ultreon.devices.core;

import dev.ultreon.devices.OmnixerioDevicesCommon;
import dev.ultreon.devices.api.TrayItemAdder;
import dev.ultreon.devices.api.event.SetupTrayItemsEvent;
import dev.ultreon.devices.api.utils.RenderUtil;
import dev.ultreon.devices.core.network.TrayItemWifi;
import dev.ultreon.devices.object.AppInfo;
import dev.ultreon.devices.object.TrayItem;
import dev.ultreon.devices.programs.system.AppStore;
import dev.ultreon.devices.programs.system.FileBrowserApp;
import dev.ultreon.devices.programs.system.SettingsApp;
import dev.ultreon.devices.util.Vulnerability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TaskBar {
    public static final Identifier APP_BAR_GUI = Identifier.parse("devices:textures/gui/application_bar.png");
    public static final int BAR_HEIGHT = 18;
    private static final int APPS_DISPLAYED = OmnixerioDevicesCommon.DEVELOPER_MODE ? 18 : 10;

    private final CompoundTag tag;

    private final Laptop laptop;

    private final int offset = 0;

    private final List<TrayItem> trayItems = new ArrayList<>();
    private static final Marker MARKER = MarkerFactory.getMarker("TaskBar");

    /**
     * @deprecated use {@link #TaskBar(Laptop, CompoundTag)} instead.
     */
    @Deprecated
    public TaskBar(Laptop laptop) {
        this(laptop, new CompoundTag());
    }

    public TaskBar(Laptop laptop, CompoundTag tag) {
        this.laptop = laptop;
        this.tag = tag;

        var trayItemsTag = tag.getCompoundOrEmpty("TrayItems");

        addTrayItem(new Vulnerability.VulnerabilityTrayItem(), trayItemsTag);
        addTrayItem(new FileBrowserApp.FileBrowserTrayItem(), trayItemsTag);
        addTrayItem(new SettingsApp.SettingsTrayItem(), trayItemsTag);
        addTrayItem(new AppStore.StoreTrayItem(), trayItemsTag);
        addTrayItem(new TrayItemWifi(), trayItemsTag);

        SetupTrayItemsEvent.EVENT.invoker().setupTrayItems(laptop, new TrayItemAdder(trayItems));
    }

    public void addTrayItem(TrayItem trayItem, CompoundTag tag) {
        this.trayItems.add(trayItem);
        String strId = trayItem.getId().toString();
        if (tag.contains(strId)) {
            CompoundTag trayTag = tag.getCompoundOrEmpty(strId);
            trayItem.deserialize(trayTag);
        }
    }

    public void init() {
        this.trayItems.forEach(TrayItem::init);
    }

    public void setupApplications() {
        // WHAT?
    }

    public void init(int posX, int posY) {
        init();
    }

    public void onTick() {
        trayItems.forEach(TrayItem::tick);
    }

    public void render(GuiGraphicsExtractor graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, float partialTicks) {
        Color bgColor = new Color(laptop.getSettings().getColorScheme().getBackgroundColor());//.brighter().brighter();
        float[] hsb = Color.RGBtoHSB(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), null);
        bgColor = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));

        int trayItemsWidth = trayItems.size() * 14;
//        graphics.blit(RenderPipelines.GUI_TEXTURED, APP_BAR_GUI, x, y, 1, 18, 0, 0, 1, 18, 256, 256, bgColor.getRGB());
//        graphics.blit(RenderPipelines.GUI_TEXTURED, APP_BAR_GUI, x + 1, y, Laptop.getScreenWidth() - 36 - trayItemsWidth, 18, 1, 0, 1, 18, 256, 256, bgColor.getRGB());
//        graphics.blit(RenderPipelines.GUI_TEXTURED, APP_BAR_GUI, x + Laptop.getScreenWidth() - 35 - trayItemsWidth, y, 35 + trayItemsWidth, 18, 2, 0, 1, 18, 256, 256, bgColor.getRGB());

        graphics.fill(x, y, x + Laptop.getScreenWidth(), y + BAR_HEIGHT, bgColor.getRGB());

        for (int i = 0; i < APPS_DISPLAYED && i < laptop.installedApps.size(); i++) {
            AppInfo info = laptop.installedApps.get(i + offset);
            RenderUtil.drawApplicationIcon(graphics, info, x + 2 + i * 16, y + 2);
            if (laptop.isApplicationRunning(info)) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, APP_BAR_GUI, x + 1 + i * 16, y + 1, 35, 0, 16, 16, 256, 256);
            }
        }

        assert mc.level == null || mc.player != null;
        graphics.text(mc.font, timeToString(mc.level != null ? mc.level.getOverworldClockTime() : 0), x + Laptop.getScreenWidth() - 31, y + 5, Color.WHITE.getRGB(), true);

        /* Settings App */
        int startX = x + Laptop.getScreenWidth() - 48;
        for (int i = 0; i < trayItems.size(); i++) {
            int posX = startX - (trayItems.size() - 1 - i) * 14;
            if (isMouseInside(mouseX, mouseY, posX, y + 2, posX + 13, y + 15)) {
                graphics.fill(posX, y + 2, posX + 14, y + 16, new Color(1f, 1f, 1f, 0.1f).getRGB());
            }
            trayItems.get(i).getIcon().draw(graphics, mc, posX + 2, y + 4);
        }

        /* Other Apps */
        if (isMouseInside(mouseX, mouseY, x + 1, y + 1, x + 236, y + 16)) {
            int appIndex = (mouseX - x - 1) / 16;
            if (appIndex >= 0 && appIndex < offset + APPS_DISPLAYED && appIndex < laptop.installedApps.size()) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, APP_BAR_GUI, x + appIndex * 16 + 1, y + 1, 35, 0, 16, 16, 256, 256);
                laptop.renderComponentTooltip(graphics, List.of(Component.literal(laptop.installedApps.get(appIndex).getName())), mouseX, mouseY);
            }
        }
    }

    public void handleClick(Laptop laptop, int x, int y, MouseButtonEvent event) {
        if (isMouseInside((int) event.x(), (int) event.y(), x + 1, y + 1, x + 236, y + 16)) {
            OmnixerioDevicesCommon.LOGGER.debug(MARKER, "Clicked on task bar");
            int appIndex = (int) ((event.x() - x - 1) / 16);
            if (appIndex >= 0 && appIndex <= offset + APPS_DISPLAYED && appIndex < laptop.installedApps.size()) {
                laptop.openApplication(laptop.installedApps.get(appIndex));
                return;
            }
        }

        int startX = x + Laptop.getScreenWidth() - 48;
        for (int i = 0; i < trayItems.size(); i++) {
            int posX = startX - (trayItems.size() - 1 - i) * 14;
            if (isMouseInside((int) event.x(), (int) event.y(), posX, y + 2, posX + 13, y + 15)) {
                TrayItem trayItem = trayItems.get(i);
                trayItem.handleClick(event);
                OmnixerioDevicesCommon.LOGGER.debug(MARKER, "Clicked on tray item (%d): %s".formatted(i, trayItem.getClass().getSimpleName()));
                break;
            }
        }
    }

    public boolean isMouseInside(int mouseX, int mouseY, int x1, int y1, int x2, int y2) {
        return mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2;
    }

    public String timeToString(long time) {
        int hours = (int) ((Math.floor(time / 1000d) + 6) % 24);
        int minutes = (int) Math.floor((time % 1000) / 1000d * 60);
        return String.format("%02d:%02d", hours, minutes);
    }

    public Laptop getLaptop() {
        return laptop;
    }

    public CompoundTag getTag() {
        return tag;
    }

    public CompoundTag serialize() {
        var tag = new CompoundTag();
        CompoundTag trayItemsTag = new CompoundTag();
        for (TrayItem trayItem : trayItems) {
            trayItemsTag.put(trayItem.getId().toString(), trayItem.serialize());
        }
        tag.put("TrayItems", trayItemsTag);

        return tag;
    }
}
