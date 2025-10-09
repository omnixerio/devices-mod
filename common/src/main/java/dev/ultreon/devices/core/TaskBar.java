package dev.ultreon.devices.core;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.ultreon.devices.UltreonDevices;
import dev.ultreon.devices.api.TrayItemAdder;
import dev.ultreon.devices.api.app.Application;
import dev.ultreon.devices.api.app.OperatingSystem;
import dev.ultreon.devices.api.event.client.ComputerTrayItemsEvent;
import dev.ultreon.devices.api.utils.RenderUtil;
import dev.ultreon.devices.core.network.TrayItemWifi;
import dev.ultreon.devices.object.AppInfo;
import dev.ultreon.devices.object.TrayItem;
import dev.ultreon.devices.programs.system.AppStore;
import dev.ultreon.devices.programs.system.FileBrowserApp;
import dev.ultreon.devices.programs.system.SettingsApp;
import dev.ultreon.devices.programs.system.SystemApp;
import dev.ultreon.devices.util.Vulnerability;
import dev.ultreon.mods.xinexlib.event.system.EventSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TaskBar {
    public static final ResourceLocation APP_BAR_GUI = ResourceLocation.parse("devices:textures/gui/application_bar.png");
    public static final int BAR_HEIGHT = 18;
    private static final int APPS_DISPLAYED = UltreonDevices.DEVELOPER_MODE ? 18 : 10;

    private final CompoundTag tag;

    private final OperatingSystem system;

    private final int offset = 0;

    private final List<TrayItem> trayItems = new ArrayList<>();
    private static final Marker MARKER = MarkerFactory.getMarker("TaskBar");

    /// @deprecated use [TaskBar#TaskBar(OperatingSystem, CompoundTag)] instead.
    @Deprecated
    public TaskBar(ComputerScreen system) {
        this(system, new CompoundTag());
    }

    /**
     * Constructs a new TaskBar instance and initializes it with default tray items.
     *
     * @param system the operating system instance associated with this taskbar
     * @param tag    the compound tag used for deserializing tray item data
     */
    public TaskBar(OperatingSystem system, CompoundTag tag) {
        this.system = system;
        this.tag = tag;

        var trayItemsTag = tag.getCompound("TrayItems");

        addTrayItem(system, new Vulnerability.VulnerabilityTrayItem(), trayItemsTag);
        addTrayItem(system, new FileBrowserApp.FileBrowserTrayItem(), trayItemsTag);
        addTrayItem(system, new SettingsApp.SettingsTrayItem(), trayItemsTag);
        addTrayItem(system, new AppStore.StoreTrayItem(), trayItemsTag);
        addTrayItem(system, new TrayItemWifi(system), trayItemsTag);

        TrayItemAdder trayItemAdder = new TrayItemAdder(this.trayItems);
        EventSystem.MAIN.publish(new ComputerTrayItemsEvent(system, trayItemAdder));
    }

    public void addTrayItem(OperatingSystem system, TrayItem trayItem, CompoundTag tag) {
        this.trayItems.add(trayItem);
        String strId = trayItem.getId().toString();
        if (tag.contains(strId, Tag.TAG_COMPOUND)) {
            CompoundTag trayTag = tag.getCompound(strId);
            trayItem.deserialize(system, trayTag);
        }
    }

    public void init() {
        this.trayItems.forEach(TrayItem::init);
    }

    public void setupApplications(List<Application> applications) {
        final Predicate<Application> VALID_APPS = app -> {
            if (app instanceof SystemApp) {
                return true;
            }
            if (UltreonDevices.hasAllowedApplications()) {
                if (UltreonDevices.getAllowedApplications().contains(app.getInfo())) {
                    return !UltreonDevices.DEVELOPER_MODE || Settings.isShowAllApps();
                }
                return false;
            } else if (UltreonDevices.DEVELOPER_MODE) {
                return Settings.isShowAllApps();
            }
            return true;
        };
    }

    public void init(int posX, int posY) {
        init();
    }

    public void onTick() {
        trayItems.forEach(TrayItem::tick);
    }

    public void render(GuiGraphics graphics, ComputerScreen computerScreen, Minecraft mc, int x, int y, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 0.75f);
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, APP_BAR_GUI);

        // r=217,g=230,b=255
        Color bgColor = new Color(computerScreen.getSettings().getColorScheme().getBackgroundColor());//.brighter().brighter();
        float[] hsb = Color.RGBtoHSB(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), null);
        bgColor = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
        RenderSystem.setShaderColor(bgColor.getRed() / 255f, bgColor.getGreen() / 255f, bgColor.getBlue() / 255f, 1f);

        int trayItemsWidth = trayItems.size() * 14;
        graphics.blit(APP_BAR_GUI, x, y, 1, 18, 0, 0, 1, 18, 256, 256);
        graphics.blit(APP_BAR_GUI, x + 1, y, ComputerScreen.getScreenWidth() - 36 - trayItemsWidth, 18, 1, 0, 1, 18, 256, 256);
        graphics.blit(APP_BAR_GUI, x + ComputerScreen.getScreenWidth() - 35 - trayItemsWidth, y, 35 + trayItemsWidth, 18, 2, 0, 1, 18, 256, 256);

        RenderSystem.disableBlend();

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        for (int i = 0; i < APPS_DISPLAYED && i < computerScreen.installedApps.size(); i++) {
            AppInfo info = computerScreen.installedApps.get(i + offset);
            RenderUtil.drawApplicationIcon(graphics, info, x + 2 + i * 16, y + 2);
            if (computerScreen.isApplicationRunning(info)) {
                RenderSystem.setShaderTexture(0, APP_BAR_GUI);
                graphics.blit(APP_BAR_GUI, x + 1 + i * 16, y + 1, 35, 0, 16, 16);
            }
        }

        assert mc.level == null || mc.player != null;
       // assert mc.level != null; //can no longer assume
        graphics.drawString(mc.font, timeToString(mc.level != null ? mc.level.getDayTime() : 0), x + ComputerScreen.getScreenWidth() - 31, y + 5, Color.WHITE.getRGB(), true);

        /* Settings App */
        int startX = x + ComputerScreen.getScreenWidth() - 48;
        for (int i = 0; i < trayItems.size(); i++) {
            int posX = startX - (trayItems.size() - 1 - i) * 14;
            if (isMouseInside(mouseX, mouseY, posX, y + 2, posX + 13, y + 15)) {
                graphics.fill(posX, y + 2, posX + 14, y + 16, new Color(1f, 1f, 1f, 0.1f).getRGB());
            }
            trayItems.get(i).getIcon().draw(graphics, mc, posX + 2, y + 4);
        }

        RenderSystem.setShaderTexture(0, APP_BAR_GUI);

        /* Other Apps */
        if (isMouseInside(mouseX, mouseY, x + 1, y + 1, x + 236, y + 16)) {
            int appIndex = (mouseX - x - 1) / 16;
            if (appIndex >= 0 && appIndex < offset + APPS_DISPLAYED && appIndex < computerScreen.installedApps.size()) {
                graphics.blit(APP_BAR_GUI, x + appIndex * 16 + 1, y + 1, 35, 0, 16, 16);
                computerScreen.renderComponentTooltip(graphics, List.of(Component.literal(computerScreen.installedApps.get(appIndex).getName())), mouseX, mouseY);
            }
        }

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    public void handleClick(ComputerScreen computerScreen, int x, int y, int mouseX, int mouseY, int mouseButton) {
        if (isMouseInside(mouseX, mouseY, x + 1, y + 1, x + 236, y + 16)) {
            UltreonDevices.LOGGER.debug(MARKER, "Clicked on task bar");
            int appIndex = (mouseX - x - 1) / 16;
            if (appIndex >= 0 && appIndex <= offset + APPS_DISPLAYED && appIndex < computerScreen.installedApps.size()) {
                computerScreen.launchApp(computerScreen.installedApps.get(appIndex));
                return;
            }
        }

        int startX = x + ComputerScreen.getScreenWidth() - 48;
        for (int i = 0; i < trayItems.size(); i++) {
            int posX = startX - (trayItems.size() - 1 - i) * 14;
            if (isMouseInside(mouseX, mouseY, posX, y + 2, posX + 13, y + 15)) {
                TrayItem trayItem = trayItems.get(i);
                trayItem.handleClick(mouseX, mouseY, mouseButton);
                UltreonDevices.LOGGER.debug(MARKER, "Clicked on tray item (%d): %s".formatted(i, trayItem.getClass().getSimpleName()));
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

    public OperatingSystem getSystem() {
        return system;
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
