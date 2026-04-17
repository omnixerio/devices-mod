package dev.ultreon.devices;

import com.mojang.blaze3d.platform.NativeImage;
import dev.ultreon.devices.api.ApplicationManager;
import dev.ultreon.devices.block.entity.renderer.*;
import dev.ultreon.devices.client.entity.renderer.SeatEntityRenderer;
import dev.ultreon.devices.core.Laptop;
import dev.ultreon.devices.debug.DebugFlags;
import dev.ultreon.devices.debug.DebugUtils;
import dev.ultreon.devices.debug.DumpType;
import dev.ultreon.devices.init.DeviceBlockEntities;
import dev.ultreon.devices.init.DeviceEntities;
import dev.ultreon.devices.object.AppInfo;
import dev.ultreon.devices.programs.system.object.ColorSchemePresets;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;


public class ClientModEvents {
    private static final Marker SETUP = MarkerFactory.getMarker("SETUP");
    private static final Logger LOGGER = OmnixerioDevicesCommon.LOGGER;

    public static void clientSetup() {
        LOGGER.info("Doing some client setup.");

        if (OmnixerioDevicesCommon.DEVELOPER_MODE) {
            LOGGER.info(SETUP, "Adding developer wallpaper.");
            Laptop.addWallpaper(OmnixerioDevicesCommon.id("developer_wallpaper"));
        } else {
            LOGGER.info(SETUP, "Adding default wallpapers.");
            Laptop.addWallpaper(OmnixerioDevicesCommon.id("laptop_wallpaper_1"));
            Laptop.addWallpaper(OmnixerioDevicesCommon.id("laptop_wallpaper_2"));
            Laptop.addWallpaper(OmnixerioDevicesCommon.id("laptop_wallpaper_3"));
            Laptop.addWallpaper(OmnixerioDevicesCommon.id("laptop_wallpaper_4"));
            Laptop.addWallpaper(OmnixerioDevicesCommon.id("laptop_wallpaper_5"));
            Laptop.addWallpaper(OmnixerioDevicesCommon.id("laptop_wallpaper_6"));
            Laptop.addWallpaper(OmnixerioDevicesCommon.id("laptop_wallpaper_7"));
            Laptop.addWallpaper(OmnixerioDevicesCommon.id("laptop_wallpaper_8"));
            Laptop.addWallpaper(OmnixerioDevicesCommon.id("laptop_wallpaper_9"));
            Laptop.addWallpaper(OmnixerioDevicesCommon.id("laptop_wallpaper_10"));
        }


        // Register other stuff.
        registerRenderLayers();
        registerLayerDefinitions();
        registerRenderers();
        registerOSContent();

        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(OmnixerioDevicesCommon.id("reloader"), new ReloaderListener());
    }

    private static void registerOSContent() {
        ColorSchemePresets.init();
    }

    @ApiStatus.Internal
    public static class ReloaderListener implements PreparableReloadListener {
        @Override
        public @NonNull CompletableFuture<Void> reload(@NonNull SharedState currentReload, @NonNull Executor taskExecutor, PreparationBarrier preparationBarrier, @NonNull Executor reloadExecutor) {
            LOGGER.debug("Reloading resources from the Device Mod.");

            return CompletableFuture.runAsync(() -> {
                if (!ApplicationManager.getAllApplications().isEmpty()) {
                    ApplicationManager.getAllApplications().forEach(AppInfo::reload);
                    generateIconAtlas(currentReload.resourceManager()); // FIXME: Broken resource reloading, can't find image resource while definitely exists.
                }

            }, reloadExecutor).thenCompose(preparationBarrier::wait);
        }
    }

    private static void registerRenderLayers() {
//        DeviceBlocks.getAllLaptops().forEach(block -> {
//            LOGGER.debug(SETUP, "Setting render layer for laptop {}", RegistrarManager.getId(block, Registries.BLOCK));
//            RenderTypeRegistry.register(RenderType.cutout(), block);
//        });
//
//        DeviceBlocks.getAllPrinters().forEach(block -> {
//            LOGGER.debug(SETUP, "Setting render layer for printer {}", RegistrarManager.getId(block, Registries.BLOCK));
//            RenderTypeRegistry.register(RenderType.cutout(), block);
//        });
//
//        DeviceBlocks.getAllRouters().forEach(block -> {
//            LOGGER.debug(SETUP, "Setting render layer for router {}", RegistrarManager.getId(block, Registries.BLOCK));
//            RenderTypeRegistry.register(RenderType.cutout(), block);
//        });
//
//        LOGGER.debug(SETUP, "Setting render layer for paper {}", RegistrarManager.getId(DeviceBlocks.PAPER.get(), Registries.BLOCK));
//        RenderTypeRegistry.register(RenderType.cutout(), DeviceBlocks.PAPER.get());
    }

    public static void generateIconAtlas() {
        generateIconAtlas(Minecraft.getInstance().getResourceManager());
    }

    public static void generateIconAtlas(ResourceManager resourceManager) {
        final int ICON_SIZE = 14;
        var imageWriter = new Object() {
            final BufferedImage atlas = new BufferedImage(ICON_SIZE * 16, ICON_SIZE * 16, BufferedImage.TYPE_INT_ARGB);
            final Graphics g = atlas.createGraphics();
            int index = 0;
            int mode = 0;
            ResourceManager rm = resourceManager;

            public void writeImage(AppInfo info, Identifier location) {
                String path = "/assets/" + location.getNamespace() + "/" + location.getPath();
                try {
                    if (rm == null) {
                        rm = Minecraft.getInstance().getResourceManager();
                    }
                    InputStream input = getClass().getClassLoader().getResourceAsStream(path);
                    if (input == null) {
                        input = getClass().getResourceAsStream(path);
                        if (input == null) {
                            Resource resource = rm.getResource(location).orElse(null);
                            if (resource == null)
                                throw new FileNotFoundException("Resource for " + location + " wasn't found");
                            input = resource.open();
                        }
                    }
                    BufferedImage icon = ImageIO.read(input);
                    if (icon.getWidth() != ICON_SIZE || icon.getHeight() != ICON_SIZE) {
                        OmnixerioDevicesCommon.LOGGER.error("Incorrect icon size for " + (info == null ? null : info.getId()) + " (Must be 14 by 14 pixels)");
                        return;
                    }
                    int iconU = index % 16 * ICON_SIZE;
                    int iconV = index / 16 * ICON_SIZE;
                    g.drawImage(icon, iconU, iconV, ICON_SIZE, ICON_SIZE, null);
                    if (info != null) {
                        AppInfo.Icon.Glyph glyph = switch (mode) {
                            case 0 -> info.getIcon().getBase();
                            case 1 -> info.getIcon().getOverlay0();
                            case 2 -> info.getIcon().getOverlay1();
                            default -> throw new IllegalStateException("Unexpected value: " + mode);
                        };
                        glyph.setU(iconU);
                        glyph.setV(iconV);
                    }
                    index++;
                    if (DebugFlags.LOG_APP_ICON_STITCHES) {
                        OmnixerioDevicesCommon.LOGGER.info("Stitching texture: " + location);
                    }
                } catch (FileNotFoundException e) {
                    OmnixerioDevicesCommon.LOGGER.error("Unable to load icon for '" + (info == null ? null : info.getId()) + "': " + e.getMessage());
                    if (DebugFlags.PRINT_MISSING_APP_ICONS_STACK_TRACES) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    OmnixerioDevicesCommon.LOGGER.error("Unable to load icon for " + (info == null ? null : info.getId()));
                    if (DebugFlags.PRINT_APP_ICONS_STACK_TRACES) {
                        e.printStackTrace();
                    }
                }
            }

            public void finish() {
                g.dispose();

                if (DebugFlags.DUMP_APP_ICON_ATLAS) {
                    try {
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                ByteArrayOutputStream output = new ByteArrayOutputStream();
                try {
                    ImageIO.write(atlas, "png", output);
                    byte[] bytes = output.toByteArray();
                    ByteArrayInputStream input = new ByteArrayInputStream(bytes);
                    Minecraft.getInstance().execute(() -> {
//                        try {
//                            Minecraft.getInstance().getTextureManager().register(Laptop.ICON_TEXTURES, new DynamicTexture(() -> "devices_mod_" + UUID.randomUUID().toString().replace("_", ""), NativeImage.read(input)));
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        imageWriter.writeImage(null, Identifier.fromNamespaceAndPath("devices", "textures/app/icon/base/missing.png"));


        for (AppInfo info : ApplicationManager.getAllApplications()) {
            if (info.getIcon() == null) continue;

            //Identifier identifier = info.getId();
            //Identifier iconResource = new Identifier(info.getIcon());
            imageWriter.mode = 0;
            imageWriter.writeImage(info, info.getIcon().getBase().getIdentifier());
            imageWriter.mode = 1;
            imageWriter.writeImage(info, info.getIcon().getOverlay0().getIdentifier());
            imageWriter.mode = 2;
            imageWriter.writeImage(info, info.getIcon().getOverlay1().getIdentifier());
        }
        imageWriter.mode = 0;
        imageWriter.finish();
    }

    public static void registerRenderers() {
        LOGGER.info("Registering renderers.");

        EntityRenderers.register(DeviceEntities.SEAT, SeatEntityRenderer::new);

        BlockEntityRenderers.register(DeviceBlockEntities.LAPTOP, LaptopRenderer::new);
        BlockEntityRenderers.register(DeviceBlockEntities.PRINTER, PrinterRenderer::new);
        BlockEntityRenderers.register(DeviceBlockEntities.PAPER, PaperRenderer::new);
        BlockEntityRenderers.register(DeviceBlockEntities.ROUTER, RouterRenderer::new);
        BlockEntityRenderers.register(DeviceBlockEntities.SEAT, OfficeChairRenderer::new);
    }

    public static void registerLayerDefinitions() {
        LOGGER.info("Registering layer definitions.");
    }
}
