package com.ultreon.devices.client;

import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL20;

public class EXTFramebufferImpl implements GLFramebuffer {
    private final int id;
    private final FramebufferTexture texture;

    public EXTFramebufferImpl(int width, int height) {
        id = EXTFramebufferObject.glGenFramebuffersEXT();

        this.texture = createTexture(width, height);

        EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, id);
        EXTFramebufferObject.glFramebufferTexture2DEXT(id, EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT, GL20.GL_TEXTURE_2D, texture.getId(), 0);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(id, EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, 0);
        EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, id);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(id, EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, 0);
        EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
    }
    
    public int getId() {
        return id;
    }
    
    public void begin() {
        EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, id);
    }
    
    public void end() {
        EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
    }
    
    public void dispose() {
        EXTFramebufferObject.glDeleteFramebuffersEXT(id);
    }

    @Override
    public FramebufferTexture getTexture() {
        return texture;
    }
}
