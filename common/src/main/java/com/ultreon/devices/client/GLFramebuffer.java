package com.ultreon.devices.client;

import com.ultreon.devices.Devices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLCapabilities;

import java.io.IOException;

public interface GLFramebuffer {
    int getId();
    void begin();
    void end();
    void dispose();

    FramebufferTexture getTexture();

    default ResourceLocation getTextureLocation() {
        return getTexture().getTextureLocation();
    }

    default FramebufferTexture createTexture(int width, int height) {
        int texture = GL20.glGenTextures();
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, texture);
        GL20.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL20.GL_RGB, width, height, 0, GL20.GL_RGB, GL20.GL_UNSIGNED_BYTE, 0);
        GL20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_LINEAR);
        GL20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_LINEAR);
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, 0);

        FramebufferTexture abstractTexture = new FramebufferTexture(this, texture);

        Minecraft.getInstance().getTextureManager().register(abstractTexture.getTextureLocation(), abstractTexture);
        return abstractTexture;
    }


    static GLFramebuffer create(int width, int height) {
        GLCapabilities caps = GL.getCapabilities();
        if (!caps.OpenGL20) {
            throw new IllegalStateException("OpenGL 2.0 is required");
        }

        if (caps.GL_ARB_framebuffer_object) {
            return new ARBFramebufferImpl(width, height);
        }

        if (caps.GL_EXT_framebuffer_object) {
            return new EXTFramebufferImpl(width, height);
        }

        if (caps.OpenGL30) {
            return new OpenGLFramebufferImpl(width, height);
        }

        throw new IllegalStateException("ARB extension, EXT extension or OpenGL 4.3 is required");
    }

    class FramebufferTexture extends AbstractTexture {
        private final int texture;
        private ResourceLocation location;
        private final GLFramebuffer framebuffer;

        public FramebufferTexture(GLFramebuffer framebuffer, int texture) {
            this.framebuffer = framebuffer;
            this.texture = texture;
        }

        @Override
        public void load(@NotNull ResourceManager resourceManager) throws IOException {
            // do nothing
        }

        @Override
        public int getId() {
            return texture;
        }

        @Override
        public void releaseId() {
            framebuffer.dispose();
        }

        public ResourceLocation getTextureLocation() {
            return location == null ? location : (location = Devices.id("dynamic_framebuffer/" + getId()));
        }
    }
}
