package com.ultreon.devices.core;

import com.mojang.blaze3d.platform.NativeImage;
import com.ultreon.devices.Reference;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.AbstractTexture;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.*;

import java.io.IOException;
import java.util.WeakHashMap;

@SuppressWarnings("unused")
public class Gpu {
    private static final WeakHashMap<GuiGraphics, Gpu> instances = new WeakHashMap<>();
    public static final int ERRNO_EIO = 1;
    public static final int ERRNO_TEXTURE_LIMIT = 2;
    public static final int ERRNO_NOT_FOUND = 3;
    public static final int ERRNO_ILLEGAL_ARGUMENT = 4;

    private final GuiGraphics graphics;
    public final Class<GL11> gl11 = GL11.class;
    public final Class<GL12> gl12 = GL12.class;
    public final Class<GL13> gl13 = GL13.class;
    public final Class<GL14> gl14 = GL14.class;
    public final Class<GL15> gl15 = GL15.class;
    public final Class<GL20> gl20 = GL20.class;
    public final Class<GL30> gl30 = GL30.class;
    public final Class<GL31> gl31 = GL31.class;
    public final Class<GL32> gl32 = GL32.class;
    public final Class<GL33> gl33 = GL33.class;
    public final Class<GL40> gl40 = GL40.class;
    public final Class<GL41> gl41 = GL41.class; // macOS only supports up to OpenGL 4.1 :(
    public String error;
    public int errno;
    private final Int2ObjectMap<GpuTexture> textures = new Int2ObjectArrayMap<>();

    public Gpu(@NotNull GuiGraphics graphics) {
        this.graphics = graphics;

        Reference.CLEANER.register(graphics, () -> {
            for (AbstractTexture texture : this.textures.values()) {
                texture.releaseId();
            }
        });
    }

    public int getErrno() {
        return this.errno;
    }

    public String getError() {
        return this.error;
    }

    public int createTexture(byte[] pixelData) {
        if (this.textures.size() >= 64) {
            this.errno = ERRNO_TEXTURE_LIMIT;
            this.error = "Texture limit reached";
            return -1;
        }

        try {
            NativeImage image = NativeImage.read(pixelData);

            GpuTexture dynamicTexture = new GpuTexture(image);
            this.textures.put(dynamicTexture.getId(), dynamicTexture);
        } catch (IOException e) {
            this.errno = ERRNO_EIO;
            this.error = "Failed to read pixel data";
            return -1;
        }

        return -1;
    }

    public void deleteTexture(int id) {
        GpuTexture remove = this.textures.remove(id);
        remove.releaseId();
        remove.close();
    }

    public void bindTexture(int id) {
        GpuTexture texture = this.textures.get(id);
        if (texture == null) {
            this.errno = ERRNO_EIO;
            this.error = "Texture does not exist";
            return;
        }

        texture.bind();
    }

    public void drawTexture(int id, int x, int y, int width, int height) {
        GpuTexture texture = this.textures.get(id);
        if (texture == null) {
            this.errno = ERRNO_NOT_FOUND;
            this.error = "Texture does not exist";
            return;
        }

        graphics.blit(texture.getTextureLocation(), x, y, 0, 0, width, height);
    }

    public void fill(int x, int y, int width, int height, int color) {
        graphics.fill(x, y, x + width, y + height, color);
    }

    public void rect(int x, int y, int width, int height, int color) {
        graphics.renderOutline(x, y, x + width, y + height, color);
    }

    public static Gpu of(@NotNull GuiGraphics graphics) {
        return instances.computeIfAbsent(graphics, Gpu::new);
    }
}
