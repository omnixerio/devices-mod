package com.ultreon.devices.core.laptop.common;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.devices.Devices;
import com.ultreon.devices.core.laptop.client.ClientLaptop;
import com.ultreon.devices.programs.system.object.ColorScheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.awt.*;

public class TaskBar {
    public static final ResourceLocation APP_BAR_GUI = new ResourceLocation("devices:textures/gui/application_bar.png");
    public static final int BAR_HEIGHT = 18;
    private static final int APPS_DISPLAYED = Devices.DEVELOPER_MODE ? 18 : 10;
    private final ClientLaptop laptop;

    private final int offset = 0;
    private final int pingTimer = 0;

    private static final Marker MARKER = MarkerFactory.getMarker("TaskBar");

    public TaskBar(ClientLaptop laptop) {
        this.laptop = laptop;
    }

//    public void.json init() {
//        this.trayItems.forEach(TrayItem::init);
//    }

//    public void.json init(int posX, int posY) {
//        init();
//    }
//
//    public void.json onTick() {
//        trayItems.forEach(TrayItem::tick);
//    }

    public void render(GuiGraphics graphics, ClientLaptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 0.75f);
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, APP_BAR_GUI);

        // r=217,g=230,b=255
        var colorScheme = new ColorScheme();
        colorScheme.resetDefault();
        Color bgColor = new Color(colorScheme.getBackgroundColor());//.brighter().brighter();
        float[] hsb = Color.RGBtoHSB(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), null);
        bgColor = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
        RenderSystem.setShaderColor(bgColor.getRed() / 255f, bgColor.getGreen() / 255f, bgColor.getBlue() / 255f, 1f);

        int trayItemsWidth = /*trayItems.size()*/0 * 14;
        graphics.blit(APP_BAR_GUI, x, y, 1, 18, 0, 0, 1, 18, 256, 256);
        graphics.blit(APP_BAR_GUI, x + 1, y, ClientLaptop.SCREEN_WIDTH - 36 - trayItemsWidth, 18, 1, 0, 1, 18, 256, 256);
        graphics.blit(APP_BAR_GUI, x + ClientLaptop.SCREEN_WIDTH - 35 - trayItemsWidth, y, 35 + trayItemsWidth, 18, 2, 0, 1, 18, 256, 256);

        RenderSystem.disableBlend();

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
//        for (int i = 0; i < APPS_DISPLAYED && i < laptop.installedApps.size(); i++) {
//            AppInfo info = laptop.installedApps.get(i + offset);
//            RenderUtil.drawApplicationIcon(pose, info, x + 2 + i * 16, y + 2);
//            if (laptop.isApplicationRunning(info)) {
//                RenderSystem.setShaderTexture(0, APP_BAR_GUI);
//                laptop.blit(pose, x + 1 + i * 16, y + 1, 35, 0, 16, 16);
//            }
//        }

        assert mc.level == null || mc.player != null;
       // assert mc.level != null; //can no longer assume
        graphics.drawString(mc.font, timeToString(mc.level != null ? mc.level.getDayTime() : 0), x + 334, y + 5, Color.WHITE.getRGB(), true);

        /* Settings App */
        int startX = x + 317;
//        for (int i = 0; i < trayItems.size(); i++) {
//            int posX = startX - (trayItems.size() - 1 - i) * 14;
//            if (isMouseInside(mouseX, mouseY, posX, y + 2, posX + 13, y + 15)) {
//                Gui.fill(pose, posX, y + 2, posX + 14, y + 16, new Color(1f, 1f, 1f, 0.1f).getRGB());
//            }
//            trayItems.get(i).getIcon().draw(pose, mc, posX + 2, y + 4);
//        }

        RenderSystem.setShaderTexture(0, APP_BAR_GUI);

//        /* Other Apps */
//        if (isMouseInside(mouseX, mouseY, x + 1, y + 1, x + 236, y + 16)) {
//            int appIndex = (mouseX - x - 1) / 16;
//            if (appIndex >= 0 && appIndex < offset + APPS_DISPLAYED && appIndex < laptop.installedApps.size()) {
//                laptop.blit(pose, x + appIndex * 16 + 1, y + 1, 35, 0, 16, 16);
//                laptop.renderComponentTooltip(pose, List.of(Component.literal(laptop.installedApps.get(appIndex).getName())), mouseX, mouseY);
//            }
//        }

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
//        RenderHelper.disableStandardItemLighting();
    }

    public void handleClick(ClientLaptop laptop, int x, int y, int mouseX, int mouseY, int mouseButton) {
        if (isMouseInside(mouseX, mouseY, x + 1, y + 1, x + 236, y + 16)) {
            Devices.LOGGER.debug(MARKER, "Clicked on task bar");
//            int appIndex = (mouseX - x - 1) / 16;
//            if (appIndex >= 0 && appIndex <= offset + APPS_DISPLAYED && appIndex < laptop.installedApps.size()) {
//                laptop.openApplication(laptop.installedApps.get(appIndex));
//                return;
//            }
        }

//        int startX = x + 317;
//        for (int i = 0; i < trayItems.size(); i++) {
//            int posX = startX - (trayItems.size() - 1 - i) * 14;
//            if (isMouseInside(mouseX, mouseY, posX, y + 2, posX + 13, y + 15)) {
//                TrayItem trayItem = trayItems.get(i);
//                trayItem.handleClick(mouseX, mouseY, mouseButton);
//                Devices.LOGGER.debug(MARKER, "Clicked on tray item (%d): %s".formatted(i, trayItem.getClass().getSimpleName()));
//                break;
//            }
//        }
    }

    public boolean isMouseInside(int mouseX, int mouseY, int x1, int y1, int x2, int y2) {
        return mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2;
    }

    public String timeToString(long time) {
        int hours = (int) ((Math.floor(time / 1000d) + 6) % 24);
        int minutes = (int) Math.floor((time % 1000) / 1000d * 60);
        return String.format("%02d:%02d", hours, minutes);
    }

    public ClientLaptop getLaptop() {
        return laptop;
    }
}
