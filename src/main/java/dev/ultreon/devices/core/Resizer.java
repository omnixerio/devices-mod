package dev.ultreon.devices.core;

import org.joml.Vector2f;

class Resizer {
    private final float ratio;
    private final float relativeRatio;
    private final Orientation orientation;
    private final float sourceWidth;
    private final float sourceHeight;

    public Resizer(float srcWidth, float srcHeight) {
        this.ratio = srcWidth / srcHeight;

        if (srcWidth > srcHeight) {
            this.relativeRatio = srcWidth / srcHeight;
            this.orientation = Orientation.LANDSCAPE;
        } else if (srcWidth < srcHeight) {
            this.relativeRatio = srcHeight / srcWidth;
            this.orientation = Orientation.PORTRAIT;
        } else {
            this.relativeRatio = 1;
            this.orientation = Orientation.SQUARE;
        }

        this.sourceWidth = srcWidth;
        this.sourceHeight = srcHeight;
    }

    public Vector2f thumbnail(float maxWidth, float maxHeight) {
        float aspectRatio;
        float width;
        float height;

        if (this.sourceWidth < this.sourceHeight) {
            aspectRatio = (float) (this.sourceWidth / (double) this.sourceHeight);

            width = maxWidth;
            height = (int) (width / aspectRatio);

            if (height < maxHeight) {
                aspectRatio = (float) (this.sourceHeight / (double) this.sourceWidth);

                height = maxHeight;
                width = (int) (height / aspectRatio);
            }
        } else {
            aspectRatio = (float) (this.sourceHeight / (double) this.sourceWidth);

            height = maxHeight;
            width = (int) (height / aspectRatio);
            if (width < maxWidth) {
                aspectRatio = (float) (this.sourceWidth / (double) this.sourceHeight);

                width = maxWidth;
                height = (int) (width / aspectRatio);
            }
        }

        return new Vector2f(width, height);
    }

    /**
     * Aspect ratio orientation.
     */
    public enum Orientation {
        LANDSCAPE,
        SQUARE,
        PORTRAIT
    }

    public float getRatio() {
        return this.ratio;
    }

    public float getRelativeRatio() {
        return this.relativeRatio;
    }

    public Orientation getOrientation() {
        return this.orientation;
    }

    public float getSourceWidth() {
        return this.sourceWidth;
    }

    public float getSourceHeight() {
        return this.sourceHeight;
    }
}
