package dev.ultreon.devices.client;

import com.google.common.collect.ImmutableList;
import dev.ultreon.devices.UltreonDevicesCommon;
import dev.ultreon.devices.api.ApplicationManager;
import dev.ultreon.devices.api.app.*;
import dev.ultreon.devices.api.app.Dialog;
import dev.ultreon.devices.api.app.System;
import dev.ultreon.devices.api.app.component.Image;
import dev.ultreon.devices.api.io.Drive;
import dev.ultreon.devices.api.io.File;
import dev.ultreon.devices.api.task.Callback;
import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.api.task.TaskManager;
import dev.ultreon.devices.api.utils.OnlineRequest;
import dev.ultreon.devices.api.video.CustomResolution;
import dev.ultreon.devices.api.video.VideoInfo;
import dev.ultreon.devices.block.entity.ComputerBlockEntity;
import dev.ultreon.devices.core.Settings;
import dev.ultreon.devices.core.TaskBar;
import dev.ultreon.devices.core.Window;
import dev.ultreon.devices.core.task.TaskInstallApp;
import dev.ultreon.devices.object.AppInfo;
import dev.ultreon.devices.programs.system.DiagnosticsApp;
import dev.ultreon.devices.programs.system.DisplayResolution;
import dev.ultreon.devices.programs.system.PredefinedResolution;
import dev.ultreon.devices.programs.system.SystemApp;
import dev.ultreon.devices.programs.system.component.FileBrowser;
import dev.ultreon.devices.programs.system.task.TaskUpdateApplicationData;
import dev.ultreon.devices.programs.system.task.TaskUpdateSystemData;
import dev.ultreon.devices.util.GLHelper;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

//TODO Intro message (created by mrcrayfish, donate here)

/**
 * Laptop GUI class.
 *
 * @author MrCrayfish, Qboi123
 */
public class Laptop extends Screen implements System {
    public static final int ID = 1;
    public static final Identifier ICON_TEXTURES = Identifier.fromNamespaceAndPath(UltreonDevicesCommon.MOD_ID, "textures/atlas/app_icons.png");
    public static final int ICON_SIZE = 14;
    private static final Identifier LAPTOP_FONT = UltreonDevicesCommon.id("laptop");
    private static Font font;
    private static final Identifier LAPTOP_GUI = Identifier.fromNamespaceAndPath(UltreonDevicesCommon.MOD_ID, "textures/gui/laptop.png");
    private static final List<Application> APPLICATIONS = new ArrayList<>();
    private static boolean worldLess;
    private static Laptop instance;
    private Double dragWindowFromX;
    private Double dragWindowFromY;
    private VideoInfo videoInfo;

    public static List<Application> getApplicationsForFabric() {
        return APPLICATIONS;
    }

    public static List<Identifier> getWallpapers() {
        return Collections.unmodifiableList(WALLPAPERS);
    }

    private static final List<Identifier> WALLPAPERS = new ArrayList<>();

    private static final int BORDER = 10;
    private static final List<Runnable> tasks = new CopyOnWriteArrayList<>();
    private static System system;
    private static BlockPos pos;
    private static Drive mainDrive;
    private final Settings settings;
    private final TaskBar bar;
    final CopyOnWriteArrayList<dev.ultreon.devices.core.Window<?>> windows;
    private final CompoundTag appData;
    private final CompoundTag systemData;
    protected List<AppInfo> installedApps = new ArrayList<>();
    private Layout context = null;
    private Wallpaper currentWallpaper;
    private int lastMouseX, lastMouseY;
    private boolean dragging = false;
    private final IntArraySet pressed = new IntArraySet();
    private final Image wallpaper;
    private final Layout wallpaperLayout;
    private BSOD bsod;

    public static Font getFontStatic() {
        if (font == null) {
            font = Minecraft.getInstance().font;
        }
        return font;
    }

    /**
     * Creates a new laptop GUI.
     *
     * @param laptop the block entity of the laptop in-game, if the laptop is not in-game, the level passed to it should be null.
     */
    public Laptop(ComputerBlockEntity laptop) {
        this(laptop, false);
    }

    /**
     * Creates a new laptop GUI.
     *
     * @param laptop the block entity of the laptop in-game, if the laptop is not in-game, the level passed to it should be null.
     */
    public Laptop(ComputerBlockEntity laptop, boolean worldLess) {
        super(Component.literal("Laptop"));

        instance = this;

        // Laptop data.
        this.appData = laptop.getApplicationData();
        this.systemData = laptop.getSystemData();

        CompoundTag videoInfoData = this.systemData.getCompoundOrEmpty("videoInfo");
        this.videoInfo = new VideoInfo(videoInfoData);

        // Windows
        this.windows = new CopyOnWriteArrayList<>() {
            @Override
            public dev.ultreon.devices.core.Window<?> get(int index) {
                try {
                    return super.get(index);
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            public boolean add(dev.ultreon.devices.core.Window<?> window) {
                window.removed = false;
                return super.add(window);
            }
        };

        // Settings etc.
        this.settings = Settings.fromTag(systemData.getCompoundOrEmpty("Settings"));

        // GUI Components
        CompoundTag taskBarTag = systemData.getCompoundOrEmpty("TaskBar");
        systemData.put("TaskBar", taskBarTag);
        this.bar = new TaskBar(this, taskBarTag);

        // Wallpaper stuff
        this.currentWallpaper = systemData.contains("CurrentWallpaper") ? new Wallpaper(systemData.getCompoundOrEmpty("CurrentWallpaper")) : null;
        if (this.currentWallpaper == null) this.currentWallpaper = new Wallpaper(0);
        Laptop.system = this;
        Laptop.pos = laptop.getBlockPos();
        this.wallpaperLayout = new Layout(getScreenWidth(), getScreenHeight());
        this.wallpaper = new Image(0, 0, getScreenWidth(), getScreenHeight());
        if (currentWallpaper.isBuiltIn()) {
            wallpaper.setImage(WALLPAPERS.get(currentWallpaper.location));
        } else {
            wallpaper.setImage(currentWallpaper.url);
        }
        this.wallpaperLayout.addComponent(this.wallpaper);
        this.wallpaperLayout.handleLoad();

        // World-less flag.
        Laptop.worldLess = worldLess;
    }

    public static Laptop getInstance() {
        return instance;
    }

    public static int getScreenWidth() {
        return instance.videoInfo.getResolution().width();
    }

    public static int getScreenHeight() {
        return instance.videoInfo.getResolution().height();
    }

    public static DisplayResolution getResolution() {
        return instance.videoInfo.getResolution();
    }

    public CompoundTag getModSystemTag(Mod mod) {
        return getModSystemTag(mod.getModId());
    }

    public CompoundTag getModSystemTag(String modId) {
        CompoundTag mods = systemData.getCompoundOrEmpty("Mods");
        systemData.put("Mods", mods);
        CompoundTag mod = mods.getCompoundOrEmpty(modId);
        mods.put(modId, mod);
        return mod;
    }

    public static boolean isWorldLess() {
        return worldLess;
    }

    /**
     * Returns the position of the laptop the player is currently using. This method can ONLY be
     * called when the laptop GUI is open, otherwise it will return a null position.
     *
     * @return the position of the laptop currently in use
     */
    @Nullable
    public static BlockPos getPos() {
        return pos;
    }

    /**
     * Add a wallpaper to the list of available wallpapers.
     *
     * @param wallpaper location to the wallpaper texture, if null the wallpaper will not be added.
     */
    public static void addWallpaper(Identifier wallpaper) {
        if (wallpaper != null) {
            WALLPAPERS.add(wallpaper);
        }
    }

    public static System getSystem() {
        return system;
    }

    @Nullable
    public static Drive getMainDrive() {
        return mainDrive;
    }

    public static void setMainDrive(Drive mainDrive) {
        if (Laptop.mainDrive == null) {
            Laptop.mainDrive = mainDrive;
        }
    }

    /**
     * Run a task later in render thread.
     *
     * @param task the task to run.
     */
    public static void runLater(Runnable task) {
        tasks.add(task);
    }

    /**
     * Initialize the Laptop GUI.
     */
    @Override
    public void init() {
        int posX = (width - getDeviceWidth()) / 2;
        int posY = (height - getDeviceHeight()) / 2;
        bar.init(posX + BORDER, posY + getDeviceHeight() - 28);

        installedApps.clear();
        ListTag list = systemData.getListOrEmpty("InstalledApps");
        for (int i = 0; i < list.size(); i++) {
            AppInfo info = ApplicationManager.getApplication(Identifier.tryParse(list.getStringOr(i, "minecraft:default")));
            if (info != null) {
                installedApps.add(info);
            }
        }
        installedApps.sort(AppInfo.SORT_NAME);
        if (Minecraft.getInstance().getConnection() == null) {
            installedApps.addAll(ApplicationManager.getAvailableApplications());
        }
    }

    private static int getDeviceWidth() {
        return getScreenWidth() + 2 * BORDER;
    }

    private static int getDeviceHeight() {
        return getScreenHeight() + 2 * BORDER;
    }

    @Override
    public void removed() {
        /* Close all windows and sendTask application data */
        for (int i = 0; i < windows.size(); i++) {
            dev.ultreon.devices.core.Window<?> window = windows.get(i);
            if (window != null) {
                window.close();
                i--;
            }
        }

        /* Send system data */
        this.updateSystemData();

        Laptop.pos = null;
        Laptop.system = null;
        Laptop.mainDrive = null;
    }

    private void updateSystemData() {
        systemData.put("CurrentWallpaper", currentWallpaper.serialize());
        systemData.put("Settings", settings.toTag());
        systemData.put("TaskBar", bar.serialize());

        ListTag tagListApps = new ListTag();
        installedApps.forEach(info -> tagListApps.add(StringTag.valueOf(info.getFormattedId())));
        systemData.put("InstalledApps", tagListApps);

        TaskManager.sendTask(new TaskUpdateSystemData(pos, systemData));
    }

    /**
     * Handles Minecraft GUI resizing.
     *
     * @param width     the new width
     * @param height    the new height
     */
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        if (videoInfo.getResolution().width() > width || videoInfo.getResolution().height() > height) {
            videoInfo.setResolution(new CustomResolution(width, height));
        }

        revalidateDisplay();
    }

    public void revalidateDisplay() {
        wallpaper.componentWidth = videoInfo.getResolution().width();
        wallpaper.componentHeight = videoInfo.getResolution().height();
        wallpaperLayout.width = videoInfo.getResolution().width();
        wallpaperLayout.height = videoInfo.getResolution().height();
        wallpaperLayout.updateComponents(0, 0);

        for (var window : windows) {
            if (window != null) {
                window.content.markForLayoutUpdate();
            }
        }
    }

    /**
     * Ticking the laptop.
     */
    @Override
    public void tick() {
        try {
            bar.onTick();

            for (dev.ultreon.devices.core.Window<?> window : windows) {
                if (window != null) {
                    window.onTick();
//                    if (window.removed) {
//                        java.lang.DebugLog.log("REMOVED " + window);
//                        windows.remove(window);
//                        i--;
//                    }
                }
            }

            FileBrowser.refreshList = false;
        } catch (Exception e) {
            bsod(e);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        if (bsod != null) {
            renderBsod(graphics, mouseX, mouseY, partialTicks);
            return;
        }

        Matrix3x2f last = new Matrix3x2f(graphics.pose());

        try {
            renderLaptop(graphics, mouseX, mouseY, partialTicks);
        } catch (NullPointerException e) {
            while (!graphics.pose().equals(last)) {
                graphics.pose();
            }
            graphics.disableScissor();
            bsod(e);// null
        } catch (Exception e) {
            while (!graphics.pose().equals(last)) {
                graphics.pose().popMatrix();
            }
            graphics.disableScissor();
            bsod(e);
        }
    }

    public void renderBsod(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, float partialTicks) {
        renderBezels(graphics, mouseX, mouseY, partialTicks);
        int posX = (width - getDeviceWidth()) / 2;
        int posY = (height - getDeviceHeight()) / 2;
        graphics.fill(posX+10, posY+10, posX + getDeviceWidth()-10, posY + getDeviceHeight()-10, new Color(0, 0, 255).getRGB());
        var bo = new ByteArrayOutputStream();

        double scale = Minecraft.getInstance().getWindow().getGuiScale();

        var b = new PrintStream(bo);
        bsod.throwable.printStackTrace(b);
        var str = bo.toString();
        drawLines(graphics, Laptop.getFontStatic(), str, posX+10, posY+10+ getFontStatic().lineHeight*2, (int) ((getDeviceWidth() - 10) * scale), new Color(255, 255, 255).getRGB());
        graphics.pose().popMatrix();
        graphics.pose().scale(2, 2);
        graphics.pose().translate((posX+10)/2f,(posY+10)/2f);
        graphics.text(getFontStatic(), "System has crashed!", 0, 0, new Color(255, 255, 255).getRGB());
        graphics.pose().popMatrix();
    }

    public static void drawLines(GuiGraphicsExtractor graphics, Font font, String text, int x, int y, int width, int color) {
        var lines = new ArrayList<String>();
        font.getSplitter().splitLines(FormattedText.of(text.replaceAll("\r\n", "\n").replaceAll("\r", "\n")), width, Style.EMPTY).forEach(b -> lines.add(b.getString()));
        var totalTextHeight = font.lineHeight*lines.size();
        var textScale = (instance.videoInfo.getResolution().height()-20- getFontStatic().lineHeight*2)/(float)totalTextHeight;
        textScale = (float) (1f / Minecraft.getInstance().getWindow().getGuiScale());
        textScale = Math.max(0.5f, textScale);
        graphics.pose().pushMatrix();
        graphics.pose().scale(textScale, textScale);
        graphics.pose().translate(x / textScale, (y+3)/textScale);
        //poseStack.translate();
        var lineNr = 0;
        for (String s : lines) {
            graphics.text(font, s.replaceAll("\t", "    "), 0, lineNr * font.lineHeight, color);
            lineNr++;
        }
        graphics.pose().popMatrix();
    }

    public void renderBezels(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, float partialTicks) {
        tasks.clear();

        this.extractBackground(graphics, mouseX, mouseY, partialTicks);
        
        //*************************//
        //     Physical Screen     //
        //*************************//
        int deviceWidth = videoInfo.getResolution().width() + BORDER * 2;
        int deviceHeight = videoInfo.getResolution().height() + BORDER * 2;
        int posX = (width - deviceWidth) / 2;
        int posY = (height - deviceHeight) / 2;

        // Corners
        graphics.blit(RenderPipelines.GUI_TEXTURED, LAPTOP_GUI, posX, posY, 0, 0, BORDER, BORDER, 256, 256); // TOP-LEFT
        graphics.blit(RenderPipelines.GUI_TEXTURED, LAPTOP_GUI, posX + deviceWidth - BORDER, posY, 11, 0, BORDER, BORDER, 256, 256); // TOP-RIGHT
        graphics.blit(RenderPipelines.GUI_TEXTURED, LAPTOP_GUI, posX + deviceWidth - BORDER, posY + deviceHeight - BORDER, 11, 11, BORDER, BORDER, 256, 256); // BOTTOM-RIGHT
        graphics.blit(RenderPipelines.GUI_TEXTURED, LAPTOP_GUI, posX, posY + deviceHeight - BORDER, 0, 11, BORDER, BORDER, 256, 256); // BOTTOM-LEFT

        // Edges
        graphics.blit(RenderPipelines.GUI_TEXTURED, LAPTOP_GUI, posX + BORDER, posY, getScreenWidth(), BORDER, 10, 0, 1, BORDER, 256, 256); // TOP
        graphics.blit(RenderPipelines.GUI_TEXTURED, LAPTOP_GUI, posX + deviceWidth - BORDER, posY + BORDER, BORDER, getScreenHeight(), 11, 10, BORDER, 1, 256, 256); // RIGHT
        graphics.blit(RenderPipelines.GUI_TEXTURED, LAPTOP_GUI, posX + BORDER, posY + deviceHeight - BORDER, getScreenWidth(), BORDER, 10, 11, 1, BORDER, 256, 256); // BOTTOM
        graphics.blit(RenderPipelines.GUI_TEXTURED, LAPTOP_GUI, posX, posY + BORDER, BORDER, getScreenHeight(), 0, 11, BORDER, 1, 256, 256); // LEFT

        // Center
        graphics.blit(RenderPipelines.GUI_TEXTURED, LAPTOP_GUI, posX + BORDER, posY + BORDER, getScreenWidth(), getScreenHeight(), 10, 10, 1, 1, 256, 256);

    }

    /**
     * Render the laptop screen.
     *
     * @param graphics     gui graphics helper
     * @param mouseX       the current mouse X position.
     * @param mouseY       the current mouse Y position.
     * @param partialTicks the rendering partial ticks that forge give use (which is useless here).
     */
    public void renderLaptop(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, float partialTicks) {
        int posX = (width - getDeviceWidth()) / 2;
        int posY = (height - getDeviceHeight()) / 2;
        // Fixes the strange partialTicks that Forge decided to give us
        for (Runnable task : tasks) {
            task.run();
        }
        
        renderBezels(graphics, mouseX, mouseY, partialTicks);

        GLHelper.pushScissor(posX, posY, videoInfo.getResolution().width() + BORDER, videoInfo.getResolution().height() + BORDER);
        //*******************//
        //     Wallpaper     //
        //*******************//
        //RenderSystem.setShaderTexture(0, WALLPAPERS.get(currentWallpaper));
        //RenderUtil.drawRectWithTexture(pose, posX + 10, posY + 10, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, 512, 288);
        Image.CACHE.forEach((s, cachedImage) -> cachedImage.delete());
        this.wallpaperLayout.render(graphics, this, this.minecraft, posX+10, posY+10, mouseX, mouseY, true, partialTicks);
        boolean insideContext = false;
        if (context != null) {
            insideContext = isMouseInside(mouseX, mouseY, context.xPosition, context.yPosition, context.xPosition + context.width, context.yPosition + context.height);
        }

        //****************//
        //     Window     //
        //****************//
        graphics.pose().pushMatrix();
        {
         //   Window<?>[] windows1 = Arrays.stream(windows.toArray()).filter(Objects::nonNull).toArray(Window<?>[]::new);
            for (int i = windows.size() - 1; i >= 0; i--) {
                var window = windows.get(i);
                if (window != null) {
                    Matrix3x2f last = new Matrix3x2f(graphics.pose());
                    try {
                        if (i == 0) {
                            window.render(graphics, this, minecraft, posX + BORDER, posY + BORDER, mouseX, mouseY, !insideContext, partialTicks);
                        } else {
                            window.render(graphics, this, minecraft, posX + BORDER, posY + BORDER, Integer.MAX_VALUE, Integer.MAX_VALUE, false, partialTicks);
                        }
                    } catch (Exception e) {
                        while (!graphics.pose().equals(last)) {
                            graphics.pose().popMatrix();
                        }
                        graphics.disableScissor();
                        e.printStackTrace();
                        Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
                        message.setTitle("Error");
                        CompoundTag intent = new CompoundTag();
                        if (window.content instanceof Application app) {
                            AppInfo info = app.getInfo();
                            if (info != null) {
                                intent.putString("name", info.getName());
                            }
                            openApplication(ApplicationManager.getApplication(UltreonDevicesCommon.id("diagnostics")), intent);
                            closeApplication(app);
                        }
                    }
                    graphics.pose().translate(0, 0);
                }
            }
        }
        bar.render(graphics, this, minecraft, posX + 10, posY + getDeviceHeight() - 28, mouseX, mouseY, partialTicks);

        if (context != null) {
            context.render(graphics, this, minecraft, context.xPosition, context.yPosition, mouseX, mouseY, true, partialTicks);
        }

        graphics.pose().popMatrix();

        //****************************//
        // Render the Application Bar //
        //****************************//
        Image.CACHE.entrySet().removeIf(entry -> {
            Image.CachedImage cachedImage = entry.getValue();
            if (cachedImage.isDynamic() && cachedImage.isPendingDeletion()) {
                @Nullable AbstractTexture texture = cachedImage.getTexture();
                texture.close();
                return true;
            }
            return false;
        });

        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        GLHelper.popScissor();

        GLHelper.clearScissorStack();
    }

    private boolean isMouseInside(int mouseX, int mouseY, int startX, int startY, int endX, int endY) {
        return mouseX >= startX && mouseX <= endX && mouseY >= startY && mouseY <= endY;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        try {
            return mouseClickedInternal(event, doubleClick);
        } catch (NullPointerException e) {
            bsod(e);// null
        } catch (Exception e) {
            bsod(e);
        }
        return super.mouseClicked(event, doubleClick);
    }
    private void bsod(Throwable e) {
        this.bsod = new BSOD(e);
        e.printStackTrace();
    }

    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public void setDisplayResolution(PredefinedResolution newValue) {
        if (this.videoInfo != null) {
            this.videoInfo.setResolution(newValue);
        }
    }

    public void revalidate() {

    }

    private static final class BSOD {
        private final Throwable throwable;
        public BSOD(Throwable e) {
            this.throwable = e;
        }
    }
    @SuppressWarnings("unchecked")
    public boolean mouseClickedInternal(MouseButtonEvent event, boolean doubleClick) {
        this.lastMouseX = (int) event.x();
        this.lastMouseY = (int) event.y();

        int posX = (width - getScreenWidth()) / 2;
        int posY = (height - getScreenHeight()) / 2;

        if (this.context != null) {
            int dropdownX = context.xPosition;
            int dropdownY = context.yPosition;
            if (isMouseInside((int) event.x(), (int) event.y(), dropdownX, dropdownY, dropdownX + context.width, dropdownY + context.height)) {
                this.context.handleMouseClick(event);
                return false;
            } else {
                this.context = null;
            }
        }

        this.bar.handleClick(this, posX, posY + getScreenHeight() - TaskBar.BAR_HEIGHT, event);

        for (int i = 0; i < windows.size(); i++) {
            dev.ultreon.devices.core.Window<Application> window = (dev.ultreon.devices.core.Window<Application>) windows.get(i);
            if (window != null) {
                try {
                    dev.ultreon.devices.core.Window<Dialog> dialogWindow = window.getContent().getActiveDialog();
                    if (isMouseWithinWindow((int) event.x(), (int) event.y(), window) || isMouseWithinWindow((int) event.x(), (int) event.y(), dialogWindow)) {
                        windows.remove(i);
                        i--;
                        updateWindowStack();
                        windows.addFirst(window);

                        windows.getFirst().handleMouseClick(this, posX, posY, event);

                        if (isMouseWithinWindowBar((int) event.x(), (int) event.y(), dialogWindow)) {
                            dragWindowFromX = event.x() - dialogWindow.offsetX;
                            dragWindowFromY = event.y() - dialogWindow.offsetY;
                            this.dragging = true;
                            return false;
                        }

                        if (isMouseWithinWindowBar((int) event.x(), (int) event.y(), window) && dialogWindow == null) {
                            dragWindowFromX = event.x() - window.offsetX;
                            dragWindowFromY = event.y() - window.offsetY;
                            this.dragging = true;
                            return false;
                        }
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
                    message.setTitle("Error");
                    if (windows.size() == 0 || windows.getFirst() == null) {
                        CompoundTag intent = new CompoundTag();
                        AppInfo info = window.content.getInfo();
                        if (info != null) {
                            intent.putString("name", info.getName());
                        }
                        openApplication(ApplicationManager.getApplication(UltreonDevicesCommon.id("diagnostics")), intent);
                    } else {
                        windows.getFirst().openDialog(message);
                    }
                }
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        super.mouseReleased(event);
        this.dragging = false;
        dragWindowFromX = null;
        dragWindowFromY = null;
        try {
            if (this.context != null) {
                int dropdownX = context.xPosition;
                int dropdownY = context.yPosition;
                if (isMouseInside((int) event.x(), (int) event.y(), dropdownX, dropdownY, dropdownX + context.width, dropdownY + context.height)) {
                    this.context.handleMouseRelease(event);
                }
            } else if (windows.getFirst() != null) {
                windows.getFirst().handleMouseRelease(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
            message.setTitle("Error");
            windows.getFirst().openDialog(message);
        }
        return true;
    }

    @Override
    public void afterKeyboardAction() {
//        if (Keyboard.getEventKeyState()) {
//            char pressed = Keyboard.getEventCharacter();
//            int code = Keyboard.getEventKey();
//
//            if (windows[0] != null) {
//                windows[0].handleKeyTyped(pressed, code);
//            }
//
////            super.charTyped(pressed, code);
//        } else {
//        }

        // Todo - handle key presses
//        this.minecraft.dispatchKeypresses();
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        boolean override = super.charTyped(event);
        try {
            if (!override && windows.getFirst() != null)
                windows.getFirst().handleCharTyped(event);
        } catch (Exception e) {
            e.printStackTrace();
            Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
            message.setTitle("Error");
            windows.getFirst().openDialog(message);
        }
        return override;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        final boolean override = super.keyPressed(event);

        try {
            if (!pressed.contains(event.key()) && !override && windows.getFirst() != null) {
                windows.getFirst().handleKeyPressed(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
            message.setTitle("Error");
            windows.getFirst().openDialog(message);
        }
        pressed.add(event.key());
        return super.keyPressed(event);
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        pressed.remove(event.key());

        boolean b = super.keyReleased(event);

        try {
            if (event.key() >= 32 && event.key() < 256 && windows.getFirst() != null) {
                windows.getFirst().handleKeyReleased(event);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
            message.setTitle("Error");
            windows.getFirst().openDialog(message);
        }
        return b;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        int posX = (width - getScreenWidth()) / 2;
        int posY = (height - getScreenHeight()) / 2;

        try {
            if (this.context != null) {
                int dropdownX = context.xPosition;
                int dropdownY = context.yPosition;
                if (isMouseInside((int) event.x(), (int) event.y(), dropdownX, dropdownY, dropdownX + context.width, dropdownY + context.height)) {
                    this.context.handleMouseDrag(event);
                }
                return true;
            }

            if (windows.getFirst() != null) {
                dev.ultreon.devices.core.Window<Application> window = (dev.ultreon.devices.core.Window<Application>) windows.getFirst();
                dev.ultreon.devices.core.Window<Dialog> dialogWindow = window.getContent().getActiveDialog();
                if (dragging) {
                    if (isMouseOnScreen((int) event.x(), (int) event.y()) && dragWindowFromX != null && dragWindowFromY != null) {
                        Objects.requireNonNullElse(dialogWindow, window).handleWindowMove(posX, posY, (int) (dx + event.x() - dragWindowFromX), (int) (dy + event.y() - dragWindowFromY));
                    } else {
                        dragging = false;
                    }
                } else {
                    if (isMouseWithinWindow((int) event.x(), (int) event.y(), window) || isMouseWithinWindow((int) event.x(), (int) event.y(), dialogWindow)) {
                        window.handleMouseDrag(event);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
            message.setTitle("Error");
            windows.getFirst().openDialog(message);
        }
        this.lastMouseX = (int) event.x();
        this.lastMouseY = (int) event.y();
        return true;
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {

    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (scrollY != 0) {
            try {
                if (windows.getFirst() != null) {
                    windows.getFirst().handleMouseScroll((int) x, (int) y, scrollY, scrollY >= 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
                message.setTitle("Error");
                windows.getFirst().openDialog(message);
            }
        }
        return true;
    }

    public void renderComponentTooltip(@NotNull GuiGraphicsExtractor graphics, @NotNull List<Component> tooltips, int x, int y) {
        List<ClientTooltipComponent> tooltipComponents = tooltips.stream().map(component -> ClientTooltipComponent.create(component.getVisualOrderText())).toList();
        graphics.tooltip(minecraft.font, tooltipComponents, x, y, DefaultTooltipPositioner.INSTANCE, null);
    }

    @SuppressWarnings("ReassignedVariable")
    public Pair<Application, Boolean> sendApplicationToFront(AppInfo info) {
        int i = 0;
        for (; i < windows.size(); i++) {
            dev.ultreon.devices.core.Window<?> window = windows.get(i);
            if (window != null && window.content instanceof Application && ((Application) window.content).getInfo() == info) {
                windows.remove(i);
                updateWindowStack();
                windows.addFirst(window);
                i--;
                return Pair.of((Application) window.content, true);
            }
        }
        return Pair.of(null, false);
    }

    @Override
    public Application openApplication(AppInfo info) {
        return openApplication(info, (CompoundTag) null);
    }

    @Override
    public Application openApplication(AppInfo info, CompoundTag intentTag) {
        Optional<Application> optional = APPLICATIONS.stream().filter(app -> app.getInfo() == info).findFirst();
        Application[] a = new Application[]{null};
        optional.ifPresent(application -> a[0] = openApplication(application, intentTag));
        return a[0];
    }

    private Application openApplication(Application app, CompoundTag intent) {
        if (!(app instanceof DiagnosticsApp)) {
            if (isApplicationNotInstalled(app.getInfo()))
                return null;

            if (isInvalidApplication(app.getInfo()))
                return null;
        }

        try {
            var q = sendApplicationToFront(app.getInfo());
            if (q.right())
                return q.left();

            if (app instanceof SystemApp) {
                ((SystemApp) app).setLaptop(this);
            }

            if (app instanceof SystemAccessor) {
                ((SystemAccessor) app).sendSystem(this);
            }

            dev.ultreon.devices.core.Window<Application> window = new dev.ultreon.devices.core.Window<>(app, this);
            window.init((width - getScreenWidth()) / 2, (height - getScreenHeight()) / 2, intent);

            if (appData.contains(app.getInfo().getFormattedId())) {
                app.load(appData.getCompoundOrEmpty(app.getInfo().getFormattedId()));
            }

            if (app.getCurrentLayout() == null) {
                app.restoreDefaultLayout();
            }

            addWindow(window);

            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
        } catch (Exception e) {
            e.printStackTrace();
            AppInfo info = ApplicationManager.getApplication(UltreonDevicesCommon.id("diagnostics"));
            system.openApplication(info);
        }
        return app;
    }

    @Override
    public Pair<Application, Boolean> openApplication(AppInfo info, File file) {
        if (isApplicationNotInstalled(info))
            return Pair.of(null, false);

        if (isInvalidApplication(info))
            return Pair.of(null, false);

        try {
            Optional<Application> optional = APPLICATIONS.stream().filter(app -> app.getInfo() == info).findFirst();
            if (optional.isPresent()) {
                Application application = optional.get();
                boolean alreadyRunning = isApplicationRunning(info);
                openApplication(application, null);
                if (isApplicationRunning(info)) {
                    if (!application.handleFile(file)) {
                        if (!alreadyRunning) {
                            closeApplication(application);
                        }
                        return Pair.of(application, false);
                    }
                    return Pair.of(application, true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppInfo info1 = ApplicationManager.getApplication(UltreonDevicesCommon.id("diagnostics"));
            system.openApplication(info1);
        }
        return Pair.of(null, true);
    }

    @Override
    public void closeApplication(AppInfo info) {
        Optional<Application> optional = APPLICATIONS.stream().filter(app -> app.getInfo() == info).findFirst();
        optional.ifPresent(this::closeApplication);
    }

    @SuppressWarnings("unchecked")
    private void closeApplication(Application app) {
        for (int i = 0; i < windows.size(); i++) {
            dev.ultreon.devices.core.Window<Application> window = (dev.ultreon.devices.core.Window<Application>) windows.get(i);
            if (window != null) {
                if (window.content.getInfo().equals(app.getInfo())) {
                    if (app.isDirty()) {
                        CompoundTag container = new CompoundTag();
                        app.save(container);
                        app.clean();
                        appData.put(app.getInfo().getFormattedId(), container);
                        TaskManager.sendTask(new TaskUpdateApplicationData(pos.getX(), pos.getY(), pos.getZ(), app.getInfo().getFormattedId(), container));
                    }

                    if (app instanceof SystemApp) {
                        ((SystemApp) app).setLaptop(null);
                    }

                    window.handleClose();
                    windows.remove(i);
                    return;
                }
            }
        }
    }

    private void addWindow(dev.ultreon.devices.core.Window<Application> window) {
        if (hasReachedWindowLimit())
            return;

        updateWindowStack();
        windows.addFirst(window);
    }

    private void updateWindowStack() {
        for (int i = windows.size() - 1; i >= 0; i--) {
            if (windows.get(i) != null) {
                if (i + 1 < windows.size()) {
                    if (i == 0 || windows.get(i - 1) != null) {
                        if (windows.get(i + 1) == null) {
                            windows.add(i + 1, windows.get(i));
                            windows.remove(i);
                        }
                    }
                }
            }
        }
    }

    private boolean hasReachedWindowLimit() {
//        for (Window<?> window : windows) {
//           // if (window == null) return false;
//        }
        return false;
    }

    private boolean isMouseOnScreen(int mouseX, int mouseY) {
        int posX = (width - getScreenWidth()) / 2;
        int posY = (height - getScreenHeight()) / 2;
        return isMouseInside(mouseX, mouseY, posX, posY, posX + getScreenWidth(), posY + getScreenHeight());
    }

    private boolean isMouseWithinWindowBar(int mouseX, int mouseY, dev.ultreon.devices.core.Window<?> window) {
        if (window == null) return false;
        int posX = (width - getScreenWidth()) / 2;
        int posY = (height - getScreenHeight()) / 2;
        return isMouseInside(mouseX, mouseY, posX + window.offsetX + 1, posY + window.offsetY + 1, posX + window.offsetX + window.width - 13, posY + window.offsetY + 11);
    }

    private boolean isMouseWithinWindow(int mouseX, int mouseY, dev.ultreon.devices.core.Window<?> window) {
        if (window == null) return false;
        int posX = (width - getScreenWidth()) / 2;
        int posY = (height - getScreenHeight()) / 2;
        return isMouseInside(mouseX, mouseY, posX + window.offsetX, posY + window.offsetY, posX + window.offsetX + window.width, posY + window.offsetY + window.height);
    }

    public boolean isMouseWithinApp(int mouseX, int mouseY, dev.ultreon.devices.core.Window<?> window) {
        int posX = (width - getScreenWidth()) / 2;
        int posY = (height - getScreenHeight()) / 2;
        return isMouseInside(mouseX, mouseY, posX + window.offsetX + 1, posY + window.offsetY + 13, posX + window.offsetX + window.width - 1, posY + window.offsetY + window.height - 1);
    }

    public boolean isApplicationRunning(AppInfo info) {
        for (Window<?> window : windows) {
            if (window != null && ((Application) window.content).getInfo() == info) {
                return true;
            }
        }
        return false;
    }

    public void nextWallpaper() {
        if (!currentWallpaper.isBuiltIn()) return;
        if (currentWallpaper.location + 1 < WALLPAPERS.size()) {
            this.currentWallpaper = new Wallpaper(currentWallpaper.location+1);
        }
        wallpaperUpdated();
    }

    public void prevWallpaper() {
        if (currentWallpaper.location - 1 >= 0) {
            this.currentWallpaper = new Wallpaper(currentWallpaper.location-1);
        }
        wallpaperUpdated();
    }

    private void wallpaperUpdated() {
        if (currentWallpaper.isBuiltIn()) {
            wallpaper.setImage(WALLPAPERS.get(currentWallpaper.location));
        } else {
            wallpaper.setImage(currentWallpaper.url);
        }
    }

    public void setWallpaper(String url) {
        currentWallpaper = new Wallpaper(url);
        wallpaperUpdated();
    }

    public void setWallpaper(int wall) {
        currentWallpaper = new Wallpaper(wall);
        wallpaperUpdated();
    }

    public Wallpaper getCurrentWallpaper() {
        return currentWallpaper;
    }

    public List<Identifier> getWallapapers() {
        return ImmutableList.copyOf(WALLPAPERS);
    }

    @Nullable
    public Application getApplication(String appId) {
        return APPLICATIONS.stream().filter(app -> app.getInfo().getFormattedId().equals(appId)).findFirst().orElse(null);
    }

    @Override
    public List<AppInfo> getInstalledApplications() {
        return ImmutableList.copyOf(installedApps);
    }

    public boolean isApplicationInstalled(AppInfo info) {
        return info.isSystemApp() || installedApps.contains(info);
    }

    public boolean isApplicationNotInstalled(AppInfo info) {
        return !isApplicationInstalled(info);
    }

    private boolean isValidApplication(AppInfo info) {
        if (UltreonDevicesCommon.hasAllowedApplications()) {
            return UltreonDevicesCommon.getAllowedApplications().contains(info);
        }
        return true;
    }

    private boolean isInvalidApplication(AppInfo info) {
        return !isValidApplication(info);
    }

    public void installApplication(AppInfo info, @Nullable Callback<Object> callback) {
        if (isValidApplication(info)) {
            Task task = new TaskInstallApp(info, pos, true);
            task.setCallback((tag, success) ->
            {
                if (success) {
                    installedApps.add(info);
                    installedApps.sort(AppInfo.SORT_NAME);
                }
                if (callback != null) {
                    callback.execute(null, success);
                }
            });
            TaskManager.sendTask(task);
        }
    }

    public void removeApplication(AppInfo info, @Nullable Callback<Object> callback) {
        if (!isValidApplication(info))
            return;

        Task task = new TaskInstallApp(info, pos, false);
        task.setCallback((tag, success) ->
        {
            if (success) {
                installedApps.remove(info);
            }
            if (callback != null) {
                callback.execute(null, success);
            }
        });
        TaskManager.sendTask(task);
    }

    public List<Application> getApplications() {
        return APPLICATIONS;
    }

    public TaskBar getTaskBar() {
        return bar;
    }

    public Settings getSettings() {
        return settings;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void openContext(Layout layout, int x, int y) {
        layout.updateComponents(x, y);
        context = layout;
        layout.init();
    }

    @Override
    public boolean hasContext() {
        return context != null;
    }

    @Override
    public void closeContext() {
        context = null;
        dragging = false;
    }

    public static final class Wallpaper {
        private final String url;
        private final int location;

        public String getUrl() {
            return url;
        }

        public int getLocation() {
            return location;
        }

        private Wallpaper(CompoundTag tag) {
            var url = tag.getString("url").orElse(null);
            var location = tag.getIntOr("location", 0);
            if (tag.contains("url")) {
                if (!OnlineRequest.isSafeAddress(url)) {
                    // Reset to default wallpaper.
                    this.url = null;
                    this.location = 0;
                } else {
                    this.url = url;
                    this.location = -87;
                }
            } else {
                this.url = null;
                this.location = location;
            }
        }
        private Wallpaper(String url) {
            this.url = url;
            this.location = -87;
        }

        private Wallpaper(int location) {
            this.location = location;
            this.url = null;
        }

        public boolean isBuiltIn() {
            return this.location != -87;
        }

        public Tag serialize() {
            var a = new CompoundTag();
            if (isBuiltIn()) {
                a.putInt("location", location);
            } else {
                a.putString("url", this.url);
            }
            return a;
        }
    }
}
