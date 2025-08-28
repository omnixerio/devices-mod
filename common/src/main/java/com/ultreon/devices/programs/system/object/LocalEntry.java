package com.ultreon.devices.programs.system.object;

import com.ultreon.devices.object.AppInfo;

/**
 * @author MrCrayfish
 */
public record LocalEntry(AppInfo info) implements AppEntry {

    @Override
    public String id() {
        return info.getId().toString();
    }

    @Override
    public String name() {
        return info.getName();
    }

    @Deprecated
    @Override
    public String author() {
        return info.getAuthor();
    }

    @Override
    public String[] authors() {
        return info.getAuthors();
    }

    @Override
    public String description() {
        return info.getDescription();
    }

    @Override
    public String version() {
        return info.getVersion();
    }

    @Override
    public AppInfo.Icon icon() {
        return info.getIcon();
    }

    @Override
    public String[] screenshots() {
        return info.getScreenshots();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AppEntry) {
            return ((AppEntry) obj).id().equals(id());
        }
        return false;
    }
}
