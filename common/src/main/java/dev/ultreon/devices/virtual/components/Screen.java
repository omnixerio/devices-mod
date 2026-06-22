package dev.ultreon.devices.virtual.components;

import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

@ApiStatus.Experimental
public class Screen implements MemoryDevice {
    private static final int COMMAND = 0;
    private static final int ATTACHMENT_POINTER = Short.BYTES;
    private static final int ATTACHMENT_SIZE = Short.BYTES + Integer.BYTES;
    private static final int MAX = Short.BYTES + Integer.BYTES + Integer.BYTES + 64;

    public static final short COMMAND_FILL = 0x0001;
    public static final short COMMAND_BLIT = 0x0002;
    public static final short COMMAND_OUTLINE = 0x0003;

    private int screenWidth;
    private int screenHeight;

    private ByteBuffer screenBuffer;
    private ByteBuffer screenBackBuffer;

    private final Memory memory;
    private final ByteBuffer commandData = MemoryUtil.memAlloc(
            Short.BYTES // Command
            + Integer.BYTES // Attachment Pointer
            + Integer.BYTES // Attachment Size
            + 64 // Reserved
    );

    private byte brightness;
    private final CopyOnWriteArrayList<Task> tasks = new CopyOnWriteArrayList<>();

    public Screen(Memory memory) {
        this.memory = memory;
    }

    @Override
    public byte read(int address) {
        return brightness;
    }

    @Override
    public void write(int address, byte value) {
        if (address >= 0 && address <= MAX) {
            commandData.put(address, value);
        } else if (MAX + 1 == address) {
            executeCommand();
        }
    }

    private void executeCommand() {
        short command = commandData.getShort(COMMAND);
        if (command == COMMAND_FILL) {
            int anInt = commandData.getInt(ATTACHMENT_POINTER);
            int x = memory.getInt(anInt);
            int y = memory.getInt(anInt + Integer.BYTES);
            int width = memory.getInt(anInt + Integer.BYTES + Integer.BYTES);
            int height = memory.getInt(anInt + Integer.BYTES + Integer.BYTES + Integer.BYTES);
            int color = memory.getInt(anInt + Integer.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES);

            screenBackBuffer.position(y * screenWidth + x);
            int[] pixels = new int[width];
            Arrays.fill(pixels, color);
            for (int i = 0; i < height; i++) {
                screenBackBuffer.position(y * screenWidth + x + i * screenWidth);
                IntBuffer intBuffer = screenBackBuffer.asIntBuffer();
                intBuffer.put(pixels);
            }
        } else if (command == COMMAND_BLIT) {
            int x = memory.getInt(commandData.getInt(ATTACHMENT_POINTER));
            int y = memory.getInt(commandData.getInt(ATTACHMENT_POINTER + Integer.BYTES));
            int width = memory.getInt(commandData.getInt(ATTACHMENT_POINTER + Integer.BYTES + Integer.BYTES));
            int height = memory.getInt(commandData.getInt(ATTACHMENT_POINTER + Integer.BYTES + Integer.BYTES + Integer.BYTES));

            byte[] pixels = new byte[width * height];
            for (int i = 0; i < height; i++) {
                memory.read(pixels, commandData.getInt(ATTACHMENT_POINTER + Integer.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES) + i * width);
                screenBackBuffer.position(y * screenWidth + x + i * screenWidth);
                screenBackBuffer.put(pixels, i * width, width);
            }
        }
        memory.read(command);
    }

    public interface Task {
        boolean execute(ByteBuffer buffer, Screen screen);
    }

}