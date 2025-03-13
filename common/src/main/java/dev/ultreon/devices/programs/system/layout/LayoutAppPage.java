package dev.ultreon.devices.programs.system.layout;

import com.google.common.collect.Lists;
import dev.ultreon.devices.Devices;
import dev.ultreon.devices.api.app.Dialog;
import dev.ultreon.devices.api.app.Icons;
import dev.ultreon.devices.api.app.Layout;
import dev.ultreon.devices.api.app.ScrollableLayout;
import dev.ultreon.devices.api.app.component.Button;
import dev.ultreon.devices.api.app.component.Image;
import dev.ultreon.devices.api.app.component.Label;
import dev.ultreon.devices.core.ComputerScreen;
import dev.ultreon.devices.core.Permission;
import dev.ultreon.devices.core.PermissionRequest;
import dev.ultreon.devices.core.PermissionResult;
import dev.ultreon.devices.debug.DebugLog;
import dev.ultreon.devices.object.AppInfo;
import dev.ultreon.devices.programs.gitweb.component.GitWebFrame;
import dev.ultreon.devices.programs.system.AppStore;
import dev.ultreon.devices.programs.system.component.SlideShow;
import dev.ultreon.devices.programs.system.object.AppEntry;
import dev.ultreon.devices.programs.system.object.LocalEntry;
import dev.ultreon.devices.programs.system.object.RemoteEntry;
import dev.ultreon.devices.util.GuiHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;
import java.nio.file.AccessDeniedException;

/// @author MrCrayfish
public class LayoutAppPage extends Layout {
    private final ComputerScreen computerScreen;
    private final AppEntry entry;
    private final AppStore store;

    private dev.ultreon.devices.api.app.Component imageIcon;

    private boolean installed;

    public LayoutAppPage(ComputerScreen computerScreen, AppEntry entry, AppStore store) {
        super(250, 150);
        this.computerScreen = computerScreen;
        this.entry = entry;
        this.store = store;
    }

    @Override
    public void init() {
        if (entry instanceof LocalEntry) {
            installed = ComputerScreen.getSystem().getInstalledApplications().contains(((LocalEntry) entry).info());
        }

        this.setBackground((graphics, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
        {
            Color color = new Color(ComputerScreen.getSystem().getSettings().getColorScheme().getBackgroundColor(), true);
            graphics.fill(x, y + 40, x + width, y + 41, color.brighter().getRGB());
            graphics.fill(x, y + 41, x + width, y + 60, color.getRGB());
            graphics.fill(x, y + 60, x + width, y + 61, color.darker().getRGB());
        });

        ResourceLocation resource = ResourceLocation.tryParse(entry.id());
        if (resource == null) {
            store.getWindow().close();
            computerScreen.openDialog(new Dialog.Message("Invalid app id: " + entry.id()));
            return;
        }

        Image imageBanner = new Image(0, 0, 250, 40);
        imageBanner.setDrawFull(true);
        imageBanner.setBorderVisible(true);
        imageBanner.setBorderThickness(0);
        if (entry instanceof LocalEntry) {
            imageBanner.setImage(ResourceLocation.fromNamespaceAndPath(resource.getNamespace(), "textures/app/banner/" + resource.getPath() + ".png"));
        } else if (entry instanceof RemoteEntry) {
            imageBanner.setImage(AppStore.CERTIFICATES_BASE_URL + "/assets/" + resource.getNamespace() + "/" + resource.getPath() + "/banner.png");
        }
        this.addComponent(imageBanner);

        if (entry instanceof LocalEntry(AppInfo info)) {
            imageIcon = new Image.AppImage(5, 26, 28, 28, info);
          //  imageIcon = new dev.ultreon.devices.api.app.component.Image(5, 26, 28, 28, info.getIconU(), info.getIconV(), 14, 14, 224, 224, Laptop.ICON_TEXTURES);
        } else if (entry instanceof RemoteEntry) {
            imageIcon = new dev.ultreon.devices.api.app.component.Image(5, 26, 28, 28, AppStore.CERTIFICATES_BASE_URL + "/assets/" + resource.getNamespace() + "/" + resource.getPath() + "/icon.png");
        }
        this.addComponent(imageIcon);

        if (store.certifiedApps.contains(entry)) {
            int width = ComputerScreen.getFont().width(entry.name()) * 2;
            dev.ultreon.devices.api.app.component.Image certifiedIcon = new dev.ultreon.devices.api.app.component.Image(38 + width + 3, 29, 20, 20, Icons.VERIFIED);
            this.addComponent(certifiedIcon);
        }
        Label labelTitle = new Label(entry.name(), 38, 32);
        labelTitle.setScale(2);
        this.addComponent(labelTitle);

        String version = entry instanceof LocalEntry ? "v" + entry.version() + " - " + entry.author() : entry.author();
        Label labelVersion = new Label(version, 38, 50);
        this.addComponent(labelVersion);

        String description = GitWebFrame.parseFormatting(entry.description());
        ScrollableLayout descriptionLayout = ScrollableLayout.create(130, 67, 115, 78, description);
        this.addComponent(descriptionLayout);

        SlideShow slideShow = new SlideShow(5, 67, 120, 78);
        if (entry instanceof LocalEntry) {
            if (entry.screenshots() != null) {
                for (String image : entry.screenshots()) {
                    if (image == null) {
                        slideShow.addImage(ResourceLocation.fromNamespaceAndPath(Devices.MOD_ID, "invalid.png"));
                        continue;
                    }
                    if (image.startsWith("http://") || image.startsWith("https://")) {
                        slideShow.addImage(image);
                    } else {
                        ResourceLocation resource1 = ResourceLocation.tryParse(image);
                        if (resource1 == null) slideShow.addImage(ResourceLocation.fromNamespaceAndPath(Devices.MOD_ID, "invalid.png"));
                        slideShow.addImage(resource1);
                    }
                }
            }
        } else if (entry instanceof RemoteEntry remoteEntry) {
            String screenshotUrl = AppStore.CERTIFICATES_BASE_URL + "/assets/" + resource.getNamespace() + "/" + resource.getPath() + "/screenshots/screenshot_%d.png";
            for (int i = 0; i < remoteEntry.screenshots; i++) {
                slideShow.addImage(String.format(screenshotUrl, i));
            }
        }
        this.addComponent(slideShow);

        if (entry instanceof LocalEntry) {
            AppInfo info = ((LocalEntry) entry).info();
            Button btnInstall = new Button(20, 2, installed ? "Delete" : "Install", installed ? Icons.CROSS : Icons.PLUS);
            btnInstall.setSize(55, 16);
            btnInstall.setClickListener((mouseX, mouseY, mouseButton) ->
            {
                if (mouseButton == 0) {
                    if (installed) {
                        computerScreen.removeApplication(info, (o, success) ->
                        {
                            btnInstall.setText("Install");
                            btnInstall.setIcon(Icons.PLUS);
                            installed = false;
                        });
                    } else {
                        computerScreen.requestPermission(new PermissionRequest(
                                "Software installation", Permission.SOFTWARE_MANAGEMENT, info),
                                (PermissionResult result) -> {
                                    if (result.granted()) {
                                        try {
                                            computerScreen.installApplication(info, (o, success) ->
                                            {
                                                DebugLog.log("Installation Succeeded: " + success);
                                                btnInstall.setText("Delete");
                                                btnInstall.setIcon(Icons.CROSS);
                                                installed = true;
                                            });
                                        } catch (AccessDeniedException e) {
                                            store.openDialog(new Dialog.Message(e.getMessage()));
                                        }
                                    }
                                }
                        );
                    }
                }
            });
            this.addComponent(btnInstall);

            //TODO implement support button
            if (info.getSupport() != null) {
                Button btnDonate = new Button(234, 44, Icons.COIN);
                btnDonate.setToolTip("Donate", "Opens a link to donate to author of the application");
                btnDonate.setSize(14, 14);
                this.addComponent(btnDonate);
            }
        } else if (entry instanceof RemoteEntry) {
            Button btnDownload = new Button(20, 2, "Download", Icons.IMPORT);
            btnDownload.setSize(66, 16);
            btnDownload.setClickListener((mouseX, mouseY, mouseButton) -> this.openWebLink("https://minecraft.curseforge.com/projects/" + ((RemoteEntry) entry).projectId));
            this.addComponent(btnDownload);
        }
    }

    @Override
    public void renderOverlay(GuiGraphics graphics, ComputerScreen computerScreen, Minecraft mc, int mouseX, int mouseY, boolean windowActive) {
        super.renderOverlay(graphics, computerScreen, mc, mouseX, mouseY, windowActive);
        if (store.certifiedApps.contains(entry)) {
            int width = ComputerScreen.getFont().width(entry.name()) * 2;
            if (GuiHelper.isMouseWithin(mouseX, mouseY, xPosition + 38 + width + 3, yPosition + 29, 20, 20)) {
                computerScreen.renderComponentTooltip(graphics, Lists.newArrayList(Component.literal("Certified App").withStyle(ChatFormatting.GREEN)), mouseX, mouseY);
            }
        }
    }

    private void openWebLink(String url) {
        Util.getPlatform().openUri(url);
//        try {
//            URI uri = new URL(url).toURI();
//            Class<?> class_ = Class.forName("java.awt.Desktop");
//            Object object = class_.getMethod("getDesktop").invoke(null);
//            class_.getMethod("browse", URI.class).invoke(object, uri);
//        } catch (Throwable throwable1) {
//            Throwable throwable = throwable1.getCause();
//            Devices.LOGGER.error("Couldn't open link: {}", throwable == null ? "<UNKNOWN>" : throwable.getMessage());
//        }
    }
}
