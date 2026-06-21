package dev.ultreon.devices.programs.system.object;

import dev.ultreon.devices.object.AppInfo;

import java.util.List;

/**
 * @author MrCrayfish
 */
public record LocalEntry(AppInfo info) implements AppEntry {

    @Override
    public String id() {
        return info.getAppId().toString();
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
    public List<String> authors() {
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
    public List<String> screenshots() {
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
