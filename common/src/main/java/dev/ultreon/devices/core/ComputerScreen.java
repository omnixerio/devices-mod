package dev.ultreon.devices.core;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ultreon.devices.Devices;
import dev.ultreon.devices.api.ApplicationManager;
import dev.ultreon.devices.api.app.Dialog;
import dev.ultreon.devices.api.app.System;
import dev.ultreon.devices.api.app.*;
import dev.ultreon.devices.api.app.component.Image;
import dev.ultreon.devices.api.io.Drive;
import dev.ultreon.devices.api.task.Callback;
import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.api.task.TaskManager;
import dev.ultreon.devices.api.video.CustomResolution;
import dev.ultreon.devices.api.video.VideoInfo;
import dev.ultreon.devices.block.entity.computer.ComputerBlockEntity;
import dev.ultreon.devices.core.io.task.TaskGetMainDrive;
import dev.ultreon.devices.core.task.TaskInstallApp;
import dev.ultreon.devices.object.AppInfo;
import dev.ultreon.devices.programs.system.DiagnosticsApp;
import dev.ultreon.devices.programs.system.DisplayResolution;
import dev.ultreon.devices.programs.system.PredefinedResolution;
import dev.ultreon.devices.programs.system.SystemApp;
import dev.ultreon.devices.programs.system.component.FileBrowser;
import dev.ultreon.devices.programs.system.component.FileInfo;
import dev.ultreon.devices.programs.system.task.TaskUpdateApplicationData;
import dev.ultreon.devices.programs.system.task.TaskUpdateSystemData;
import dev.ultreon.devices.util.GLHelper;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/// Laptop GUI class.
///
/// @author MrCrayfish, Qubilux
public class ComputerScreen extends Screen implements System {
    public static final int ID = 1;
    public static final ResourceLocation ICON_TEXTURES = ResourceLocation.fromNamespaceAndPath(Devices.MOD_ID, "textures/atlas/app_icons.png");
    public static final int ICON_SIZE = 14;
    private static final ResourceLocation LAPTOP_FONT = Devices.res("laptop");
    private static Font font;
    private static final ResourceLocation LAPTOP_GUI = ResourceLocation.fromNamespaceAndPath(Devices.MOD_ID, "textures/gui/laptop.png");
    private static final List<Application> APPLICATIONS = new ArrayList<>();
    private static boolean worldLess;
    private static ComputerScreen instance;
    private Double dragWindowFromX;
    private Double dragWindowFromY;
    private final VideoInfo videoInfo;
    private final Map<UUID, Drive> drives = new HashMap<>();
    private Dialog systemDialog = null;
    private Window<Dialog> systemDialogWindow = null;
    private static boolean loaded;
    private final Bios bios;

    public static List<Application> getApplicationsForFabric() {
        return APPLICATIONS;
    }

    public static List<ResourceLocation> getWallpapers() {
        return ImmutableList.copyOf(WALLPAPERS);
    }

    private static final List<ResourceLocation> WALLPAPERS = new ArrayList<>();

    private static final int BORDER = 10;
    private static final List<Runnable> tasks = new CopyOnWriteArrayList<>();
    private static System system;
    private static BlockPos pos;
    private static Drive mainDrive;
    private final Settings settings;
    private final TaskBar bar;
    final CopyOnWriteArrayList<Window<?>> windows;
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

    public static Font getFont() {
        if (font == null) {
            font = Minecraft.getInstance().font;
        }
        return font;
    }

    /// Creates a new laptop GUI.
    ///
    /// @param laptop the block entity of the laptop in-game, if the laptop is not in-game, the level passed to it should be null.
    public ComputerScreen(ComputerBlockEntity laptop) {
        this(laptop, false);
    }

    /// Creates a new laptop GUI.
    ///
    /// @param laptop the block entity of the laptop in-game, if the laptop is not in-game, the level passed to it should be null.
    public ComputerScreen(ComputerBlockEntity laptop, boolean worldLess) {
        super(Component.literal("Laptop"));

        instance = this;
        this.bios = determineBios(laptop);

        // Laptop data.
        this.appData = laptop.getApplicationData();
        this.systemData = laptop.getSystemData();

        CompoundTag videoInfoData = this.systemData.getCompound("videoInfo");
        this.videoInfo = new VideoInfo(videoInfoData);

        // Windows
        this.windows = new CopyOnWriteArrayList<>() {
            @Override
            public Window<?> get(int index) {
                try {
                    return super.get(index);
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            public boolean add(Window<?> window) {
                window.removed = false;
                return super.add(window);
            }
        };

        // Settings etc.
        this.settings = Settings.fromTag(systemData.getCompound("Settings"));

        // GUI Components
        CompoundTag taskBarTag = systemData.getCompound("TaskBar");
        systemData.put("TaskBar", taskBarTag);
        this.bar = new TaskBar(this, taskBarTag);

        // Wallpaper stuff
        this.currentWallpaper = systemData.contains("CurrentWallpaper", 10) ? new Wallpaper(systemData.getCompound("CurrentWallpaper")) : null;
        if (this.currentWallpaper == null) this.currentWallpaper = new Wallpaper(0);
        ComputerScreen.system = this;
        ComputerScreen.pos = laptop.getBlockPos();
        this.wallpaperLayout = new Layout(getScreenWidth(), getScreenHeight());
        this.wallpaper = new Image(0, 0, getScreenWidth(), getScreenHeight());
        if (currentWallpaper.isBuiltIn()) {
            wallpaper.setImage(WALLPAPERS.get(currentWallpaper.location));
        } else {
            wallpaper.setImage(currentWallpaper.locationPath);
        }
        this.wallpaperLayout.addComponent(this.wallpaper);
        this.wallpaperLayout.handleLoad();

        // World-less flag.
        ComputerScreen.worldLess = worldLess;
    }

    private Bios determineBios(ComputerBlockEntity laptop) {
        if (laptop == null) return WorldLessBios.INSTANCE;
        return laptop.getBios();
    }

    public static ComputerScreen getInstance() {
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

    public static @Nullable Drive getDrive(UUID drive) {
        if (drive == null) return null;
        return instance.drives.get(drive);
    }

    public CompoundTag getModSystemTag(String modId) {
        CompoundTag mods = systemData.getCompound("Mods");
        systemData.put("Mods", mods);
        CompoundTag mod = mods.getCompound(modId);
        mods.put(modId, mod);
        return mod;
    }

    public static boolean isWorldLess() {
        return worldLess;
    }

    /// Returns the position of the laptop the player is currently using.
    /// This method can ONLY be called when the laptop GUI is open, otherwise it will return a null position.
    ///
    /// @return the position of the laptop currently in use
    @Nullable
    public static BlockPos getPos() {
        return pos;
    }

    /// Add a wallpaper to the list of available wallpapers.
    ///
    /// @param wallpaper location to the wallpaper texture, if null, the wallpaper will not be added.
    public static void addWallpaper(ResourceLocation wallpaper) {
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

    public static void getOrLoadMainDrive(Callback<@NotNull Drive> callback) {
        if (ComputerScreen.mainDrive != null) {
            callback.execute(ComputerScreen.mainDrive, true);
            return;
        }

        TaskGetMainDrive task = new TaskGetMainDrive(pos);
        task.setCallback((tag, success) -> {
            if (!success) {
                callback.execute(null, false);
                return;
            }

            if (tag != null) {
                ComputerScreen.mainDrive = new Drive(tag.getCompound("main_drive"));
                callback.execute(ComputerScreen.mainDrive, true);
                return;
            }

            callback.execute(null, false);
        });

        TaskManager.sendTask(task);
    }

    public static void setMainDrive(Drive mainDrive) {
        if (ComputerScreen.mainDrive == null) {
            ComputerScreen.mainDrive = mainDrive;
        }
    }

    /// Run a task later in render thread.
    ///
    /// @param task the task to run.
    public static void runLater(Runnable task) {
        tasks.add(task);
    }

    /// Initialize the Laptop GUI.
    @Override
    public void init() {
        int posX = (width - getDeviceWidth()) / 2;
        int posY = (height - getDeviceHeight()) / 2;
        bar.init(posX + BORDER, posY + getDeviceHeight() - 28);

        installedApps.clear();
        ListTag list = systemData.getList("InstalledApps", Tag.TAG_STRING);
        for (int i = 0; i < list.size(); i++) {
            AppInfo info = ApplicationManager.getApplication(ResourceLocation.tryParse(list.getString(i)));
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
            Window<?> window = windows.get(i);
            if (window != null) {
                window.close();
                i--;
            }
        }

        /* Send system data */
        this.updateSystemData();

        ComputerScreen.pos = null;
        ComputerScreen.system = null;
        ComputerScreen.mainDrive = null;
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

    /// Handles Minecraft GUI resizing.
    ///
    /// @param minecraft the Minecraft instance
    /// @param width     the new width
    /// @param height    the new height
    @Override
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);

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

    /// Ticking the laptop.
    @Override
    public void tick() {
        try {
            bar.onTick();

            for (Window<?> window : List.copyOf(windows)) {
                window.onTick();
                if (window.removed) {
                    windows.remove(window);
                }
            }

            FileBrowser.refreshList = false;
        } catch (Exception e) {
            bsod(e);
        }
    }

    @Override
    public void render(final @NotNull GuiGraphics graphics, final int mouseX, final int mouseY, float partialTicks) {
        super.renderBackground(graphics, mouseX, mouseY, partialTicks);

        if (bsod != null) {
            renderBsod(graphics, mouseX, mouseY, partialTicks);
            return;
        }

        RenderSystem.disableDepthTest();
        PoseStack.Pose last = graphics.pose().last();

        try {
            renderLaptop(graphics, mouseX, mouseY, partialTicks);
        } catch (NullPointerException e) {
            while (graphics.pose().last() != last) {
                graphics.pose().popPose();
            }
            RenderSystem.disableScissor();
            bsod(e);// null
        } catch (Exception e) {
            while (graphics.pose().last() != last) {
                graphics.pose().popPose();
            }
            RenderSystem.disableScissor();
            bsod(e);
        }
    }

    public void renderBsod(final @NotNull GuiGraphics graphics, final int mouseX, final int mouseY, float partialTicks) {
        renderBezels(graphics, mouseX, mouseY, partialTicks);
        int posX = (width - getDeviceWidth()) / 2;
        int posY = (height - getDeviceHeight()) / 2;
        graphics.fill(posX + 10, posY + 10, posX + getDeviceWidth() - 10, posY + getDeviceHeight() - 10, new Color(0, 0, 255).getRGB());
        var bo = new ByteArrayOutputStream();

        double scale = Minecraft.getInstance().getWindow().getGuiScale();

        var b = new PrintStream(bo);
        bsod.throwable.printStackTrace(b);
        var str = bo.toString();
        drawLines(graphics, ComputerScreen.getFont(), str, posX + 10, posY + 10 + getFont().lineHeight * 2, (int) ((getDeviceWidth() - 10) * scale), new Color(255, 255, 255).getRGB());
        graphics.pose().pushPose();
        graphics.pose().scale(2, 2, 0);
        graphics.pose().translate((posX + 10) / 2f, (posY + 10) / 2f, 0);
        graphics.drawString(getFont(), "System has crashed!", 0, 0, new Color(255, 255, 255).getRGB());
        graphics.pose().popPose();
    }

    public static void drawLines(GuiGraphics graphics, Font font, String text, int x, int y, int width, int color) {
        var lines = new ArrayList<String>();
        font.getSplitter().splitLines(FormattedText.of(text.replaceAll("\r\n", "\n").replaceAll("\r", "\n")), width, Style.EMPTY).forEach(b -> lines.add(b.getString()));
        var totalTextHeight = font.lineHeight * lines.size();
        var textScale = (instance.videoInfo.getResolution().height() - 20 - (getFont().lineHeight * 2)) / (float) totalTextHeight;
        textScale = (float) (1f / Minecraft.getInstance().getWindow().getGuiScale());
        textScale = Math.max(0.5f, textScale);
        graphics.pose().pushPose();
        graphics.pose().scale(textScale, textScale, 1);
        graphics.pose().translate(x / textScale, (y + 3) / textScale, 0);
        //poseStack.translate();
        var lineNr = 0;
        for (String s : lines) {
            graphics.drawString(font, s.replaceAll("\t", "    "), 0, lineNr * font.lineHeight, color);
            lineNr++;
        }
        graphics.pose().popPose();
    }

    public void renderBezels(final @NotNull GuiGraphics graphics, final int mouseX, final int mouseY, float partialTicks) {
        tasks.clear();

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, LAPTOP_GUI);

        //*************************//
        //     Physical Screen     //
        //*************************//
        int deviceWidth = videoInfo.getResolution().width() + BORDER * 2;
        int deviceHeight = videoInfo.getResolution().height() + BORDER * 2;
        int posX = (width - deviceWidth) / 2;
        int posY = (height - deviceHeight) / 2;

        // Corners
        graphics.blit(LAPTOP_GUI, posX, posY, 0, 0, BORDER, BORDER); // TOP-LEFT
        graphics.blit(LAPTOP_GUI, posX + deviceWidth - BORDER, posY, 11, 0, BORDER, BORDER); // TOP-RIGHT
        graphics.blit(LAPTOP_GUI, posX + deviceWidth - BORDER, posY + deviceHeight - BORDER, 11, 11, BORDER, BORDER); // BOTTOM-RIGHT
        graphics.blit(LAPTOP_GUI, posX, posY + deviceHeight - BORDER, 0, 11, BORDER, BORDER); // BOTTOM-LEFT

        // Edges
        graphics.blit(LAPTOP_GUI, posX + BORDER, posY, getScreenWidth(), BORDER, 10, 0, 1, BORDER, 256, 256); // TOP
        graphics.blit(LAPTOP_GUI, posX + deviceWidth - BORDER, posY + BORDER, BORDER, getScreenHeight(), 11, 10, BORDER, 1, 256, 256); // RIGHT
        graphics.blit(LAPTOP_GUI, posX + BORDER, posY + deviceHeight - BORDER, getScreenWidth(), BORDER, 10, 11, 1, BORDER, 256, 256); // BOTTOM
        graphics.blit(LAPTOP_GUI, posX, posY + BORDER, BORDER, getScreenHeight(), 0, 11, BORDER, 1, 256, 256); // LEFT

        // Center
        graphics.blit(LAPTOP_GUI, posX + BORDER, posY + BORDER, getScreenWidth(), getScreenHeight(), 10, 10, 1, 1, 256, 256);

    }

    /// Render the laptop screen.
    ///
    /// @param graphics     gui graphics helper
    /// @param mouseX       the current mouse X position.
    /// @param mouseY       the current mouse Y position.
    /// @param partialTicks the rendering partial ticks that NeoForge give use (which is useless here).
    public void renderLaptop(final @NotNull GuiGraphics graphics, final int mouseX, final int mouseY, float partialTicks) {
        int posX = (width - getDeviceWidth()) / 2;
        int posY = (height - getDeviceHeight()) / 2;
        // Fixes the strange partialTicks that NeoForge decided to give us
        final float frameTime = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks();
        for (Runnable task : tasks) {
            task.run();
        }

        renderBezels(graphics, mouseX, mouseY, partialTicks);

        GLHelper.pushScissor(posX, posY, videoInfo.getResolution().width() + BORDER, videoInfo.getResolution().height() + BORDER);
        //*******************//
        //     Wallpaper     //
        //*******************//
        Image.CACHE.forEach((s, cachedImage) -> cachedImage.delete());
        this.wallpaperLayout.render(graphics, this, this.minecraft, posX + 10, posY + 10, mouseX, mouseY, true, partialTicks);
        boolean insideContext = false;
        if (context != null) {
            insideContext = isMouseInside(mouseX, mouseY, context.xPosition, context.yPosition, context.xPosition + context.width, context.yPosition + context.height);
        }

        //****************//
        //     Window     //
        //****************//
        graphics.pose().pushPose();
        {
            renderWindows(graphics, mouseX, mouseY, partialTicks, posX, posY, insideContext);
        }
        bar.render(graphics, this, minecraft, posX + 10, posY + getDeviceHeight() - 28, mouseX, mouseY, frameTime);

        graphics.pose().translate(0, 0, 100);
        if (context != null) {
            context.render(graphics, this, minecraft, context.xPosition, context.yPosition, mouseX, mouseY, true, frameTime);
        }

        graphics.pose().translate(0, 0, 200);
        if (systemDialogWindow != null && systemDialogWindow.removed) {
            systemDialog = null;
            systemDialogWindow = null;
        }

        if (systemDialogWindow != null) {
            graphics.fill(posX + 10, posY + 10, posX + getDeviceWidth() - 10, posY + getDeviceHeight() - 10, 0x60000000);
            int w = getScreenWidth() / 2 - systemDialog.getWidth() / 2;
            int h = getScreenHeight() / 2 - systemDialog.getHeight() / 2;
            systemDialogWindow.render(graphics, this, minecraft, posX + BORDER, posY + BORDER, mouseX, mouseY, true, partialTicks);
        }

        graphics.pose().popPose();

        //****************************//
        // Render the Application Bar //
        //****************************//
        Image.CACHE.entrySet().removeIf(entry -> {
            Image.CachedImage cachedImage = entry.getValue();
            if (cachedImage.isDynamic() && cachedImage.isPendingDeletion()) {
                int texture = cachedImage.getTextureId();
                if (texture != -1) {
                    RenderSystem.deleteTexture(texture);
                }
                return true;
            }
            return false;
        });

        GLHelper.popScissor();

        GLHelper.clearScissorStack();
    }

    private void renderWindows(@NotNull GuiGraphics graphics,
                               int mouseX,
                               int mouseY,
                               float partialTicks,
                               int posX,
                               int posY,
                               boolean insideContext) {
        //   Window<?>[] windows1 = Arrays.stream(windows.toArray()).filter(Objects::nonNull).toArray(Window<?>[]::new);
        for (int i = windows.size() - 1; i >= 0; i--) {
            var window = windows.get(i);
            if (window != null) {
                renderWindow(graphics, mouseX, mouseY, partialTicks, posX, posY, insideContext, i, window);
                graphics.pose().translate(0, 0, 400);
            }
        }
    }

    private void renderWindow(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, int posX, int posY, boolean insideContext, int i, Window<?> window) {
        PoseStack.Pose last = graphics.pose().last();
        try {
            if (i == 0 && systemDialogWindow == null) {
                window.render(graphics, this, minecraft, posX + BORDER, posY + BORDER, mouseX, mouseY, !insideContext && systemDialogWindow == null, partialTicks);
            } else {
                window.render(graphics, this, minecraft, posX + BORDER, posY + BORDER, Integer.MAX_VALUE, Integer.MAX_VALUE, false, partialTicks);
            }
            if (i == 0 && systemDialogWindow == null) {
                window.renderOverlay(graphics, this, minecraft, posX + BORDER, posY + BORDER, mouseX, mouseY, !insideContext && systemDialogWindow == null, partialTicks);
            } else {
                window.renderOverlay(graphics, this, minecraft, posX + BORDER, posY + BORDER, Integer.MAX_VALUE, Integer.MAX_VALUE, false, partialTicks);
            }
        } catch (Exception e) {
            while (graphics.pose().last() != last) {
                graphics.pose().popPose();
            }
            RenderSystem.disableScissor();
            Devices.LOGGER.error("An error has occurred.", e);
            Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
            message.setTitle("Error");
            CompoundTag intent = new CompoundTag();
            if (window.content instanceof Application app) {
                openDiagnostics(app, intent);
            }
        }
    }

    private void openDiagnostics(Application app, CompoundTag intent) {
        AppInfo info = app.getInfo();
        if (info != null) {
            String name = info.getName();
            if (name == null) {
                name = info.getId().toString();
            }
            intent.putString("name", name);
        }
        launchApp(ApplicationManager.getApplication(Devices.res("diagnostics")), intent);
        closeApplication(app);
    }

    private boolean isMouseInside(int mouseX, int mouseY, int startX, int startY, int endX, int endY) {
        return mouseX >= startX && mouseX <= endX && mouseY >= startY && mouseY <= endY;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        try {
            return mouseClickedInternal(mouseX, mouseY, mouseButton);
        } catch (NullPointerException e) {
            bsod(e);// null
        } catch (Exception e) {
            bsod(e);
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void bsod(Throwable e) {
        this.bsod = new BSOD(e);
        Devices.LOGGER.error("A fatal error has occurred.", e);
    }

    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public void setDisplayResolution(PredefinedResolution newValue) {
        if (this.videoInfo != null) {
            this.videoInfo.setResolution(newValue);
        }
    }

    public void requestPermission(PermissionRequest permissionRequest, Consumer<PermissionResult> callback) {
        Permission permission = permissionRequest.permission();
        if (permission == null) {
            callback.accept(PermissionResult.DENIED);
        }

        String reason = permissionRequest.reason();
        if (reason == null) {
            reason = "App did not specify a reason.";
        }

        setSystemDialog(new Dialog.Permission(permissionRequest, result -> {
            PermissionManager.grant(permission, () -> {
                callback.accept(result);
            });
        }, reason));
    }

    private static final class BSOD {
        private final Throwable throwable;

        public BSOD(Throwable e) {
            this.throwable = e;
        }
    }

    @SuppressWarnings("unchecked")
    public boolean mouseClickedInternal(double mouseX, double mouseY, int mouseButton) {
        if (bsod != null) {
            return true;
        }

        if (systemDialogWindow != null) {
            int posX = (width - getScreenWidth()) / 2;
            int posY = (height - getScreenHeight()) / 2;

            systemDialogWindow.handleMouseClick(this, posX, posY, (int) mouseX, (int) mouseY, mouseButton);
            return true;
        }

        this.lastMouseX = (int) mouseX;
        this.lastMouseY = (int) mouseY;

        int posX = (width - getScreenWidth()) / 2;
        int posY = (height - getScreenHeight()) / 2;

        if (this.context != null) {
            int dropdownX = context.xPosition;
            int dropdownY = context.yPosition;
            if (isMouseInside((int) mouseX, (int) mouseY, dropdownX, dropdownY, dropdownX + context.width, dropdownY + context.height)) {
                this.context.handleMouseClick((int) mouseX, (int) mouseY, mouseButton);
                return true;
            } else {
                this.context = null;
            }
        }

        this.bar.handleClick(this, posX, posY + getScreenHeight() - TaskBar.BAR_HEIGHT, (int) mouseX, (int) mouseY, mouseButton);

        for (int i = 0; i < windows.size(); i++) {
            Window<Application> window = (Window<Application>) windows.get(i);
            if (window != null) {
                try {
                    Window<Dialog> dialogWindow = window.getContent().getActiveDialog();
                    if (isMouseWithinWindow((int) mouseX, (int) mouseY, window) || isMouseWithinWindow((int) mouseX, (int) mouseY, dialogWindow)) {
                        windows.remove(i);
                        i--;
                        updateWindowStack();
                        windows.addFirst(window);

                        windows.getFirst().handleMouseClick(this, posX, posY, (int) mouseX, (int) mouseY, mouseButton);

                        if (isMouseWithinWindowBar((int) mouseX, (int) mouseY, dialogWindow)) {
                            dragWindowFromX = mouseX - dialogWindow.offsetX;
                            dragWindowFromY = mouseY - dialogWindow.offsetY;
                            this.dragging = true;
                            return false;
                        }

                        if (isMouseWithinWindowBar((int) mouseX, (int) mouseY, window) && dialogWindow == null) {
                            dragWindowFromX = mouseX - window.offsetX;
                            dragWindowFromY = mouseY - window.offsetY;
                            this.dragging = true;
                            return false;
                        }
                        break;
                    }
                } catch (Exception e) {
                    Devices.LOGGER.error("An error has occurred.", e);
                    Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
                    message.setTitle("Error");
                    if (windows.isEmpty() || windows.getFirst() == null) {
                        CompoundTag intent = new CompoundTag();
                        AppInfo info = window.content.getInfo();
                        if (info != null) {
                            intent.putString("name", info.getName());
                        }
                        launchApp(ApplicationManager.getApplication(Devices.res("diagnostics")), intent);
                    } else {
                        setSystemDialog(message);
                    }
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void setSystemDialog(Dialog message) {
        systemDialog = message;

        systemDialogWindow = new Window<>(systemDialog, null);
        systemDialogWindow.init(width / 2 - systemDialog.getWidth() / 2, height / 2 - systemDialog.getHeight() / 2, null);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state) {
        if (bsod != null) {
            return true;
        }

        if (systemDialogWindow != null) {
            systemDialogWindow.handleMouseRelease((int) mouseX, (int) mouseY, state);
            return true;
        }

        super.mouseReleased(mouseX, mouseY, state);
        this.dragging = false;
        dragWindowFromX = null;
        dragWindowFromY = null;
        try {
            if (this.context != null) {
                int dropdownX = context.xPosition;
                int dropdownY = context.yPosition;
                if (isMouseInside((int) mouseX, (int) mouseY, dropdownX, dropdownY, dropdownX + context.width, dropdownY + context.height)) {
                    this.context.handleMouseRelease((int) mouseX, (int) mouseY, state);
                }
            } else if (!windows.isEmpty()) {
                windows.getFirst().handleMouseRelease((int) mouseX, (int) mouseY, state);
            }
        } catch (Exception e) {
            Devices.LOGGER.error("An error has occurred.", e);
            Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
            message.setTitle("Error");
            setSystemDialog(message);
        }
        return true;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (bsod != null) {
            return false;
        }

        if (systemDialogWindow != null) {
            systemDialogWindow.handleCharTyped(codePoint, modifiers);
            return true;
        }

        boolean override = super.charTyped(codePoint, modifiers);
        try {
            if (!override && !windows.isEmpty())
                windows.getFirst().handleCharTyped(codePoint, modifiers);
        } catch (Exception e) {
            Devices.LOGGER.error("An error has occurred.", e);
            Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
            message.setTitle("Error");
            setSystemDialog(message);
        }
        return override;
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) return true;

        if (bsod != null) {
            this.bios.systemExit(PowerMode.REBOOT);
            return true;
        }

        if (systemDialogWindow != null) {
            systemDialogWindow.handleKeyPressed(keyCode, scanCode, modifiers);
            return true;
        }

        final boolean override = super.keyPressed(keyCode, scanCode, modifiers);

        try {
            if (!pressed.contains(keyCode) && !override && !windows.isEmpty()) {
                windows.getFirst().handleKeyPressed(keyCode, scanCode, modifiers);
            }
        } catch (Exception e) {
            Devices.LOGGER.error("An error has occurred.", e);
            Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
            message.setTitle("Error");
            setSystemDialog(message);
            return true;
        }
        pressed.add(keyCode);
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        pressed.remove(keyCode);

        boolean b = super.keyReleased(keyCode, scanCode, modifiers);

        try {
            if (keyCode >= 32 && keyCode < 256 && !windows.isEmpty()) {
                windows.getFirst().handleKeyReleased(keyCode, scanCode, modifiers);
                return true;
            }
        } catch (Exception e) {
            Devices.LOGGER.error("An error has occurred.", e);
            Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
            message.setTitle("Error");
            setSystemDialog(message);
        }
        return b;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (bsod != null) {
            return true;
        }

        if (systemDialogWindow != null) {
            systemDialogWindow.handleMouseDrag((int) mouseX, (int) mouseY, button);
            return true;
        }

        int posX = (width - getScreenWidth()) / 2;
        int posY = (height - getScreenHeight()) / 2;

        try {
            if (this.context != null) {
                int dropdownX = context.xPosition;
                int dropdownY = context.yPosition;
                if (isMouseInside((int) mouseX, (int) mouseY, dropdownX, dropdownY, dropdownX + context.width, dropdownY + context.height)) {
                    this.context.handleMouseDrag((int) mouseX, (int) mouseY, button);
                }
                return true;
            }

            if (!windows.isEmpty()) {
                Window<Application> window = (Window<Application>) windows.getFirst();
                Window<Dialog> dialogWindow = window.getContent().getActiveDialog();
                if (dragging) {
                    if (isMouseOnScreen((int) mouseX, (int) mouseY) && dragWindowFromX != null && dragWindowFromY != null) {
                        Objects.requireNonNullElse(dialogWindow, window).handleWindowMove(posX, posY, (int) ((dragX + mouseX) - dragWindowFromX), (int) ((dragY + mouseY) - dragWindowFromY));
                    } else {
                        dragging = false;
                    }
                } else {
                    if (isMouseWithinWindow((int) mouseX, (int) mouseY, window) || isMouseWithinWindow((int) mouseX, (int) mouseY, dialogWindow)) {
                        window.handleMouseDrag((int) mouseX, (int) mouseY, button);
                    }
                }
            }
        } catch (Exception e) {
            Devices.LOGGER.error("An error has occurred.", e);
            Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
            message.setTitle("Error");
            setSystemDialog(message);
        }
        this.lastMouseX = (int) mouseX;
        this.lastMouseY = (int) mouseY;
        return true;
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        this.lastMouseX = (int) pMouseX;
        this.lastMouseY = (int) pMouseY;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        if (bsod != null) {
            return true;
        }

        if (systemDialogWindow != null) {
            systemDialogWindow.handleMouseScroll((int) mouseX, (int) mouseY, deltaY, deltaY >= 0);
            return true;
        }

        if (deltaY != 0) {
            try {
                if (!windows.isEmpty()) {
                    windows.getFirst().handleMouseScroll((int) mouseX, (int) mouseY, deltaY, deltaY >= 0);
                }
            } catch (Exception e) {
                Devices.LOGGER.error("An error has occurred.", e);
                Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
                message.setTitle("Error");
                setSystemDialog(message);
            }
        }
        return true;
    }

    public void renderComponentTooltip(@NotNull GuiGraphics graphics, @NotNull List<Component> tooltips, int x, int y) {
        if (minecraft != null) {
            graphics.renderComponentTooltip(minecraft.font, tooltips, x, y);
        }
    }

    @SuppressWarnings("ReassignedVariable")
    public Pair<Application, Boolean> sendApplicationToFront(AppInfo info) {
        int i = 0;
        for (; i < windows.size(); i++) {
            Window<?> window = windows.get(i);
            if (window != null && window.content instanceof Application && ((Application) window.content).getInfo() == info) {
                windows.remove(i);
                updateWindowStack();
                windows.addFirst(window);
                return Pair.of((Application) window.content, true);
            }
        }
        return Pair.of(null, false);
    }

    @Override
    public Application launchApp(AppInfo info) {
        return launchApp(info, null);
    }

    @Override
    public Application launchApp(AppInfo info, CompoundTag intentTag) {
        Optional<Application> optional = APPLICATIONS.stream().filter(app -> app.getInfo() == info).findFirst();
        Application[] a = new Application[]{null};
        optional.ifPresent(application -> a[0] = launchApp(application, intentTag));
        return a[0];
    }

    @Override
    public @Nullable Application loadApp(AppInfo info) {
        Optional<Application> optional = APPLICATIONS.stream().filter(app -> app.getInfo() == info).findFirst();
        return optional.orElse(null);
    }

    private Application launchApp(Application app, CompoundTag intent) {
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

            Window<Application> window = new Window<>(app, this);
            window.init((width - getScreenWidth()) / 2, (height - getScreenHeight()) / 2, intent);

            if (appData.contains(app.getInfo().getFormattedId())) {
                app.load(appData.getCompound(app.getInfo().getFormattedId()));
            }

            if (app.getCurrentLayout() == null) {
                app.restoreDefaultLayout();
            }

            addWindow(window);

            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
        } catch (Exception e) {
            Devices.LOGGER.error("An error has occurred.", e);
            AppInfo info = ApplicationManager.getApplication(Devices.res("diagnostics"));
            system.launchApp(info);
        }
        return app;
    }

    public void launchApp(AppInfo info, FileInfo file, Callback<Application> callback) {
        if (isApplicationNotInstalled(info)) {
            if (callback != null) {
                callback.execute(null, false);
            }
            return;
        }

        if (isInvalidApplication(info)) {
            if (callback != null) {
                callback.execute(null, false);
            }
            return;
        }

        try {
            Optional<Application> optional = APPLICATIONS.stream().filter(app -> app.getInfo() == info).findFirst();
            if (optional.isPresent()) {
                Application application = optional.get();
                boolean alreadyRunning = isApplicationRunning(info);
                launchApp(application, null);
                if (isApplicationRunning(info)) {
                    if (!application.handleFile(file, (response, success) -> {
                        if (success) {
                            if (!alreadyRunning) {
                                closeApplication(application);
                            }
                            callback.execute(application, true);
                        } else {
                            callback.execute(application, false);
                        }
                    })) {
                        system.openDialog(new Dialog.Message("The file could not be opened."));
                    }
                }
            }
        } catch (Exception e) {
            Devices.LOGGER.error("An error has occurred.", e);
            AppInfo info1 = ApplicationManager.getApplication(Devices.res("diagnostics"));
            system.launchApp(info1);
        }
    }

    @Override
    public void closeApplication(AppInfo info) {
        Optional<Application> optional = APPLICATIONS.stream().filter(app -> app.getInfo() == info).findFirst();
        optional.ifPresent(this::closeApplication);
    }

    @SuppressWarnings("unchecked")
    private void closeApplication(Application app) {
        for (int i = 0; i < windows.size(); i++) {
            Window<Application> window = (Window<Application>) windows.get(i);
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

    private void addWindow(Window<Application> window) {
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
        return windows.size() >= 1024;
    }

    private boolean isMouseOnScreen(int mouseX, int mouseY) {
        int posX = (width - getScreenWidth()) / 2;
        int posY = (height - getScreenHeight()) / 2;
        return isMouseInside(mouseX, mouseY, posX, posY, posX + getScreenWidth(), posY + getScreenHeight());
    }

    private boolean isMouseWithinWindowBar(int mouseX, int mouseY, Window<?> window) {
        if (window == null) return false;
        int posX = (width - getScreenWidth()) / 2;
        int posY = (height - getScreenHeight()) / 2;
        return isMouseInside(mouseX, mouseY, posX + window.offsetX + 1, posY + window.offsetY + 1, posX + window.offsetX + window.width - 13, posY + window.offsetY + 11);
    }

    private boolean isMouseWithinWindow(int mouseX, int mouseY, Window<?> window) {
        if (window == null) return false;
        int posX = (width - getScreenWidth()) / 2;
        int posY = (height - getScreenHeight()) / 2;
        return isMouseInside(mouseX, mouseY, posX + window.offsetX, posY + window.offsetY, posX + window.offsetX + window.width, posY + window.offsetY + window.height);
    }

    public boolean isMouseWithinApp(int mouseX, int mouseY, Window<?> window) {
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
            this.currentWallpaper = new Wallpaper(currentWallpaper.location + 1);
        }
        wallpaperUpdated();
    }

    public void prevWallpaper() {
        if (currentWallpaper.location - 1 >= 0) {
            this.currentWallpaper = new Wallpaper(currentWallpaper.location - 1);
        }
        wallpaperUpdated();
    }

    private void wallpaperUpdated() {
        if (currentWallpaper.isBuiltIn()) {
            wallpaper.setImage(WALLPAPERS.get(currentWallpaper.location));
        } else {
            wallpaper.setImage(currentWallpaper.locationPath);
        }
    }

    public void setWallpaper(Path path) {
        currentWallpaper = new Wallpaper(path);
        wallpaperUpdated();
    }

    public void setWallpaper(int wall) {
        currentWallpaper = new Wallpaper(wall);
        wallpaperUpdated();
    }

    public Wallpaper getCurrentWallpaper() {
        return currentWallpaper;
    }

    public List<ResourceLocation> getWallapapers() {
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

    @Override
    public void openDialog(Dialog message) {
        this.systemDialog = message;
    }

    public boolean isApplicationInstalled(AppInfo info) {
        return info.isSystemApp() || installedApps.contains(info);
    }

    public boolean isApplicationNotInstalled(AppInfo info) {
        return !isApplicationInstalled(info);
    }

    private boolean isValidApplication(AppInfo info) {
        if (Devices.hasAllowedApplications()) {
            return Devices.getAllowedApplications().contains(info);
        }
        return true;
    }

    private boolean isInvalidApplication(AppInfo info) {
        return !isValidApplication(info);
    }

    public void installApplication(AppInfo info, @Nullable Callback<Object> callback) throws AccessDeniedException {
        if (!PermissionManager.hasPermission(Permission.SOFTWARE_MANAGEMENT))
            throw new AccessDeniedException("You do not have permission to install applications");

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

    public static void loadApplications(Consumer<List<Application>> loader) {
        if (loaded) throw new IllegalStateException("Applications are already loaded");
        loaded = true;
        loader.accept(APPLICATIONS);
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
        private final Path locationPath;
        private final int location;

        public Wallpaper(Path of) {
            this.locationPath = of;
            this.location = -87;
        }

        public Path getPath() {
            return locationPath;
        }

        public int getLocation() {
            return location;
        }

        private Wallpaper(CompoundTag tag) {
            var url = tag.getString("url");
            var location = tag.getInt("location");
            if (tag.contains("path", 8)) {
                this.locationPath = Path.of(tag.getString("path"));
                this.location = -87;
            } else {
                this.locationPath = Path.of(url);
                this.location = location;
            }
        }

        private Wallpaper(int location) {
            this.location = location;
            this.locationPath = null;
        }

        public boolean isBuiltIn() {
            return this.location != -87;
        }

        public Tag serialize() {
            var a = new CompoundTag();
            if (isBuiltIn()) {
                a.putInt("location", location);
            } else {
                a.putString("path", this.locationPath.toString());
            }
            return a;
        }
    }
}
