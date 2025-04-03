package com.ultreon.devices.client;

import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.GL20;

public class ARBFramebufferImpl implements GLFramebuffer {
    private final int id;
    private final FramebufferTexture texture;

    public ARBFramebufferImpl(int width, int height) {
        id = ARBFramebufferObject.glGenFramebuffers();

        texture = createTexture(width, height);

        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, id);
        ARBFramebufferObject.glFramebufferTexture2D(id, ARBFramebufferObject.GL_COLOR_ATTACHMENT0, GL20.GL_TEXTURE_2D, texture.getId(), 0);
        ARBFramebufferObject.glFramebufferRenderbuffer(id, ARBFramebufferObject.GL_DEPTH_ATTACHMENT, ARBFramebufferObject.GL_RENDERBUFFER, 0);
        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, id);
        ARBFramebufferObject.glFramebufferRenderbuffer(id, ARBFramebufferObject.GL_DEPTH_ATTACHMENT, ARBFramebufferObject.GL_RENDERBUFFER, 0);
        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, 0);
    }

    public int getId() {
        return id;
    }

    public void begin() {
        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, id);
    }

    public void end() {
        ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, 0);
    }

    public void dispose() {
        ARBFramebufferObject.glDeleteFramebuffers(id);
    }

    @Override
    public FramebufferTexture getTexture() {
        return texture;
    }
}
