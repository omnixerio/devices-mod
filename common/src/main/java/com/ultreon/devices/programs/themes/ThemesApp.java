package com.ultreon.devices.programs.themes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.devices.api.app.*;
import com.ultreon.devices.api.app.Component;
import com.ultreon.devices.api.app.Dialog;
import com.ultreon.devices.api.app.System;
import com.ultreon.devices.api.app.component.Button;
import com.ultreon.devices.api.app.component.TextField;
import com.ultreon.devices.api.utils.RenderUtil;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.object.AppInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import org.apache.commons.codec.binary.Hex;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HexFormat;

public class ThemesApp extends Application implements SystemAccessor {
    private System system;
    private int[] lastMousePositionsX = null;
    private int[] lastMousePositionsY = null;

    private final int[] currentMouse = new int[2];

    @Override
    public void init(@Nullable CompoundTag intent) {
        this.setCurrentLayout(createMainMenu());
    }

    @Override
    public void load(CompoundTag tag) {

    }

    @Override
    public void save(CompoundTag tag) {

    }

    @Override
    public void sendSystem(System system) {
        this.system = system;
    }

    @Override
    public void render(GuiGraphics graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active, float partialTicks) {
        super.render(graphics, laptop, mc, x, y, mouseX, mouseY, active, partialTicks);
    }

    @Override
    public void onTick() {
        super.onTick();
        if (lastMousePositionsX != null) {
            var newX = new int[20];
            var newY = new int[20];
            assert lastMousePositionsX.length == lastMousePositionsY.length;
            for (int i = 0; i < newX.length-1; i++) {
                var x = lastMousePositionsX[i];
                var y = lastMousePositionsY[i];
                newX[i+1] = x;
                newY[i+1] = y;
            }
            newX[0] = currentMouse[0];
            newY[0] = currentMouse[1];
            lastMousePositionsX = newX;
            lastMousePositionsY = newY;
        }
    }

    private void renderBackground(GuiGraphics graphics, Minecraft minecraft, int x, int y, int width, int height, int mouseX, int mouseY, boolean active) {
        if (true) return;
        graphics.pose().pushPose();
        if (!active) return;
        currentMouse[0] = mouseX;
        currentMouse[1] = mouseY;
        if (lastMousePositionsX == null) {
            lastMousePositionsX = new int[20];
            Arrays.fill(lastMousePositionsX, mouseX);

            lastMousePositionsY = new int[20];
            Arrays.fill(lastMousePositionsY, mouseY);
        }
        graphics.fill(x, y, x + width, x + height, new Color(0, 0, 0).getRGB());
        for (int i = 0; i < lastMousePositionsX.length; i++) {
            mouseX = lastMousePositionsX[i];
            mouseY = lastMousePositionsY[i];
            graphics.fill(mouseX - 5, mouseY - 5, mouseX + 5, mouseY + 5, 0x57575788);
        }
        graphics.pose().popPose();
    }

    int marginX = 10;
    int marginY = 10;
    int paddingY = 4;
    private Layout createMainMenu() {
        Layout mainMenu = new Layout(200, 100);
        mainMenu.setBackground(this::renderBackground);
        mainMenu.addComponent(new Button(marginX, marginY, "Themes"));
        mainMenu.addComponent(createTintButton());
        return mainMenu;
    }

    private Button createTintButton() {
        var button = new Button(marginX, marginY + 16 + paddingY, "Tints");
        button.setClickListener((mouseX, mouseY, btn) -> {
            setCurrentLayout(createTintMenu());
        });
        return button;
    }

    private Layout createTintMenu() {
        var ly = new Layout(200, 100) {
            @Override
            public void init() {
                super.init();
                var s = new ScrollableLayout(200, 800, 100);
                var i = 0;
                for (AppInfo installedApplication : ThemesApp.this.system.getInstalledApplications()) {
                    s.addComponent(new AppTintSet(0, 16*i, installedApplication));
                    i++;
                }
                this.addComponent(s);
            }
        };
        ly.setTitle("Tints");
        return ly;
    }

    private class AppTintSet extends Component {
        private static final int width = 200;
        private static final int height = 16;
        private final AppInfo info;
        private boolean editing;

        /**
         * The default constructor for a component.
         * <p>
         * Laying out components is simply relative positioning. So for left (x position),
         * specific how many pixels from the left of the application window you want
         * it to be positioned at. The top is the same, but instead from the top (y position).
         *
         * @param left how many pixels from the left
         * @param top  how many pixels from the top
         */
        public AppTintSet(int left, int top, AppInfo info) {
            super(left, top);
            this.info = info;
        }

        @Override
        protected void init(Layout layout) {
            super.init(layout);
            var primaryTint = new TextField(left+16, top, 50);
            primaryTint.setText(toColorHex(info.getTint(1)));
            primaryTint.setBackgroundColor(new Color(getColorScheme().getBackgroundColor()).darker().darker());
            primaryTint.setTextColor(Color.WHITE.darker());
            primaryTint.setEnabled(false);
            layout.addComponent(primaryTint);

            var secondaryTint = new TextField(left+16+50, top, 50);
            secondaryTint.setText(toColorHex(info.getTint(2)));
            secondaryTint.setBackgroundColor(new Color(getColorScheme().getBackgroundColor()).darker().darker());
            secondaryTint.setTextColor(Color.WHITE.darker());
            secondaryTint.setEnabled(false);
            layout.addComponent(secondaryTint);

            var editButton = new Button(left+16+50+50, top, Icons.EDIT);
            editButton.setSize(16, 16);

            var resetButton = new Button(left+16+50+50+16, top, Icons.RELOAD);
            resetButton.setSize(16, 16);
            resetButton.setEnabled(false);

            var okButton = new Button(left+16+50+50+16+16, top, Icons.CHECK);
            resetButton.setClickListener((__, ___, ____) -> {
                this.info.setTintProvider(AppInfo.getDefaultTintProvider());

                primaryTint.setText(toColorHex(info.getTint(1)));
                primaryTint.setBackgroundColor(new Color(getColorScheme().getBackgroundColor()).darker().darker());
                primaryTint.setTextColor(Color.WHITE.darker());
                primaryTint.setEnabled(false);

                secondaryTint.setText(toColorHex(info.getTint(2)));
                secondaryTint.setBackgroundColor(new Color(getColorScheme().getBackgroundColor()).darker().darker());
                secondaryTint.setTextColor(Color.WHITE.darker());
                secondaryTint.setEnabled(false);

                resetButton.setEnabled(false);
                editButton.setEnabled(true);
                okButton.setEnabled(false);
            });
            layout.addComponent(resetButton);

            okButton.setSize(16, 16);
            okButton.setEnabled(false);
            okButton.setClickListener((__, ___, ____) -> {
                try {
                    var l = new Color(new BigInteger(primaryTint.getText().replace("#", ""), 16).intValue());
                    var ll = new Color(new BigInteger(secondaryTint.getText().replace("#", ""), 16).intValue());
                    this.info.setTintProvider(new ThemeTintProvider(l, ll));
                    resetButton.setEnabled(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    openDialog(new Dialog.Message("Failed to set tint"));
                }
             //   okButton.setEnabled();
            });
            layout.addComponent(okButton);


            // click listener
            editButton.setClickListener((__, ___, ____) -> {
                primaryTint.setBackgroundColor(new Color(getColorScheme().getBackgroundColor()));
                primaryTint.setTextColor(Color.WHITE);
                primaryTint.setEnabled(true);

                secondaryTint.setBackgroundColor(new Color(getColorScheme().getBackgroundColor()));
                secondaryTint.setTextColor(Color.WHITE);
                secondaryTint.setEnabled(true);
                this.editing = true;

                okButton.setEnabled(true);
                editButton.setEnabled(false);
            });
            layout.addComponent(editButton);

            if (this.info.getTintProvider() instanceof ThemeTintProvider) {
                editButton.forceClick(0, 0, 0);
                resetButton.setEnabled(true);
            }
        }

        private String toColorHex(int b) {
            String hexColor = String.format("#%06X", (0xFFFFFF & b));
            return hexColor;
        }

        @Override
        protected void render(GuiGraphics graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
            super.render(graphics, laptop, mc, x, y, mouseX, mouseY, windowActive, partialTicks);
            graphics.pose().pushPose();
            RenderUtil.drawIcon(graphics, x, y, info, height, height); // height is intended
            graphics.pose().popPose();
        }
    }

    private static void createCTP() {

    }

    private static class ThemeTintProvider implements AppInfo.TintProvider {
        private final Color l;
        private final Color ll;
        ThemeTintProvider(Color l, Color ll) {
            this.l = l;
            this.ll = ll;
        }
        @Override
        public int getTintColor(AppInfo info, int i) {
            return switch (i) {
                case 0 -> new Color(255, 255, 255).getRGB();
                case 1 -> l.getRGB();
                case 2 -> ll.getRGB();
                default -> new Color(255, 255, 255).getRGB();
            };
        }

        @Override
        public CompoundTag toTag() {
            var l = new CompoundTag();
            l.putInt("0", getTintColor(null, 0));
            l.putInt("1", getTintColor(null, 1));
            l.putInt("2", getTintColor(null, 2));
            return l;
        }
    }
}
