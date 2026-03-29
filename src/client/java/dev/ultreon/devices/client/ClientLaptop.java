package dev.ultreon.devices.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ultreon.devices.client.gui.ClientWindow;
import dev.ultreon.devices.core.laptop.common.C2SUpdatePacket;
import dev.ultreon.devices.debug.DebugLog;
import dev.ultreon.devices.network.DevicesCommonNetworker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.List;

import static dev.ultreon.devices.client.ClientLaptopScreen.LAPTOP_GUI;

public class ClientLaptop {
    public static final HashMap<UUID, ClientLaptop> laptops = new HashMap<>(); // current active client laptops
    public static final int DEVICE_HEIGHT = 216;
    public static final int SCREEN_HEIGHT = DEVICE_HEIGHT - 20;
    public static final int DEVICE_WIDTH = 384;
    public static final int SCREEN_WIDTH = DEVICE_WIDTH - 20;

    private UUID uuid;
    public final double[] square = new double[2];
    private final BitSet usedIds = new BitSet();
    private final Map<Integer, ClientWindow> windowById = new HashMap<>();
    private final List<ClientWindow> windows = new ArrayList<>();
    private final List<ClientWindow> topMostWindows = new ArrayList<>();
    private final List<ClientWindow> bottomMostWindows = new ArrayList<>();

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public ClientLaptop() {
        //super(Component.translatable("laptop")); //todo
    }

    public void handlePacket(String type, CompoundTag nbt) {
        if (type.equals("placeSquare")) {
            DebugLog.log("moving square lol");
            DebugLog.log(nbt);
            square[0] = nbt.getDoubleOr("x", 0);
            square[1] = nbt.getDoubleOr("y", 0);
            DebugLog.log("SET");
        }
        if (type.equals("createWindow")) {
            int bitIndex = usedIds.nextClearBit(0);
            windowById.put(bitIndex, new ClientWindow(nbt.getStringOr("title", "Window"), nbt.getIntOr("x", 0), nbt.getIntOr("y", 0), nbt.getIntOr("width", 0), nbt.getIntOr("height", 0)));
            usedIds.set(bitIndex);
            CompoundTag data = new CompoundTag();
            data.putInt("id", windowById.size() - 1);
            sendPacket("windowCreated", data);
        }
        if (type.equals("modifyWindow")) {
            Optional<Integer> id = nbt.getInt("id");
            if (id.isEmpty()) return;

            ClientWindow clientWindow = windowById.get(id.get());
            nbt.getString("title").ifPresent(clientWindow::setTitle);
            nbt.getInt("x").ifPresent(clientWindow::setX);
            nbt.getInt("y").ifPresent(clientWindow::setY);
            nbt.getInt("width").ifPresent(clientWindow::setWidth);
            nbt.getInt("height").ifPresent(clientWindow::setHeight);
            nbt.getBoolean("topMost").ifPresent(topMost -> {
                if (topMost) {
                    if (clientWindow.isTopMost()) return;
                    if (clientWindow.isBottomMost()) {
                        bottomMostWindows.remove(clientWindow);
                    } else {
                        windows.remove(clientWindow);
                    }
                    topMostWindows.addFirst(clientWindow);
                    clientWindow.setTopMost(true);
                    clientWindow.setBottomMost(false);
                } else {
                    if (clientWindow.isBottomMost()) {
                        bottomMostWindows.remove(clientWindow);
                    } else if (clientWindow.isTopMost()) {
                        topMostWindows.remove(clientWindow);
                    }
                    windows.addFirst(clientWindow);
                    clientWindow.setTopMost(false);
                }
            });
            nbt.getBoolean("bottomMost").ifPresent(bottomMost -> {
                if (bottomMost) {
                    if (clientWindow.isBottomMost()) return;
                    if (clientWindow.isTopMost()) {
                        topMostWindows.remove(clientWindow);
                    } else {
                        windows.remove(clientWindow);
                    }
                    bottomMostWindows.addFirst(clientWindow);
                    clientWindow.setBottomMost(true);
                    clientWindow.setTopMost(false);
                } else {
                    if (clientWindow.isTopMost()) {
                        topMostWindows.remove(clientWindow);
                    } else if (clientWindow.isBottomMost()) {
                        bottomMostWindows.remove(clientWindow);
                    }
                    windows.addFirst(clientWindow);
                    clientWindow.setBottomMost(false);
                }
            });
            sendPacket("windowModified", nbt);
        }
        if (type.equals("closeWindow")) {
            Optional<Integer> id = nbt.getInt("id");
            if (id.isEmpty()) return;

            ClientWindow remove = windowById.remove(id.get());
            usedIds.clear(id.get());
            windows.remove(remove);
            topMostWindows.remove(remove);
            bottomMostWindows.remove(remove);
            sendPacket("windowClosed", nbt);
        }
        if (type.equals("windowFocus")) {
            Optional<Integer> id = nbt.getInt("id");
            if (id.isEmpty()) return;

            Integer idValue = id.get();
            ClientWindow remove = windowById.get(idValue);
            if (remove.isTopMost()) {
                topMostWindows.remove(remove);
                topMostWindows.addFirst(remove);
            } else if (remove.isBottomMost()) {
                bottomMostWindows.remove(remove);
                bottomMostWindows.addFirst(remove);
            } else {
                windows.remove(remove);
                windows.addFirst(remove);
            }
            sendPacket("windowFocused", nbt);
        }
    }

    public void sendPacket(String type, CompoundTag nbt) {
        System.out.printf("Sending packet %s, %s%n", type, nbt);
        DevicesCommonNetworker.INSTANCE.sendToServer(new C2SUpdatePacket(this.uuid, type, nbt));
    }

    public void renderBezels(final @NotNull PoseStack pose, final int mouseX, final int mouseY, float partialTicks) { // no bezels

    }

    //@Override
    public void render(final @NotNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, float partialTicks) {
        double[] square = new double[2];
        Minecraft.getInstance().submit(() -> {
            square[0] = this.square[0];
            square[1] = this.square[1];
        });
        graphics.blit(RenderPipelines.GUI_TEXTURED, LAPTOP_GUI, 0, 0, ClientLaptop.SCREEN_WIDTH, ClientLaptop.SCREEN_HEIGHT, 10, 10, 1, 1, 256, 256);

        graphics.text(Minecraft.getInstance().font, "New Laptop System 0.01% complete", 0, 0, 0xffffff);
        graphics.fill(0, 0, 10, 10, 0x2e2e2e);
        taskbar.render(graphics, this, Minecraft.getInstance(), 0, SCREEN_HEIGHT - 16, mouseX, mouseY, partialTicks);
        DebugLog.log("x = " + square[0]);
        graphics.fill((int) square[0], (int) square[1], (int) square[0] + 10, (int) square[1] + 10, 0xffffff);
    }

    public void mouseMoved(double mouseX, double mouseY) {
        var nbt = new CompoundTag();
        nbt.putDouble("x", mouseX);
        nbt.putDouble("y", mouseY);
        sendPacket("mouseMoved", nbt);
    }

    public UUID getUuid() {
        return uuid;
    }
}
