package com.ultreon.devices.programs.snake.layout;

import com.mojang.blaze3d.platform.InputConstants;
import com.ultreon.devices.api.app.Component;
import com.ultreon.devices.api.app.Icons;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.api.app.component.Button;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.debug.DebugLog;
import com.ultreon.devices.programs.snake.SnakeApp;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;

import java.awt.*;
import java.util.ArrayList;

/**
 * layout shoult be 15x15, background green 0x0A7A0A
 */
public class SnakeLayout extends Layout {
    public SnakeLayout(SnakeApp app) {
        super(150, 150);
        var button = new Button(1, 1, Icons.ARROW_LEFT);
        button.setClickListener(((mouseX, mouseY, mouseButton) -> {
            app.setCurrentLayout(app.titleScreen);
        }));

        this.setBackground((graphics, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
            graphics.fill(x,y,x+width,x+height, new Color(0x0, 0x0, 0x0).getRGB());
        });
        this.addComponent(button);
        this.addComponent(new Grid(0, 0));
    }

    @Override
    public void render(GuiGraphics graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        super.render(graphics, laptop, mc, x, y, mouseX, mouseY, windowActive, partialTicks);
    }

    @Override
    public void handleLoad() {
        super.handleLoad();
    }

    public static class Grid extends Component {
        private ArrayList<Pos2d> snakePos = new ArrayList<>() {
            @Override
            public Pos2d get(int index) {
                try {
                    return super.get(index);
                } catch (Exception e) {
                    return null;
                }
            }
        };
        private Direction2d direction2d = Direction2d.UP;
        private int speed = 5;
        private int tick = 0;
        private Pos2d applePos;

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
        public Grid(int left, int top) {
            super(left, top);
            var initialPos = new Pos2d(5, 5);
            initialPos.first = true;

            var lastInitPos = new Pos2d(5, 7);
            lastInitPos.last = true;
            snakePos.add(lastInitPos);
            snakePos.add(new Pos2d(5, 6));
            snakePos.add(initialPos);
            this.applePos = new Pos2d((int) (Math.random()*15), (int) (Math.random()*15));
            var clean = false;
            while (!clean) {
                for (var pos : snakePos) {
                    if (pos.samePos(applePos)) {
                        this.applePos = new Pos2d((int) (Math.random() * 15), (int) (Math.random() * 15));
                    }
                }

                var isC = true;
                for (var pos : snakePos) {
                    if (pos.samePos(applePos)) {
                        isC = false;
                    }
                }
                if (isC)clean=true;
            }

        }

        @Override
        protected void render(GuiGraphics graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
            super.render(graphics, laptop, mc, x, y, mouseX, mouseY, windowActive, partialTicks);
            var black = new Color(0, 0, 0, 0.5f);
            var intBlack = black.getRGB();
//            for (var i = 0;i<15;i++) {
//                for (var j=0;j<15;j++) {
//                    //DebugLog.log(i +", " + (i+j)/2 + ", " + j);
//                    intBlack = new Color(i*10, (i+j)/2*10, j*10, 127).getRGB();
//                    //0, 0 -> 10, 10
//                    // 10,10 -> 20, 20
//                    Gui.fill(pose, x+i*10, y+j*10, x+i*10+10, y+j*10+10, intBlack);
//                }
//            }

            var white = new Color(255, 255, 255).getRGB();
            var red = new Color(255, 0, 0).getRGB();

            for (int i = 0; i < snakePos.size(); i++) {
                //Pos2d pos = snakePos.get(i);
                renderConnectedSnakePart(graphics, x, y, white, i);
            }
            graphics.fill(x+applePos.x*10, y+applePos.y*10, x+applePos.x*10+10, y+applePos.y*10+10, red);
        }

        private void renderConnectedSnakePart(GuiGraphics graphics, int x, int y, int color, int index) {
            var pos = snakePos.get(index);
            graphics.fill(x+pos.x*10+1, y+pos.y*10+1, x+pos.x*10+10-1, y+pos.y*10+10-1, color);

            // Right Checks
            if (snakePos.get(index+1) != null && snakePos.get(index).right().samePos(snakePos.get(index+1))) {
                graphics.fill(x+pos.x*10+10-1, y+pos.y*10+1, x+pos.x*10+10, y+pos.y*10+10-1, color);
            }
            if (snakePos.get(index-1) != null && snakePos.get(index).right().samePos(snakePos.get(index-1))) {
                graphics.fill(x+pos.x*10+10-1, y+pos.y*10+1, x+pos.x*10+10, y+pos.y*10+10-1, color);
            }
            // Left Checks
            if (snakePos.get(index+1) != null && snakePos.get(index).left().samePos(snakePos.get(index+1))) {
                graphics.fill(x+pos.x*10, y+pos.y*10+1, x+pos.x*10+1, y+pos.y*10+10-1, color);
            }
            if (snakePos.get(index-1) != null && snakePos.get(index).left().samePos(snakePos.get(index-1))) {
                graphics.fill(x+pos.x*10, y+pos.y*10+1, x+pos.x*10+1, y+pos.y*10+10-1, color);
            }
            // Down Checks
            if (snakePos.get(index+1) != null && snakePos.get(index).down().samePos(snakePos.get(index+1))) {
                graphics.fill(x+pos.x*10+1, y+pos.y*10+10-1, x+pos.x*10+10-1, y+pos.y*10+10, color);
            }
            if (snakePos.get(index-1) != null && snakePos.get(index).down().samePos(snakePos.get(index-1))) {
                graphics.fill(x+pos.x*10+1, y+pos.y*10+10-1, x+pos.x*10+10-1, y+pos.y*10+10, color);
            }
            // Up Checks
            if (snakePos.get(index+1) != null && snakePos.get(index).up().samePos(snakePos.get(index+1))) {
                graphics.fill(x+pos.x*10+1, y+pos.y*10, x+pos.x*10+10-1, y+pos.y*10+1, color);
            }
            if (snakePos.get(index-1) != null && snakePos.get(index).up().samePos(snakePos.get(index-1))) {
                graphics.fill(x+pos.x*10+1, y+pos.y*10, x+pos.x*10+10-1, y+pos.y*10+1, color);
            }
            //Gui.fill(pose, x+pos.x*10, y+pos.y*10, x+pos.x*10+10, y+pos.y*10+10, color);
        }
        @Override
        public void handleKeyPressed(int keyCode, int scanCode, int modifiers) {
            super.handleKeyPressed(keyCode, scanCode, modifiers);
            switch (keyCode) {
                case InputConstants.KEY_W -> {
                    if (direction2d != Direction2d.DOWN) up();
                }
                case InputConstants.KEY_S -> {
                    if (direction2d != Direction2d.UP) down();
                }
                case InputConstants.KEY_A -> {
                    if (direction2d != Direction2d.RIGHT) left();
                }
                case InputConstants.KEY_D -> {
                    if (direction2d != Direction2d.LEFT) right();
                }
            }
        }

        private void up() {
            DebugLog.log("UP");
            this.direction2d = Direction2d.UP;
        }

        private void down() {
            DebugLog.log("DOWN");
            this.direction2d = Direction2d.DOWN;
        }

        private void left() {
            DebugLog.log("LEFT");
            this.direction2d = Direction2d.LEFT;
        }

        private void right() {
            DebugLog.log("RIGHT");
            this.direction2d = Direction2d.RIGHT;
        }

        @Override
        protected void handleTick() {
            super.handleTick();
            if (tick > speed) {
                tick = 0;
                Pos2d newPos = null;
                Pos2d newLastPos = null;
                var first = snakePos.get(snakePos.size() - 1);
                newPos = first.dir(direction2d);
                for (var pos : snakePos) {
                    if (newPos.samePos(pos)) {
                        speed = 999999999;
                        return;
                    }
                }
                if (newPos.x < 0 || newPos.x > 14 || newPos.y < 0 || newPos.y > 15) {
                    speed = 999999999;
                    return;
                }
                newPos.setFirst();
                var last = findLastPos();
                snakePos.remove(last);
                newLastPos = findLastPos();
                newLastPos.setLast();
//            for (Pos2d pos2d : b) {
//                if (pos2d.first) {
//                    newPos = pos2d.dir(direction2d);
//                    newPos.setFirst();
//                }
//                if (pos2d.last) {
//                    snakePos.remove(pos2d);
//                    newLastPos = findLastPos();
//                }
//            }

                assert newPos != null;
                snakePos.add(newPos);
                //snakePos.add(0, newLastPos);
                if (applePos.samePos(snakePos.get(snakePos.size()-1))) {
                    newApplePos();
                    var newLast = new Pos2d(newLastPos.x, newLastPos.y);
                    newLast.setLast();
                    snakePos.add(0, newLast);
                }
            }
            tick++;
        }

        private Pos2d findLastPos() {
            return snakePos.get(0);
        }

        private void newApplePos() {
            this.applePos = new Pos2d((int) (Math.random() * 15), (int) (Math.random() * 15));
            var clean = false;
            while (!clean) {
                for (var pos : snakePos) {
                    if (pos.samePos(applePos)) {
                        this.applePos = new Pos2d((int) (Math.random() * 15), (int) (Math.random() * 15));
                    }
                }

                var isC = true;
                for (var pos : snakePos) {
                    if (pos.samePos(applePos)) {
                        isC = false;
                    }
                }
                if (isC)clean=true;
            }
        }

        private static enum Direction2d {
            LEFT, RIGHT, UP, DOWN
        }

        private class Pos2d {
            private int x;
            private int y;
            private boolean last;
            private boolean first;
            public Pos2d(int x, int y) {
                this.x = x;
                this.y = y;
            }

            private boolean samePos(Pos2d pos) {
                return pos.x == this.x && pos.y == this.y;
            }

            private Pos2d dir(Direction2d direction2d) {
                return switch (direction2d) {
                    case UP -> up();
                    case DOWN -> down();
                    case LEFT -> left();
                    case RIGHT -> right();
                };
            }
            private Pos2d up() {
                return new Pos2d(x, y-1);
            }
            private Pos2d down() {
                return new Pos2d(x, y+1);
            }

            private Pos2d left() {
                return new Pos2d(x-1, y);
            }

            private Pos2d right() {
                return new Pos2d(x+1, y);
            }
            private void setLast() {
                for (Pos2d pos2d : Grid.this.snakePos) {
                    if (pos2d.last) pos2d.last = false;
                }
                this.last = true;
            }
            private void setFirst() {
                for (Pos2d pos2d : Grid.this.snakePos) {
                    if (pos2d.first) pos2d.first = false;
                }
                this.first = true;
            }
        }
    }

    @Override
    public void handleTick() {
        super.handleTick();

    }
}
