package com.ultreon.devices.programs.system.object;

import net.minecraft.resources.Identifier;

/**
 * @author MrCrayfish
 */
public class ImageEntry {
    private final Type type;
    private Identifier resource;
    private String url;

    public ImageEntry(Identifier resource) {
        this.type = Type.LOCAL;
        this.resource = resource;
    }

    public ImageEntry(String url) {
        this.type = Type.REMOTE;
        this.url = url;
    }

    public Type getType() {
        return type;
    }

    public Identifier getResource() {
        return resource;
    }

    public String getUrl() {
        return url;
    }

    public enum Type {
        LOCAL, REMOTE
    }
}
