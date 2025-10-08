package dev.ultreon.devices.core.io;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;

public class Path implements Iterable<String> {
    private final String[] path;

    private Path(String[] split) {
        if (split.length == 0) throw new IllegalArgumentException("Path must be absolute!");
        if (!split[0].isEmpty()) throw new IllegalArgumentException("Path must be absolute!");

        path = split;

        for (int i = 1; i < split.length; i++) {
            if (split[i].isEmpty()) throw new IllegalArgumentException("Path must not contain empty segments");
            if (split[i].contains("/")) throw new IllegalArgumentException("Path must not contain any slashes!");
        }
    }

    public static Path of(String s) {
        if (s.endsWith("/")) s = s.substring(0, s.length() - 1);
        return new Path(s.split("/"));
    }

    public String toString() {
        return String.join("/", path);
    }

    public String[] getNames() {
        return path;
    }

    public Path resolve(String formattedId) {
        return Path.of(this + "/" + formattedId);
    }

    public Path getParent() {
        if (path.length == 1) return null;
        return Path.of(String.join("/", Arrays.copyOfRange(path, 0, path.length - 1)));
    }

    public String getFileName() {
        if (path.length == 1) return "/";
        return path[path.length - 1];
    }

    public boolean endsWith(String s) {
        return toString().endsWith(s);
    }

    public Path resolveSibling(String name) {
        String[] newPath = new String[path.length];
        System.arraycopy(path, 0, newPath, 0, path.length - 1);
        newPath[newPath.length - 1] = name;
        return new Path(newPath);
    }

    @Override
    public @NotNull Iterator<String> iterator() {
        return new Iterator<>() {
            private int i = 1;

            @Override
            public boolean hasNext() {
                return i < path.length;
            }

            @Override
            public String next() {
                return path[i++];
            }
        };
    }
}
