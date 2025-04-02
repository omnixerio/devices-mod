package com.ultreon.devices.programs.terminal;

import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.core.Laptop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.function.Consumer;

public class TerminalLayout extends Layout {
    private String[] keyCodes = null;
    private char[][] matrix;
    private int cursorX, cursorY;
    private int lock = -1;
    private Consumer<String> inputHandler;
    private Consumer<String> oldInputHandler;

    public TerminalLayout() {
        super();
    }

    @Override
    public void init() {
        super.init();

        cursorX = 0;
        cursorY = 0;
        lock = -1;
        matrix = new char[20][50];
        this.height = matrix.length * 8 + 16;
        this.width = matrix[0].length * 8 + 16;
    }

    private void inputHandler(String code) {
        if (code.startsWith("PRESSED:")) {
            System.out.println(code);

            switch (code) {
                case "PRESSED:ENTER" -> {
                    newLine();
                    inputHandler = oldInputHandler;
                }
                case "PRESSED:BACKSPACE" -> backspace();
                case "PRESSED:LEFT" -> {
                    cursorX--;
                    if (cursorX < 0 || cursorX < lock) {
                        cursorX++;
                    }
                }
                case "PRESSED:RIGHT" -> {
                    if (getchr(cursorX, cursorY) != 0) {
                        cursorX++;
                    }
                }
            }
        } else if (code.startsWith("TYPED:")) {
            char c = code.charAt(6);
            print(c, cursorX, cursorY);
        }
    }

    @Override
    public void render(GuiGraphics graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        super.render(graphics, laptop, mc, x, y, mouseX, mouseY, windowActive, partialTicks);

        if (keyCodes == null) {
            keyCodes = new String[GLFW.GLFW_KEY_LAST];
            for (int i = 0; i < GLFW.GLFW_KEY_LAST; i++) {
                keyCodes[i] = GLFW.glfwGetKeyName(i, 0);
            }
        }

        graphics.fill(0, 0, x + width, y + height, 0xff000000);
        graphics.fill(x, y, x + width, y + height, 0xff000000);

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                char c = matrix[i][j];
                if (c != 0) {
                    graphics.drawString(mc.font, String.valueOf(c), x + j * 8 + 8 - Minecraft.getInstance().font.width(String.valueOf(c)) / 2, y + i * 8 + 4, 0xFFFFFF);
                }

                if (j == cursorX && i == cursorY) {
                    graphics.drawString(mc.font, "_", x + j * 8 + 8 - Minecraft.getInstance().font.width(String.valueOf("_")) / 2, y + i * 8 + 4, 0xFFFFFF);
                }
            }
        }
    }

    public void print(char c, int x, int y) {
        cursorX = x;
        cursorY = y;

        for (int i = matrix[0].length - 2; i >= x; i--) {
            matrix[y][i + 1] = matrix[y][i];
        }

        matrix[y][x] = c;

        forward();
    }

    private void forward() {
        cursorX++;
        if (cursorX == matrix[0].length) {
            cursorX = 0;
            cursorY++;

            if (cursorY == matrix.length) {
                cursorY--;
                scroll();
            }
        }
    }

    public void print(String s, int x, int y) {
        cursorX = x;
        cursorY = y;
        for (int i = 0; i < s.length(); i++) {
            matrix[y][x] = s.charAt(i);
            x++;
            if (x == matrix[0].length) {
                x = 0;
                y++;

                if (y == matrix.length) {
                    y--;
                    scroll();
                }
            }

            cursorX = x;
            cursorY = y;
        }
    }

    public void putchr(char c, int x, int y) {
        matrix[y][x] = c;
    }

    public char getchr(int x, int y) {
        if (x < 0 || x >= matrix[0].length || y < 0 || y >= matrix.length) {
            return 0;
        }
        return matrix[y][x];
    }

    public void putstr(String s, int x, int y) {
        for (int i = 0; i < s.length(); i++) {
            matrix[y][x] = s.charAt(i);
            x++;

            if (x == matrix[0].length) {
                x = 0;
                y++;

                if (y == matrix.length) {
                    y--;
                    scroll();
                }
            }
        }
    }

    public String getstr(int x, int y, int length) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < length; i++) {
            s.append(matrix[y][x]);
            x++;

            if (x == matrix[0].length) {
                x = 0;
                y++;

                if (y == matrix.length) {
                    y--;
                    scroll();
                }
            }
        }

        return s.toString();
    }

    public void backspace() {
        if (cursorX > 0 && cursorX >= lock) {
            for (int i = cursorX - 1; i < matrix[0].length - 1; i++) {
                putchr(getchr(i + 1, cursorY), i, cursorY);
            }
            cursorX--;
        }
    }

    public void setLocation(int x, int y) {
        cursorX = x;
        cursorY = y;
    }

    public void setLock(int lock) {
        this.lock = lock;
    }

    public int getLock() {
        return lock;
    }

    public void resetLock() {
        this.lock = -1;
    }

    public void interceptInput(Consumer<String> handler) {
        this.inputHandler = handler;
    }

    public void scroll() {
        for (int i = 0; i < matrix.length - 1; i++) {
            matrix[i] = matrix[i + 1];
        }

        Arrays.fill(matrix[matrix.length - 1], (char) 0);
    }

    public void clear() {
        for (char[] chars : matrix) {
            Arrays.fill(chars, (char) 0);
        }
    }

    public void clear(int x, int y) {
        matrix[y][x] = 0;
    }

    public void input(String s) {
        print(s, cursorX, cursorY);
        lock = cursorX;
        oldInputHandler = inputHandler;
        inputHandler = this::inputHandler;
    }

    @Override
    public void handleKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (inputHandler != null) {
            inputHandler.accept("PRESSED:" + getKey(keyCode, modifiers));
        } else {
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                backspace();
            }

            if (keyCode == GLFW.GLFW_KEY_ENTER) {
                newLine();
            }

            if (keyCode == GLFW.GLFW_KEY_LEFT) {
                if (cursorX > 0 && cursorX > lock) {
                    cursorX--;
                }
            }

            if (keyCode == GLFW.GLFW_KEY_RIGHT) {
                if (getchr(cursorX, cursorY) != 0) {
                    cursorX++;
                }
            }
        }
    }

    private void newLine() {
        cursorY++;
        cursorX = 0;
        if (cursorY == matrix.length) {
            cursorY--;
            scroll();
        }
    }

    public void handleKeyReleased(int keyCode, int scanCode, int modifiers) {
        if (inputHandler != null) {
            inputHandler.accept("RELEASED:" + getKey(keyCode, modifiers));
        }
    }

    @Override
    public void handleCharTyped(char codePoint, int modifiers) {
        if (inputHandler != null) {
            inputHandler.accept("TYPED:" + codePoint);
        } else {
            print(codePoint, cursorX, cursorY);
        }
    }

    private String getKey(int keyCode, int modifiers) {
        int glfwModShift = GLFW.GLFW_MOD_SHIFT;
        int glfwModControl = GLFW.GLFW_MOD_CONTROL;
        int glfwModAlt = GLFW.GLFW_MOD_ALT;

        if ((modifiers & glfwModShift) == glfwModShift) {
            return "SHIFT:" + keyCodes[keyCode];
        } else if ((modifiers & glfwModControl) == glfwModControl) {
            return "CTRL:" + keyCodes[keyCode];
        } else if ((modifiers & glfwModAlt) == glfwModAlt) {
            return "ALT:" + keyCodes[keyCode];
        } else {
            return ":" + keyCodes[keyCode];
        }
    }
}
