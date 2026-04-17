package dev.ultreon.devices.programs;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ultreon.devices.Reference;
import dev.ultreon.devices.api.app.*;
import dev.ultreon.devices.api.app.component.*;
import dev.ultreon.devices.api.app.Component;
import dev.ultreon.devices.api.app.Dialog;
import dev.ultreon.devices.api.app.component.Button;
import dev.ultreon.devices.api.app.component.Label;
import dev.ultreon.devices.api.app.component.TextField;
import dev.ultreon.devices.api.app.renderer.ListItemRenderer;
import dev.ultreon.devices.api.io.File;
import dev.ultreon.devices.api.print.IPrint;
import dev.ultreon.devices.api.utils.RenderUtil;
import dev.ultreon.devices.core.Laptop;
import dev.ultreon.devices.core.io.FileSystem;
import dev.ultreon.devices.debug.DebugLog;
import dev.ultreon.devices.object.Canvas;
import dev.ultreon.devices.object.ColorGrid;
import dev.ultreon.devices.object.Picture;
import dev.ultreon.devices.programs.system.layout.StandardLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.awt.*;
import java.util.Objects;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class PixelPainterApp extends Application {
    private static final Identifier PIXEL_PAINTER_ICONS = Identifier.parse("devices:textures/gui/pixel_painter.png");

    private static final Color ITEM_BACKGROUND = new Color(170, 176, 194);
    private static final Color ITEM_SELECTED = new Color(200, 176, 174);
    private static final Color AUTHOR_TEXT = new Color(114, 120, 138);

    /* Main Menu */
    private StandardLayout layoutMainMenu;
    private Label labelLogo;
    private Button btnNewPicture;
    private Button btnLoadPicture;

    /* New Picture */
    private Layout layoutNewPicture;
    private Label labelName;
    private TextField fieldName;
    private Label labelAuthor;
    private TextField fieldAuthor;
    private Label labelSize;
    private CheckBox checkBox16x;
    private CheckBox checkBox32x;
    private Button btnCreatePicture;

    /* Load Picture */
    private Layout layoutLoadPicture;
    private ItemList<Picture> listPictures;
    private Button btnLoadSavedPicture;
    private Button btnBrowseSavedPicture;
    private Button btnDeleteSavedPicture;
    private Button btnBackSavedPicture;

    /* Drawing */
    private Layout layoutDraw;
    private Canvas canvas;
    private ButtonToggle btnPencil;
    private ButtonToggle btnBucket;
    private ButtonToggle btnEraser;
    private ButtonToggle btnEyeDropper;
    private Button btnCancel;
    private Button btnSave;
    private Slider redSlider;
    private Slider greenSlider;
    private Slider blueSlider;
    private Component colorDisplay;
    private ColorGrid colorGrid;
    private CheckBox displayGrid;

    public PixelPainterApp() {
        //super("pixel_painter", "Pixel Painter");
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void init(@Nullable CompoundTag intent) {
        /* Main Menu */
        layoutMainMenu = new StandardLayout("Main Menu", 201, 125, this, null);
        layoutMainMenu.setIcon(Icons.HOME);

        ItemList<Picture> pictureList = new ItemList<>(5, 43, 80, 4);
        pictureList.setListItemRenderer(new ListItemRenderer<>(18) {
            @Override
            public void render(GuiGraphicsExtractor graphics, Picture picture, Minecraft mc, int x, int y, int width, int height, boolean selected) {
                RenderUtil.drawStringClipped(graphics, "Henlo", x, y, 100, AUTHOR_TEXT.getRGB(), true);
            }
        });
        layoutMainMenu.addComponent(pictureList);

        btnNewPicture = new Button(5, 25, "New", Icons.PICTURE);
        btnNewPicture.setSize(40, 16);
        btnNewPicture.setToolTip("New Picture", "Start a new masterpiece!");
        btnNewPicture.setClickListener(event ->
        {
            if (event.button() == 0) {
                setCurrentLayout(layoutNewPicture);
            }
        });
        layoutMainMenu.addComponent(btnNewPicture);

        btnLoadPicture = new Button(48, 25, Icons.IMPORT);
        btnLoadPicture.setToolTip("Load External", "Open a picture from file");
        btnLoadPicture.setClickListener(event -> setCurrentLayout(layoutLoadPicture));
        layoutMainMenu.addComponent(btnLoadPicture);

        Button btnDeletePicture = new Button(67, 25, Icons.TRASH);
        btnDeletePicture.setToolTip("Delete", "Removes the selected image");
        layoutMainMenu.addComponent(btnDeletePicture);


        /* New Picture */

        layoutNewPicture = new Layout(180, 65);

        labelName = new Label("Name", 5, 5);
        layoutNewPicture.addComponent(labelName);

        fieldName = new TextField(5, 15, 100);
        layoutNewPicture.addComponent(fieldName);

        labelAuthor = new Label("Author", 5, 35);
        layoutNewPicture.addComponent(labelAuthor);

        fieldAuthor = new TextField(5, 45, 100);
        layoutNewPicture.addComponent(fieldAuthor);

        labelSize = new Label("Size", 110, 5);
        layoutNewPicture.addComponent(labelSize);

        RadioGroup sizeGroup = new RadioGroup();

        checkBox16x = new CheckBox("16x", 110, 17);
        checkBox16x.setSelected(true);
        checkBox16x.setRadioGroup(sizeGroup);
        layoutNewPicture.addComponent(checkBox16x);

        checkBox32x = new CheckBox("32x", 145, 17);
        checkBox32x.setRadioGroup(sizeGroup);
        layoutNewPicture.addComponent(checkBox32x);

        btnCreatePicture = new Button(110, 40, "Create");
        btnCreatePicture.setSize(65, 20);
        btnCreatePicture.setClickListener(event ->
        {
            setCurrentLayout(layoutDraw);
            canvas.createPicture(fieldName.getText(), fieldAuthor.getText(), checkBox16x.isSelected() ? Picture.Size.X16 : Picture.Size.X32);
        });
        layoutNewPicture.addComponent(btnCreatePicture);


        /* Load Picture */

        layoutLoadPicture = new Layout(165, 116);
        layoutLoadPicture.setInitListener(() ->
        {
            listPictures.removeAll();
            FileSystem.getApplicationFolder(this, (folder, success) ->
            {
                if (success) {
                    assert folder != null;
                    folder.search(file -> file.isForApplication(this)).forEach(file ->
                    {
                        Picture picture = Picture.fromFile(file);
                        listPictures.addItem(picture);
                    });
                }
            });
        });

        listPictures = new ItemList<>(5, 5, 80, 5);
        listPictures.setListItemRenderer(new ListItemRenderer<>(20) {
            @Override
            public void render(GuiGraphicsExtractor graphics, Picture picture, Minecraft mc, int x, int y, int width, int height, boolean selected) {
                graphics.fill(x, y, x + width, y + height, selected ? ITEM_SELECTED.getRGB() : ITEM_BACKGROUND.getRGB());
                graphics.text(mc.font, picture.getName(), x + 2, y + 2, Color.WHITE.getRGB(), false);
                graphics.text(mc.font, picture.getAuthor(), x + 2, y + 11, AUTHOR_TEXT.getRGB(), false);
            }
        });
        listPictures.setItemClickListener((picture, index, mouseButton) ->
        {
            if (mouseButton == 0) {
                btnLoadSavedPicture.setEnabled(true);
                btnDeleteSavedPicture.setEnabled(true);
            }
        });
        layoutLoadPicture.addComponent(listPictures);

        btnLoadSavedPicture = new Button(110, 5, "Load");
        btnLoadSavedPicture.setSize(50, 20);
        btnLoadSavedPicture.setEnabled(false);
        btnLoadSavedPicture.setClickListener(event ->
        {
            if (listPictures.getSelectedIndex() != -1) {
                canvas.setPicture(Objects.requireNonNull(listPictures.getSelectedItem()));
                setCurrentLayout(layoutDraw);
            }
        });
        layoutLoadPicture.addComponent(btnLoadSavedPicture);

        btnBrowseSavedPicture = new Button(110, 30, "Browse");
        btnBrowseSavedPicture.setSize(50, 20);
        btnBrowseSavedPicture.setClickListener(event ->
        {
            Dialog.OpenFile dialog = new Dialog.OpenFile(this);
            dialog.setResponseHandler((success, file) ->
            {
                if (file.isForApplication(this)) {
                    Picture picture = Picture.fromFile(file);
                    canvas.setPicture(picture);
                    setCurrentLayout(layoutDraw);
                    return true;
                } else {
                    Dialog.Message dialog2 = new Dialog.Message("Invalid file for Pixel Painter");
                    openDialog(dialog2);
                }
                return false;
            });
            openDialog(dialog);
        });
        layoutLoadPicture.addComponent(btnBrowseSavedPicture);

        btnDeleteSavedPicture = new Button(110, 55, "Delete");
        btnDeleteSavedPicture.setSize(50, 20);
        btnDeleteSavedPicture.setEnabled(false);
        btnDeleteSavedPicture.setClickListener(event ->
        {
            if (listPictures.getSelectedIndex() != -1) {
                Picture picture = listPictures.getSelectedItem();
                assert picture != null;
                File file = picture.getSource();
                if (file != null) {
                    file.delete((o, success) ->
                    {
                        if (success) {
                            listPictures.removeItem(listPictures.getSelectedIndex());
                            btnDeleteSavedPicture.setEnabled(false);
                            btnLoadSavedPicture.setEnabled(false);
                        } else {
                            //TODO error dialog
                        }
                    });
                } else {
                    //TODO error dialog
                }
            }
        });
        layoutLoadPicture.addComponent(btnDeleteSavedPicture);

        btnBackSavedPicture = new Button(110, 80, "Back");
        btnBackSavedPicture.setSize(50, 20);
        btnBackSavedPicture.setClickListener(event -> setCurrentLayout(layoutMainMenu));
        layoutLoadPicture.addComponent(btnBackSavedPicture);


        /* Drawing */

        layoutDraw = new Layout(213, 140);

        canvas = new Canvas(5, 5);
        layoutDraw.addComponent(canvas);

        RadioGroup toolGroup = new RadioGroup();

        btnPencil = new ButtonToggle(138, 5, PIXEL_PAINTER_ICONS, 0, 0, 10, 10);
        btnPencil.setClickListener(event -> canvas.setCurrentTool(Canvas.PENCIL));
        btnPencil.setRadioGroup(toolGroup);
        layoutDraw.addComponent(btnPencil);

        btnBucket = new ButtonToggle(138, 24, PIXEL_PAINTER_ICONS, 10, 0, 10, 10);
        btnBucket.setClickListener(event -> canvas.setCurrentTool(Canvas.BUCKET));
        btnBucket.setRadioGroup(toolGroup);
        layoutDraw.addComponent(btnBucket);

        btnEraser = new ButtonToggle(138, 43, PIXEL_PAINTER_ICONS, 20, 0, 10, 10);
        btnEraser.setClickListener(event -> canvas.setCurrentTool(Canvas.ERASER));
        btnEraser.setRadioGroup(toolGroup);
        layoutDraw.addComponent(btnEraser);

        btnEyeDropper = new ButtonToggle(138, 62, PIXEL_PAINTER_ICONS, 30, 0, 10, 10);
        btnEyeDropper.setClickListener(event ->
        {
            canvas.setCurrentTool(Canvas.EYE_DROPPER);
            Color color = new Color(canvas.getCurrentColor());
            redSlider.setPercentage(color.getRed() / 255F);
            greenSlider.setPercentage(color.getGreen() / 255F);
            blueSlider.setPercentage(color.getBlue() / 255F);
        });
        btnEyeDropper.setRadioGroup(toolGroup);
        layoutDraw.addComponent(btnEyeDropper);

        Button button = new Button(138, 81, Icons.PRINTER);
        button.setClickListener(event ->
        {
            DebugLog.log("Print action triggered in pixel painter");
            if (event.button() == 0) {
                Dialog.Print dialog = new Dialog.Print(new PicturePrint(canvas.picture.getName(), canvas.getPixels(), canvas.picture.getWidth()));
                openDialog(dialog);
            }
        });
//        button.setEnabled(false); // FIXME: WHY THE ACTUAL HELL IS THIS EVEN HERE :skull:
        layoutDraw.addComponent(button);

        btnCancel = new Button(138, 100, PIXEL_PAINTER_ICONS, 50, 0, 10, 10);
        btnCancel.setClickListener(event ->
        {
            if (canvas.isExistingImage())
                setCurrentLayout(layoutLoadPicture);
            else
                setCurrentLayout(layoutMainMenu);
            canvas.clear();
        });
        layoutDraw.addComponent(btnCancel);

        btnSave = new Button(138, 119, PIXEL_PAINTER_ICONS, 40, 0, 10, 10);
        btnSave.setClickListener(event ->
        {
            canvas.picture.pixels = canvas.copyPixels();

            CompoundTag pictureTag = new CompoundTag();
            canvas.picture.writeToNBT(pictureTag);

            if (canvas.isExistingImage()) {
                File file = canvas.picture.getSource();
                if (file != null) {
                    file.setData(pictureTag, (response, success) ->
                    {
                        assert response != null;
                        if (response.getStatus() == FileSystem.Status.SUCCESSFUL) {
                            canvas.clear();
                            setCurrentLayout(layoutLoadPicture);
                        } else {
                            //TODO error dialog
                        }
                    });
                }
            } else {
                Dialog.SaveFile dialog = new Dialog.SaveFile(PixelPainterApp.this, pictureTag);
                dialog.setResponseHandler((success, file) ->
                {
                    if (success) {
                        canvas.clear();
                        setCurrentLayout(layoutLoadPicture);
                        return true;
                    } else {
                        //TODO error dialog
                    }
                    return false;
                });
                openDialog(dialog);
            }
        });
        layoutDraw.addComponent(btnSave);

        redSlider = new Slider(158, 30, 50);
        redSlider.setSlideListener(percentage -> canvas.setRed(percentage));
        layoutDraw.addComponent(redSlider);

        greenSlider = new Slider(158, 46, 50);
        greenSlider.setSlideListener(percentage -> canvas.setGreen(percentage));
        layoutDraw.addComponent(greenSlider);

        blueSlider = new Slider(158, 62, 50);
        blueSlider.setSlideListener(percentage -> canvas.setBlue(percentage));
        layoutDraw.addComponent(blueSlider);

        colorDisplay = new Component(158, 5) {
            @Override
            public void render(GuiGraphicsExtractor graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
                graphics.fill(xPosition, yPosition, xPosition + 50, yPosition + 20, Color.DARK_GRAY.getRGB());
                graphics.fill(xPosition + 1, yPosition + 1, xPosition + 49, yPosition + 19, canvas.getCurrentColor());
            }
        };
        layoutDraw.addComponent(colorDisplay);

        colorGrid = new ColorGrid(157, 82, 50, canvas, redSlider, greenSlider, blueSlider);
        layoutDraw.addComponent(colorGrid);

        displayGrid = new CheckBox("Grid", 166, 120);
        displayGrid.setClickListener(event -> canvas.setShowGrid(displayGrid.isSelected()));
        layoutDraw.addComponent(displayGrid);

        setCurrentLayout(layoutMainMenu);
    }

    @Override
    public void load(CompoundTag tagCompound) {

    }

    @Override
    public void save(CompoundTag tagCompound) {

    }

    @Override
    public void onClose() {
        super.onClose();
        listPictures.removeAll();
    }

    public static class PicturePrint implements IPrint {
        private String name;
        private int[] pixels;
        private int resolution;
        private boolean cut;

        public PicturePrint() {
        }

        public PicturePrint(String name, int[] pixels, int resolution) {
            this.name = name;
            this.setPicture(pixels);
        }

        private void setPicture(int[] pixels) {
            int resolution = (int) Math.sqrt(pixels.length);
            Picture.Size size = Picture.Size.getFromSize(resolution);
            if (size == null) {
                throw new IllegalArgumentException("Invalid pixels");
            }
            this.resolution = resolution;
            this.pixels = pixels;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int speed() {
            return resolution;
        }

        @Override
        public boolean requiresColor() {
            for (int pixel : pixels) {
                int r = pixel >> 16 & 255;
                int g = pixel >> 8 & 255;
                int b = pixel & 255;
                if (r != g || r != b) {
                    return true;
                }
            }
            return false;
        }

        public int getResolution() {
            return resolution;
        }

        @Override
        public int[] getPixels() {
            return pixels;
        }

        @Override
        public CompoundTag toTag() {
            CompoundTag tag = new CompoundTag();
            tag.putString("name", name);
            tag.putIntArray("pixels", pixels);
            tag.putInt("resolution", resolution);
            if (cut) tag.putBoolean("cut", true);
            return tag;
        }

        @Override
        public void fromTag(CompoundTag tag) {
            name = tag.getString("name").orElse(null);
            cut = tag.getBooleanOr("cut", false);
            setPicture(tag.getIntArray("pixels").orElse(null));
        }

        @Override
        public Class<? extends Renderer> getRenderer() {
            return PictureRenderer.class;
        }
    }

    public static class PictureRenderer implements IPrint.Renderer {
        public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Reference.MOD_ID, "textures/model/paper.png");

        @SuppressWarnings("resource")
        @Override
        public boolean render(GuiGraphicsExtractor graphics, PoseStack pose, CompoundTag data) {
            if (data.contains("pixels") && data.contains("resolution")) {
                int[] pixels = data.getIntArray("pixels").orElse(null);
                int resolution = data.getIntOr("resolution", 0);
                boolean cut = data.getBooleanOr("cut", false);

                if (pixels.length != resolution * resolution)
                    return false;

//                GlStateManager.disableLighting();
                pose.mulPose(new Quaternionf(0, 1, 0, 180));

                // This is for the paper background
                if (!cut) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, -1, 0, 0, 0, 1, 1, resolution, resolution, resolution, resolution);
                }

                // This creates a flipped copy of the pixel array
                // as it otherwise would be mirrored
                NativeImage image = new NativeImage(resolution, resolution, false);
                for (int i = 0; i < resolution; i++) {
                    for (int j = 0; j < resolution; j++) {
                        image.setPixel(resolution - i - 1, resolution - j - 1, pixels[i + j * resolution]);
                    }
                }

                // FixMe - Port to 26.1+
//                int textureId = TextureUtil.generateTextureId();
//                TextureUtil.prepareImage(textureId, resolution, resolution);
//
//                RenderSystem.setShaderTexture(0, textureId);
//                RenderUtil.drawRectWithTexture(null, pose, -1, 0, 0, 0, 1, 1, resolution, resolution, resolution, resolution);
//                RenderSystem.deleteTexture(textureId);
//
////                RenderSystem.disableRescaleNormal();
//                RenderSystem.disableBlend();
//                RenderSystem.enableLighting();
                return true;
            }
            return false;
        }
    }
}
