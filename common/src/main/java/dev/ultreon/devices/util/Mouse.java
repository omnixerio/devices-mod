package dev.ultreon.devices.util;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class Mouse {
    public static int[] getEventPos() {
        double[] xPos = new double[1];
        double[] yPos = new double[1];
        GLFW.glfwGetCursorPos(Minecraft.getInstance().getWindow().getWindow(), xPos, yPos);
        return new int[]{(int) xPos[0], (int) yPos[0]};
    }

    public static int getEventX() {
        return getEventPos()[0];
    }

    public static int getEventY() {
        return getEventPos()[1];
    }
}
