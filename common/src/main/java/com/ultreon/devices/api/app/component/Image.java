package com.ultreon.devices.api.app.component;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.devices.Devices;
import com.ultreon.devices.api.app.Component;
import com.ultreon.devices.api.app.IIcon;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.api.utils.OnlineRequest;
import com.ultreon.devices.api.utils.RenderUtil;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.object.AppInfo;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class Image extends Component {
    public static class AppImage extends Layout {
        private final AppInfo appInfo;
        private AppInfo.Icon.Glyph[] glyphs;
        private int componentWidth;
        private int componentHeight;
        public AppImage(int left, int top, AppInfo resource) {
            this(left, top, 14, 14, resource);
            this.glyphs = new AppInfo.Icon.Glyph[]{resource.getIcon().getBase(), resource.getIcon().getOverlay0(), resource.getIcon().getOverlay1()};
        }

        public AppImage(int left, int top, int componentWidth, int componentHeight, AppInfo resource) {
            super(left, top, componentWidth, componentHeight);
            this.appInfo = resource;
            this.glyphs = new AppInfo.Icon.Glyph[]{resource.getIcon().getBase(), resource.getIcon().getOverlay0(), resource.getIcon().getOverlay1()};
            this.componentWidth = componentWidth;
            this.componentHeight = componentHeight;
            //super(left, top, componentWidth, componentHeight, imageU, imageV, 14, 14, 224, 224, resource);
        }

        @Override
        public void init(Layout layout) {
            super.init(layout);
            if (appInfo.getIcon().getBase().getU() == -1 && appInfo.getIcon().getBase().getV() == -1) {
                var image = new Image(0, 0, componentWidth, componentHeight, 0, 0, 14, 14, 224, 224, Laptop.ICON_TEXTURES);
                this.addComponent(image);
                return;
            }
            for (AppInfo.Icon.Glyph glyph : glyphs) {
                if (glyph.getU() == -1 || glyph.getV() == -1) continue;
                var image = new Image(0, 0, componentWidth, componentHeight, glyph.getU(), glyph.getV(), 14, 14, 224, 224, Laptop.ICON_TEXTURES);
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

    private static class ColorSupplier {
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

    /**
     * Creates a new Image using a ResourceLocation. This automatically sets the width and height of
     * the component according to the width and height of the image.
     *
     * @param left        the amount of pixels to be offset from the left
     * @param top         the amount of pixels to be offset from the top
     * @param imageU      the u position on the image resource
     * @param imageV      the v position on the image resource
     * @param imageWidth  the image width
     * @param imageHeight the image height
     * @param resource    the resource location of the image
     */
    public Image(int left, int top, int imageU, int imageV, int imageWidth, int imageHeight, ResourceLocation resource) {
        this(left, top, imageWidth, imageHeight, imageU, imageV, imageWidth, imageHeight, resource);
    }

    /**
     * Creates a new Image using a ResourceLocation. This constructor allows the specification of
     * the width and height of the component instead of automatically unlike
     * {@link Image#Image(int, int, int, int, int, int, ResourceLocation)}
     *
     * @param left            the amount of pixels to be offset from the left
     * @param top             the amount of pixels to be offset from the top
     * @param componentWidth  the width of the component
     * @param componentHeight the height of the component
     * @param imageU          the u position on the image resource
     * @param imageV          the v position on the image resource
     * @param imageWidth      the image width
     * @param imageHeight     the image height
     * @param resource        the resource location of the image
     */
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

    /**
     * Creates a new Image from an url. This allows a resource to be downloaded from the internet
     * and be used as the Image. In the case that the resource could not be downloaded or the player
     * is playing the game in an offline state, the Image will default to a missing texture.
     * <p>
     * It should be noted that the remote resource is cached, so updating it may not result in an
     * instant change. Caching has a default limit of 10 resources but this can be changed by the
     * player in the configuration.
     *
     * @param left            the amount of pixels to be offset from the left
     * @param top             the amount of pixels to be offset from the top
     * @param componentWidth  the width of the component
     * @param componentHeight the height of the component
     * @param url             the url of the resource
     */
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
    public void render(GuiGraphics graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        if (this.visible) {
            if (loader != null && loader.setup) {
                image = loader.load(this);
                spinner.setVisible(false);
                loader.setup = false;
            }

            if (hasBorder) {
                graphics.fill(x, y, x + componentWidth, y + componentHeight, borderColor);
            }

            RenderSystem.setShaderColor(tint.get().r/255f, tint.get().g/255f, tint.get().b/255f, alpha);

            if (image != null && image.textureId != -1) {
                image.restore();

                RenderSystem.setShaderColor(tint.get().r/255f, tint.get().g/255f, tint.get().b/255f, alpha);
                RenderSystem.enableBlend();
                RenderSystem.setShaderTexture(0, image.textureId);

                if (/*hasBorder*/true) {
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
                    if (drawFull) {
                        //DebugLog.log("Rendering image");
                        RenderUtil.drawRectWithTexture(null, graphics, x, y, componentWidth, componentHeight, imageU, imageV, 256, 256);
//                        GuiComponent.blit(pose, x, y, componentWidth, componentHeight, imageU, imageV, 256, 256);
                    } else {
                        //DebugLog.log("Rendering image");
                        RenderUtil.drawRectWithTexture(null, graphics, x, y, componentWidth, componentHeight, imageU, imageV, imageWidth, imageHeight, sourceWidth, sourceHeight);
                        //GuiComponent.blit(pose, x, y, componentWidth, componentHeight, imageU, imageV, sourceWidth, sourceHeight, imageWidth, imageHeight);
                    }
                }
            } else {
                if (/*hasBorder*/true) {
                    graphics.fill(x + borderThickness, y + borderThickness, x + componentWidth - borderThickness, y + componentHeight - borderThickness, Color.LIGHT_GRAY.getRGB());
                } else {
                    graphics.fill(x, y, x + componentWidth, y + componentHeight, Color.LIGHT_GRAY.getRGB());
                }
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

    public void setImage(Laptop.Wallpaper wallpaper) {
        if (wallpaper.isBuiltIn()) {
            setImage(Laptop.getWallpapers().get(wallpaper.getLocation()));
        } else {
            setImage(wallpaper.getUrl());
        }
    }

    public void setImage(String url) {
        setLoader(new DynamicLoader(url));
        this.drawFull = true;
    }

    private void setLoader(ImageLoader loader) {
        this.loader = loader;
        if (initialized) {
            loader.setup(this);
            spinner.setVisible(true);
        }
    }

    /**
     * Sets the alpha for this image. Must be in the range
     * of 0f to 1f
     *
     * @param alpha how transparent you want it to be.
     */
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

    /**
     * Makes it so the border shows
     *
     * @param show should the border show
     */
    public void setBorderVisible(boolean show) {
        this.hasBorder = show;
        this.borderThickness = show ? _pBorderThickness : 0;
    }

    /**
     * Sets the border color for this component
     *
     * @param color the border color
     */
    private void setBorderColor(Color color) {
        this.borderColor = color.getRGB();
    }

    /**
     * Sets the thickness of the border
     *
     * @param thickness how thick in pixels
     */
    public void setBorderThickness(int thickness) {
        this._pBorderThickness = thickness;
        this.borderThickness = thickness;
    }

    public void setDrawFull(boolean drawFull) {
        this.drawFull = drawFull;
    }

    /**
     * Image Loader
     */
    private static abstract class ImageLoader {
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

    private static class DynamicLoader extends ImageLoader {
        private final String url;
        private AbstractTexture texture;

        public DynamicLoader(String url) {
            this.url = url;
        }

        @Override
        public void setup(final Image image) {
            if (CACHE.containsKey(url)) {
                setup = true;
                return;
            }
            Runnable r = () -> {
                try {
                    URL url = new URL(this.url);
                    OnlineRequest.checkURLForSuspicions(url);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                    conn.setRequestProperty("Accept", "image/png");
                    InputStream connIn = conn.getInputStream();
                    byte[] bytes = connIn.readAllBytes();
                    connIn.close();
                    conn.disconnect();

                    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                    ByteArrayInputStream imageIn = new ByteArrayInputStream(bytes);
                    BufferedImage img = ImageIO.read(imageIn);

                    NativeImage nativeImage = NativeImage.read(in);

                    Laptop.runLater(() -> {
                        Devices.LOGGER.debug("Loaded image: " + url);
                        texture = new DynamicTexture(nativeImage);
                        setup = true;
                    });
                } catch (IOException e) {
                    texture = MissingTextureAtlasSprite.getTexture();
                    setup = true;
                    e.printStackTrace();
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
            NativeImage nativeImage = NativeImage.read(in);
            Minecraft.getInstance().getTextureManager().register(Devices.id("dynamic_loaded/" + getId()), this);
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
