package com.ultreon.devices.client;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL20;

public class OpenGLFramebufferImpl implements GLFramebuffer {
    private final int id;
    private final FramebufferTexture texture;

    public OpenGLFramebufferImpl(int width, int height) {
        id = GL30.glGenFramebuffers();

        this.texture = createTexture(width, height);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
        GL30.glFramebufferTexture2D(id, GL30.GL_COLOR_ATTACHMENT0, GL20.GL_TEXTURE_2D, texture.getId(), 0);
        GL30.glFramebufferRenderbuffer(id, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, 0);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
        GL30.glFramebufferRenderbuffer(id, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, 0);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public int getId() {
        return id;
    }
    
    public void begin() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
    }
    
    public void end() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }
    
    public void dispose() {
        GL30.glDeleteFramebuffers(id);
    }

    @Override
    public FramebufferTexture getTexture() {
        return texture;
    }
}
