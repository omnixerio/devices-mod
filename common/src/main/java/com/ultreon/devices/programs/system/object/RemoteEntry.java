package com.ultreon.devices.programs.system.object;

import com.ultreon.devices.object.AppInfo;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author MrCrayfish
 */
public class RemoteEntry implements AppEntry {
    public String id;
    public String name;
    public String[] authors;
    public String author;
    public String description;
    public int screenshots;
    public String projectId;

    @Override
    public String id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

    @Deprecated
    @Override
    public String author() {
        return author == null ? AppEntry.super.author() : author;
    }

    @Override
    public List<String> authors() {
        return List.of(authors);
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    @Nullable
    public String version() {
        return null;
    }

    @Override
    public AppInfo.Icon icon() {
        return null;
    }

    @Override
    public List<String> screenshots() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AppEntry) {
            return ((AppEntry) obj).id().equals(id);
        }
        return false;
    }
}
