package dev.ultreon.devices.client.gui;

import dev.ultreon.devices.OmnixerioDevicesCommon;
import dev.ultreon.devices.client.ClientLaptop;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

public class ClientWindow implements Iterable<ClientWidget> {
    public static final Identifier WINDOW_BACKGROUND = OmnixerioDevicesCommon.id("textures/gui/window.png");

    private String title;
    private int x;
    private int y;
    private int width;
    private int height;
    private boolean topMost;
    private boolean bottomMost;
    private List<ClientWidget> widgets;
    private final int id;
    private final ClientLaptop laptop;
    private final BitSet usedIds = new BitSet();

    public ClientWindow(int id, String title, int x, int y, int width, int height, ClientLaptop laptop) {
        this.id = id;
        this.title = title;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.laptop = laptop;
    }

    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.pose().pushMatrix();
        {
            graphics.pose().translate(-x, -y);
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, WINDOW_BACKGROUND, -5, -5, width + 10, height + 10);
        }
        graphics.pose().popMatrix();
    }

    public void setTitle(String s) {
        this.title = s;
    }

    public String getTitle() {
        return title;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setX(int integer) {
        this.x = integer;
    }

    public void setY(int integer) {
        this.y = integer;
    }

    public void setWidth(int integer) {
        this.width = integer;
    }

    public void setHeight(int integer) {
        this.height = integer;
    }

    public boolean isTopMost() {
        return topMost;
    }

    public void setTopMost(boolean b) {
        this.topMost = b;
    }

    public boolean isBottomMost() {
        return bottomMost;
    }

    public void setBottomMost(boolean b) {
        this.bottomMost = b;
    }

    public void addButton(CompoundTag nbt) {
        int id = usedIds.nextClearBit(0);
        this.widgets.add(new ClientButton(id, this, nbt, laptop));
        usedIds.set(id);
    }

    public void onClick(int mouseX, int mouseY) {
        for (ClientWidget widget : widgets) {
            if (widget.visible && mouseX >= widget.x && mouseY >= widget.y && mouseX < widget.x + widget.width && mouseY < widget.y + widget.height) {
                if (widget instanceof ClientButton button) {
                    button.onClick();
                }
            }
        }
    }

    @Override
    public @NonNull Iterator<ClientWidget> iterator() {
        return widgets.iterator();
    }

    public ClientLaptop laptop() {
        return laptop;
    }

    public int id() {
        return id;
    }
}
