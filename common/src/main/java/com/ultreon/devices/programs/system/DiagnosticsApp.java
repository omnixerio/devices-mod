package com.ultreon.devices.programs.system;

import com.ultreon.devices.api.app.Application;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.api.app.component.Button;
import com.ultreon.devices.api.app.component.Text;
import com.ultreon.devices.api.app.listener.ClickListener;
import com.ultreon.devices.object.AppInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public final class DiagnosticsApp extends SystemApp {
    private String messageText;
    private final AppInfo appInfo;
    private final Application application;

    private ClickListener positiveListener;

    public DiagnosticsApp() {
        this.messageText = "Unknown app crashed";
        this.appInfo = null;
        this.application = null;
    }

    public DiagnosticsApp(AppInfo appInfo) {
        this.appInfo = appInfo;

        String messageText;
        try {
            messageText = "App Crashed:\n" + appInfo.getName();
        } catch (Exception e) {
            messageText = "App Crashed";
        }
        this.messageText = messageText;
        this.application = null;
    }

    public DiagnosticsApp(Application app) {
        AppInfo appInfo;
        try {
            appInfo = app.getInfo();
        } catch (Exception e) {
            appInfo = null;
        }

        this.appInfo = appInfo;
        String messageText;
        try {
            messageText = appInfo == null ? "App Crashed" : "App Crashed:\n" + this.appInfo.getName();
        } catch (Exception e) {
            messageText = "App Crashed";
        }
        this.messageText = messageText;
        this.application = app;
    }

    @Override
    public void init(@Nullable CompoundTag intent) {
        String applicationName = null;
        if (intent != null) {
            applicationName = intent.getString("applicationName");
        }

        this.messageText = applicationName == null ? "App Crashed" : "App Crashed:\n" + applicationName;

        Layout layoutMain = new Layout(150, 40);

        int textHeight = Minecraft.getInstance().font.wordWrapHeight(messageText, getWidth() - 10);
        layoutMain.height += textHeight;

        layoutMain.setBackground((graphics, mc, x, y, width, height, mouseX, mouseY, windowActive) -> graphics.fill(x, y, x + width, y + height, Color.LIGHT_GRAY.getRGB()));

        Text message = new Text(messageText, 5, 5, getWidth() - 10);
        this.addComponent(message);

        Button buttonPositive = new Button(getWidth() - 41, getHeight() - 20, "Close");
        buttonPositive.setSize(36, 16);
        buttonPositive.setClickListener((event) -> {
            if (positiveListener != null) {
                positiveListener.onClick(event);
            }
            getWindow().close();
        });
        this.addComponent(buttonPositive);
    }

    @Override
    public void load(CompoundTag tag) {

    }

    @Override
    public void save(CompoundTag tag) {

    }

    public void setPositiveListener(ClickListener positiveListener) {
        this.positiveListener = positiveListener;
    }

    public AppInfo getAppInfo() {
        return appInfo;
    }

    public Application getApplication() {
        return application;
    }
}
