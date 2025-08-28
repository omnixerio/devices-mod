package dev.ultreon.devices.object;

import dev.ultreon.devices.programs.system.component.FileInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Unit;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;

public class Picture {
    public int[] pixels;
    public Size size;
    private FileInfo source;
    private final String name;
    private String author = "Unknown";

    public Picture(String name, String author, Size size) {
        this.name = name;
        this.author = author;
        this.pixels = new int[size.width * size.height];
        this.size = size;
        init();
    }

    public Picture(FileInfo source) {
        this.name = source.getName();
        this.source = source;
        init();
    }

    public static Picture fromFile(FileInfo file) {
        return new Picture(file);
    }

    public void load(Consumer<Result<Picture>> callback) {
        callback.accept(Result.success(this));
    }

    private void init() {
        Arrays.fill(pixels, new Color(1.0F, 1.0F, 1.0F, 0.0F).getRGB());
    }

    public FileInfo getSource() {
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

    public void writeToFile(FileInfo file, Consumer<Result<Unit>> callback) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutput output = new DataOutputStream(bos);
        try {
            output.writeUTF(getName());
            output.writeUTF(getAuthor());
            output.writeInt(size.pixelWidth);
            output.writeInt(size.pixelHeight);

            output.writeInt(pixels.length);
            for (int pixel : pixels) {
                output.writeInt(pixel);
            }

            file.write(bos.toByteArray(), response -> {
                if (!response.success()) callback.accept(Result.failure(response.message()));
                try {
                    bos.close();
                    callback.accept(Result.success(Unit.INSTANCE));
                } catch (IOException e) {
                    callback.accept(Result.failure(e.getMessage()));
                }
            });
        } catch (IOException e) {
            callback.accept(Result.failure(e.getMessage()));
        }
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
