package com.ultreon.devices.object;

import com.ultreon.devices.api.io.File;
import net.minecraft.nbt.CompoundTag;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

public class Picture {
    public int[] pixels;
    public Size size;
    private File source;
    private final String name;
    private final String author;

    public Picture(String name, String author, Size size) {
        this.name = name;
        this.author = author;
        this.pixels = new int[size.width * size.height];
        this.size = size;
        init();
    }

    public static Picture fromFile(File file) {
        CompoundTag data = file.getData();
        assert data != null;
        Picture picture = new Picture(data.getString("Name"), data.getString("Author"), Objects.requireNonNull(Size.getFromSize(data.getInt("Resolution"))));
        picture.source = file;
        picture.pixels = data.getIntArray("Pixels");
        return picture;
    }

    private void init() {
        Arrays.fill(pixels, new Color(1.0F, 1.0F, 1.0F, 0.0F).getRGB());
    }

    public File getSource() {
        return source;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public int[] getPixels() {
        return pixels;
    }

    public int getWidth() {
        return size.width;
    }

    public int getHeight() {
        return size.height;
    }

    public int getPixelWidth() {
        return size.pixelWidth;
    }

    public int getPixelHeight() {
        return size.pixelHeight;
    }

    public int[] copyPixels() {
        int[] copiedPixels = new int[pixels.length];
        System.arraycopy(pixels, 0, copiedPixels, 0, pixels.length);
        return copiedPixels;
    }

    @Override
    public String toString() {
        return name;
    }

    public void writeToNBT(CompoundTag tagCompound) {
        tagCompound.putString("Name", getName());
        tagCompound.putString("Author", getAuthor());
        tagCompound.putIntArray("Pixels", pixels);
        tagCompound.putInt("Resolution", size.width);
    }

    public enum Size {
        X16(16, 16, 8, 8), X32(32, 32, 4, 4);

        public int width, height;
        public int pixelWidth, pixelHeight;

        Size(int width, int height, int pixelWidth, int pixelHeight) {
            this.width = width;
            this.height = height;
            this.pixelWidth = pixelWidth;
            this.pixelHeight = pixelHeight;
        }

        public static Size getFromSize(int size) {
            if (size == 16) return X16;
            if (size == 32) return X32;
            return null;
        }
    }
}
