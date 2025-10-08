package dev.ultreon.devices.api.app.component;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.ultreon.devices.UltreonDevices;
import dev.ultreon.devices.api.app.Component;
import dev.ultreon.devices.api.app.Dialog;
import dev.ultreon.devices.api.app.IIcon;
import dev.ultreon.devices.api.app.Layout;
import dev.ultreon.devices.api.utils.OnlineRequest;
import dev.ultreon.devices.api.utils.RenderUtil;
import dev.ultreon.devices.core.ComputerScreen;
import dev.ultreon.devices.core.io.Path;
import dev.ultreon.devices.object.AppInfo;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.mojang.blaze3d.platform.NativeImage.Format.*;

@SuppressWarnings("unused")
public class Image extends Component {
    public static class AppImage extends Layout {
        private final AppInfo appInfo;
        private AppInfo.Icon.Glyph[] glyphs;
        private final int componentWidth;
        private final int componentHeight;

        public AppImage(int left, int top, AppInfo resource) {
            this(left, top, 14, 14, resource);
            this.glyphs = new AppInfo.Icon.Glyph[]{resource.getIcon().getBase(), resource.getIcon().getOverlay0(), resource.getIcon().getOverlay1()};
        }

        public AppImage(int left, int top, int componentWidth, int componentHeight, AppInfo resource) {
            super(left, top, componentWidth, componentHeight);
            this.appInfo = resource;
            AppInfo.Icon icon = resource.getIcon();
            if (icon != null) {
                this.glyphs = new AppInfo.Icon.Glyph[]{icon.getBase(), icon.getOverlay0(), icon.getOverlay1()};
            }
            this.componentWidth = componentWidth;
            this.componentHeight = componentHeight;
            //super(left, top, componentWidth, componentHeight, imageU, imageV, 14, 14, 224, 224, resource);
        }

        @Override
        public void init(Layout layout) {
            super.init(layout);
            AppInfo.Icon icon = appInfo.getIcon();
            if (icon == null || icon.getBase().getU() == -1 && icon.getBase().getV() == -1) {
                var image = new Image(0, 0, componentWidth, componentHeight, 0, 0, 14, 14, 224, 224, ComputerScreen.ICON_TEXTURES);
                this.addComponent(image);
                return;
            }
            for (AppInfo.Icon.Glyph glyph : glyphs) {
                if (glyph.getU() == -1 || glyph.getV() == -1) continue;
                var image = new Image(0, 0, componentWidth, componentHeight, glyph.getU(), glyph.getV(), 14, 14, 224, 224, ComputerScreen.ICON_TEXTURES);
                Supplier<ColorSupplier> suscs = () -> {
                    int tint = appInfo.getTint(glyph.getType());
                    var col = new Color(tint);
                    var cs = new ColorSupplier();
                    cs.r = col.getRed();
                    cs.g = col.getGreen();
                    cs.b = col.getBlue();
                    return cs;
                };
                image.setTint(suscs);
                this.addComponent(image);
                //image.init(layout);
            }
        }
    }


    public static final Map<String, CachedImage> CACHE = new HashMap<>();
    protected ImageLoader loader;
    protected CachedImage image;
    protected boolean initialized = false;
    protected boolean drawFull = false;
    protected int imageU, imageV;
    protected int imageWidth, imageHeight;
    protected int sourceWidth, sourceHeight;
    public int componentWidth;
    public int componentHeight;
    private Spinner spinner;
    private float alpha = 1f;
    private Supplier<ColorSupplier> tint = () -> Util.make(new ColorSupplier(), cs -> {
        cs.r = 255;
        cs.g = 255;
        cs.b = 255;
    });

    public void setTint(int r, int g, int b) {
        var cs = new ColorSupplier();
        cs.r = r;
        cs.g = g;
        cs.b = b;
        this.setTint(() -> cs);
    }

    public static class ColorSupplier {
        int r;
        int g;
        int b;
    }

    public void setTint(Supplier<ColorSupplier> colorSupplier) {
        this.tint = colorSupplier;
    }

    private boolean hasBorder = false;
    private int borderColor = Color.BLACK.getRGB();
    private int borderThickness = 0;

    public Image(int left, int top, int width, int height) {
        super(left, top);
        this.componentWidth = width;
        this.componentHeight = height;
    }

    /// Creates a new Image using a ResourceLocation. This automatically sets the width and height of
    /// the component according to the width and height of the image.
    ///
    /// @param left        the amount of pixels to be offset from the left
    /// @param top         the amount of pixels to be offset from the top
    /// @param imageU      the u position on the image resource
    /// @param imageV      the v position on the image resource
    /// @param imageWidth  the image width
    /// @param imageHeight the image height
    /// @param resource    the resource location of the image
    public Image(int left, int top, int imageU, int imageV, int imageWidth, int imageHeight, ResourceLocation resource) {
        this(left, top, imageWidth, imageHeight, imageU, imageV, imageWidth, imageHeight, resource);
    }

    /// Creates a new Image using a ResourceLocation. This constructor allows the specification of
    /// the width and height of the component instead of automatically unlike
    /// [#Image(int,int,int,int,int,int,ResourceLocation)]
    ///
    /// @param left            the amount of pixels to be offset from the left
    /// @param top             the amount of pixels to be offset from the top
    /// @param componentWidth  the width of the component
    /// @param componentHeight the height of the component
    /// @param imageU          the u position on the image resource
    /// @param imageV          the v position on the image resource
    /// @param imageWidth      the image width
    /// @param imageHeight     the image height
    /// @param resource        the resource location of the image
    public Image(int left, int top, int componentWidth, int componentHeight, int imageU, int imageV, int imageWidth, int imageHeight, ResourceLocation resource) {
        this(left, top, componentWidth, componentHeight, imageU, imageV, imageWidth, imageHeight, 256, 256, resource);
    }

    public Image(int left, int top, int componentWidth, int componentHeight, int imageU, int imageV, int imageWidth, int imageHeight, int sourceWidth, int sourceHeight, ResourceLocation resource) {
        super(left, top);
        this.loader = new StandardLoader(resource);
        this.componentWidth = componentWidth;
        this.componentHeight = componentHeight;
        this.imageU = imageU;
        this.imageV = imageV;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.sourceWidth = sourceWidth;
        this.sourceHeight = sourceHeight;
    }

    /// Creates a new Image from an url. This allows a resource to be downloaded from the internet
    /// and be used as the Image. In the case that the resource could not be downloaded or the player
    /// is playing the game in an offline state, the Image will default to a missing texture.
    ///
    /// It should be noted that the remote resource is cached, so updating it may not result in an
    /// instant change. Caching has a default limit of 10 resources but this can be changed by the
    /// player in the configuration.
    ///
    /// @param left            the amount of pixels to be offset from the left
    /// @param top             the amount of pixels to be offset from the top
    /// @param componentWidth  the width of the component
    /// @param componentHeight the height of the component
    /// @param url             the url of the resource
    public Image(int left, int top, int componentWidth, int componentHeight, String url) {
        super(left, top);
        this.loader = new DynamicLoader(url);
        this.componentWidth = componentWidth;
        this.componentHeight = componentHeight;
        this.drawFull = true;
    }

    public Image(int left, int top, IIcon icon) {
        super(left, top);
        this.loader = new StandardLoader(icon.getIconAsset());
        this.componentWidth = icon.getIconSize();
        this.componentHeight = icon.getIconSize();
        this.imageU = icon.getU();
        this.imageV = icon.getV();
        this.imageWidth = icon.getIconSize();
        this.imageHeight = icon.getIconSize();
        this.sourceWidth = icon.getGridWidth() * icon.getIconSize();
        this.sourceHeight = icon.getGridHeight() * icon.getIconSize();
    }

    public Image(int left, int top, int componentWidth, int componentHeight, IIcon icon) {
        super(left, top);
        this.loader = new StandardLoader(icon.getIconAsset());
        this.componentWidth = componentWidth;
        this.componentHeight = componentHeight;
        this.imageU = icon.getU();
        this.imageV = icon.getV();
        this.imageWidth = icon.getIconSize();
        this.imageHeight = icon.getIconSize();
        this.sourceWidth = icon.getGridWidth() * icon.getIconSize();
        this.sourceHeight = icon.getGridHeight() * icon.getIconSize();
    }

    @Override
    public void init(Layout layout) {
        if (layout != null) {
            spinner = new Spinner(left + (componentWidth / 2) - 6, top + (componentHeight / 2) - 6);
            layout.addComponent(spinner);
        }
        initialized = true;
    }

    @Override
    public void handleLoad() {
        this.reload();
    }

    @Override
    protected void handleUnload() {
        this.initialized = false;
    }

    @Override
    public void render(GuiGraphics graphics, ComputerScreen computerScreen, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        if (this.visible) {
            if (loader != null && loader.setup) {
                image = loader.load(this);
                spinner.setVisible(false);
                loader.setup = false;
            }

            if (hasBorder) {
                graphics.fill(x, y, x + componentWidth, y + componentHeight, borderColor);
            }

            RenderSystem.setShaderColor(tint.get().r / 255f, tint.get().g / 255f, tint.get().b / 255f, alpha);

            if (image != null && image.textureId != -1) {
                image.restore();

                RenderSystem.setShaderColor(tint.get().r / 255f, tint.get().g / 255f, tint.get().b / 255f, alpha);
                RenderSystem.enableBlend();
                RenderSystem.setShaderTexture(0, image.textureId);

                if (drawFull) {
                    //DebugLog.log("Rendering image");
                    RenderUtil.drawRectWithTexture(null, graphics, x + borderThickness, y + borderThickness, 0, imageU, imageV, componentWidth - borderThickness * 2, componentHeight - borderThickness * 2, 256, 256);
                    //GuiComponent.blit(pose, x + borderThickness, y + borderThickness, imageU, imageV, componentWidth - borderThickness * 2, componentHeight - borderThickness * 2, 256, 256);
                } else {
                    //DebugLog.log("Rendering image");
                    RenderUtil.drawRectWithTexture(null, graphics, x + borderThickness, y + borderThickness, imageU, imageV, componentWidth - borderThickness * 2, componentHeight - borderThickness * 2, imageWidth, imageHeight, sourceWidth, sourceHeight);
                    //GuiComponent.blit(pose, x + borderThickness, y + borderThickness, componentWidth - borderThickness * 2, imageU, imageV, componentHeight - borderThickness * 2, sourceWidth, sourceHeight, imageWidth, imageHeight);
                }
            } else {
                graphics.fill(x + borderThickness, y + borderThickness, x + componentWidth - borderThickness, y + componentHeight - borderThickness, Color.LIGHT_GRAY.getRGB());
            }
            RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
        }
    }

    public void reload() {
        if (loader != null) {
            loader.setup(this);
        }
    }

    public void setImage(ResourceLocation resource) {
        setLoader(new StandardLoader(resource));
        this.drawFull = true;
    }

    public void setImage(ComputerScreen.Wallpaper wallpaper) {
        if (wallpaper.isBuiltIn()) {
            setImage(ComputerScreen.getWallpapers().get(wallpaper.getLocation()));
        } else {
            setImage(wallpaper.getPath());
        }
    }

    public void setImage(String url) {
        setLoader(new DynamicLoader(url));
        this.drawFull = true;
    }

    public void setImage(Path path) {
        setLoader(new DynamicLoader(path));
        this.drawFull = true;
    }

    private void setLoader(ImageLoader loader) {
        this.loader = loader;
        if (initialized) {
            loader.setup(this);
            spinner.setVisible(true);
        }
    }

    /// Sets the alpha for this image. Must be in the range
    /// of 0f to 1f
    ///
    /// @param alpha how transparent you want it to be.
    public void setAlpha(float alpha) {
        if (alpha < 0f) {
            this.alpha = 0f;
            return;
        }
        if (alpha > 1f) {
            this.alpha = 1f;
            return;
        }
        this.alpha = alpha;
    }

    private int _pBorderThickness = 1;

    /// Makes it so the border shows
    ///
    /// @param show should the border show
    public void setBorderVisible(boolean show) {
        this.hasBorder = show;
        this.borderThickness = show ? _pBorderThickness : 0;
    }

    /// Sets the border color for this component
    ///
    /// @param color the border color
    private void setBorderColor(Color color) {
        this.borderColor = color.getRGB();
    }

    /// Sets the thickness of the border
    ///
    /// @param thickness how thick in pixels
    public void setBorderThickness(int thickness) {
        this._pBorderThickness = thickness;
        this.borderThickness = thickness;
    }

    public void setDrawFull(boolean drawFull) {
        this.drawFull = drawFull;
    }

    /// Image Loader
    protected static abstract class ImageLoader {
        protected boolean setup = false;

        public final boolean isSetup() {
            return setup;
        }

        protected void setup(Image image) {
            setup = false;
        }

        public abstract CachedImage load(Image image);
    }

    private static class StandardLoader extends ImageLoader {
        private final AbstractTexture texture;
        private final ResourceLocation resource;

        public StandardLoader(ResourceLocation resource) {
            this.texture = new SimpleTexture(resource);
            this.resource = resource;
        }

        @Override
        protected void setup(Image image) {
            setup = true;
        }

        @Override
        @SuppressWarnings("ConstantConditions")
        public CachedImage load(Image image) {
            @Nullable AbstractTexture textureObj = Minecraft.getInstance().getTextureManager().getTexture(resource, null);
            if (textureObj != null) {
                return new CachedImage(textureObj.getId(), 0, 0, false);
            } else {
                AbstractTexture texture = new SimpleTexture(resource);
                Minecraft.getInstance().getTextureManager().register(resource, texture);
                return new CachedImage(texture.getId(), 0, 0, false);
            }
        }

        public AbstractTexture getTexture() {
            return texture;
        }
    }

    public static NativeImage read(InputStream textureStream) throws IOException {
        return read(NativeImage.Format.RGBA, textureStream);
    }

    public static NativeImage read(@Nullable NativeImage.Format format, InputStream textureStream) throws IOException {
        ByteBuffer loadBuffer = null;

        NativeImage loaded;
        try {
            loadBuffer = TextureUtil.readResource(textureStream);
            loadBuffer.rewind();
            loaded = read(format, loadBuffer);
        } finally {
            MemoryUtil.memFree(loadBuffer);
            IOUtils.closeQuietly(textureStream);
        }

        return loaded;
    }

    public static NativeImage read(@Nullable NativeImage.Format format, ByteBuffer textureData) throws IOException {
        if (format != null && !format.supportedByStb()) {
            throw new UnsupportedOperationException("Don't know how to read format " + format);
        } else if (MemoryUtil.memAddress(textureData) == 0L) {
            throw new IllegalArgumentException("Invalid buffer");
        } else {
            NativeImage nativeImage;
            try (MemoryStack memoryStack = MemoryStack.stackPush()) {
                IntBuffer xBuf = memoryStack.mallocInt(1);
                IntBuffer yBuf = memoryStack.mallocInt(1);
                IntBuffer channelBuf = memoryStack.mallocInt(1);
                ByteBuffer imageBuf = STBImage.stbi_load_from_memory(textureData, xBuf, yBuf, channelBuf, format == null ? 0 : format.components());
                if (imageBuf == null) {
                    throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
                }

                return new NativeImage(
                        format == null ? getStbFormat(channelBuf.get(0)) : format, xBuf.get(0), yBuf.get(0), true, MemoryUtil.memAddress(imageBuf)
                );
            }
        }
    }

    static NativeImage.Format getStbFormat(int channels) {
        return switch (channels) {
            case 1 -> LUMINANCE;
            case 2 -> LUMINANCE_ALPHA;
            case 3 -> RGB;
            default -> RGBA;
        };
    }

    private static class DynamicLoader extends ImageLoader {
        private String url;
        private Path path;
        private AbstractTexture texture;

        public DynamicLoader(String url) {
            this.url = url;
        }

        public DynamicLoader(Path path) {
            this.path = path;
        }

        @Override
        public void setup(final Image image) {
            if (CACHE.containsKey(url)) {
                setup = true;
                return;
            }
            Runnable r = () -> {
                if (path != null) {
                    ComputerScreen.getOrLoadMainDrive(((drive, success) -> {
                        if (!success) {
                            texture = MissingTextureAtlasSprite.getTexture();
                            setup = true;
                            return;
                        }

                        if (drive != null) {
                            drive.read(path, response -> {
                                if (!response.success()) {
                                    Dialog.Message dialog = new Dialog.Message("Failed to load image: " + response.message());
                                    ComputerScreen.getInstance().setSystemDialog(dialog);
                                    texture = MissingTextureAtlasSprite.getTexture();
                                    setup = true;
                                    return;
                                }

                                ByteArrayInputStream in = new ByteArrayInputStream(response.data());

                                try (NativeImage nativeImage = Image.read(in)) {
                                    ComputerScreen.runLater(() -> {
                                        UltreonDevices.LOGGER.debug("Loaded image: {}", path);
                                        texture = new DynamicTexture(nativeImage);
                                        setup = true;
                                    });
                                } catch (IOException e) {
                                    texture = MissingTextureAtlasSprite.getTexture();
                                    setup = true;
                                    UltreonDevices.LOGGER.error("Failed to load image: {}", path, e);
                                }
                            });
                        }
                    }));
                    return;
                }
                try {
                    URI url = new URI(this.url);
                    OnlineRequest.checkURLForSuspicions(url);
                    HttpURLConnection conn = (HttpURLConnection) url.toURL().openConnection();
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                    conn.setRequestProperty("Accept", "image/png");
                    InputStream connIn = conn.getInputStream();
                    byte[] bytes = connIn.readAllBytes();
                    connIn.close();
                    conn.disconnect();

                    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                    ByteArrayInputStream imageIn = new ByteArrayInputStream(bytes);

                    NativeImage nativeImage = Image.read(in);

                    ComputerScreen.runLater(() -> {
                        UltreonDevices.LOGGER.debug("Loaded image: {}", url);
                        texture = new DynamicTexture(nativeImage);
                        setup = true;
                    });
                } catch (IOException | URISyntaxException e) {
                    texture = MissingTextureAtlasSprite.getTexture();
                    setup = true;
                    UltreonDevices.LOGGER.error("Failed to load image: {}", url, e);
                }
            };
            Thread thread = new Thread(r, "Image Loader");
            thread.start();
        }

        @Override
        public CachedImage load(Image image) {
            if (CACHE.containsKey(url)) {
                CachedImage cachedImage = CACHE.get(url);
                image.imageWidth = cachedImage.width;
                image.imageHeight = cachedImage.height;
                return cachedImage;
            }

            try {
                texture.load(Minecraft.getInstance().getResourceManager());
                CachedImage cachedImage = new CachedImage(texture.getId(), image.imageWidth, image.imageHeight, true);
                if (texture != MissingTextureAtlasSprite.getTexture())
                    CACHE.put(url, cachedImage);
                return cachedImage;
            } catch (IOException e) {
                return new CachedImage(MissingTextureAtlasSprite.getTexture().getId(), 0, 0, true);
            }
        }
    }

    private static class DynamicLoadedTexture extends AbstractTexture {
        private final InputStream in;
        private final BufferedImage image;

        private DynamicLoadedTexture(InputStream in, BufferedImage image) {
            this.in = in;

            this.image = image;
            TextureUtil.prepareImage(getId(), this.image.getWidth(), this.image.getHeight());
        }

        @Override
        public void load(@NotNull ResourceManager resourceManager) throws IOException {
            NativeImage nativeImage = Image.read(in);
            Minecraft.getInstance().getTextureManager().register(UltreonDevices.res("dynamic_loaded/" + getId()), this);
            this.upload(nativeImage);
        }

        private void upload(NativeImage nativeImage) {
            nativeImage.upload(0, 0, 0, mipmap);
        }

        public BufferedImage getImage() {
            return image;
        }
    }

    private static class ImageCache extends LinkedHashMap<String, CachedImage> {
        private final int CAPACITY;

        private ImageCache(final int capacity) {
            super(capacity, 1f, true);
            this.CAPACITY = capacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, CachedImage> eldest) {
            if (size() > CAPACITY) {
                eldest.getValue().delete = true;
                return true;
            }
            return false;
        }
    }

    public static class CachedImage {
        private final int textureId;
        private final int width;
        private final int height;
        private final boolean dynamic;
        private boolean delete = false;

        private CachedImage(int textureId, int width, int height, boolean dynamic) {
            this.textureId = textureId;
            this.width = width;
            this.height = height;
            this.dynamic = dynamic;
        }

        public int getTextureId() {
            return textureId;
        }

        public void restore() {
            delete = false;
        }

        public void delete() {
            delete = true;
        }

        public boolean isDynamic() {
            return dynamic;
        }

        public boolean isPendingDeletion() {
            return delete;
        }
    }
}
