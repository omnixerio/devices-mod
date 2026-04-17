package dev.ultreon.devices.client;

import dev.ultreon.devices.client.gui.ClientWindow;
import dev.ultreon.devices.core.laptop.common.ServerboundUpdatePacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
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
    private final BitSet usedIds = new BitSet();
    private final Map<Integer, ClientWindow> windowById = new HashMap<>();
    private final List<ClientWindow> windows = new ArrayList<>();
    private final List<ClientWindow> topMostWindows = new ArrayList<>();
    private final List<ClientWindow> bottomMostWindows = new ArrayList<>();

    public ClientLaptop(UUID uuid) {
        laptops.put(uuid, this);
    }

    public void handlePacket(String type, CompoundTag nbt) {
        if (type.equals("createWindow")) {
            int id = usedIds.nextClearBit(0);
            windowById.put(id, new ClientWindow(id, nbt.getStringOr("title", "Window"), nbt.getIntOr("x", 0), nbt.getIntOr("y", 0), nbt.getIntOr("width", 0), nbt.getIntOr("height", 0), this));
            usedIds.set(id);
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
        if (type.equals("addButton")) {
            Optional<Integer> id = nbt.getInt("id");
            if (id.isEmpty()) return;

            ClientWindow window = windowById.get(id.get());
            window.addButton(nbt);

            CompoundTag data = new CompoundTag();
            data.putInt("id", id.get());
            sendPacket("buttonAdded", data);
        }
    }

    public void sendPacket(String type, CompoundTag nbt) {
        System.out.printf("Sending packet %s, %s%n", type, nbt);
        ClientPlayNetworking.send(new ServerboundUpdatePacket(this.uuid, type, nbt));
    }

    public void extractRenderState(final @NotNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, float partialTicks) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, LAPTOP_GUI, 0, 0, ClientLaptop.SCREEN_WIDTH, ClientLaptop.SCREEN_HEIGHT, 10, 10, 1, 1, 256, 256);

        graphics.fill(0, 0, 10, 10, 0x2e2e2e);

        for (ClientWindow window : windows) {
            window.extractRenderState(graphics, mouseX - window.getX(), mouseY - window.getY(), partialTicks);
        }

        graphics.text(Minecraft.getInstance().font, "New Laptop System 0.01% complete", 0, 0, 0xffffff);
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
