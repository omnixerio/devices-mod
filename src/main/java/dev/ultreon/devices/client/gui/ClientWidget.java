package dev.ultreon.devices.client.gui;

import dev.ultreon.devices.client.ClientLaptop;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public abstract class ClientWidget {
    public final int id;
    public int x;
    public int y;
    public int width;
    public int height;
    public boolean visible = true;
    public boolean isHovered;
    public boolean enabled;
    protected final ClientWindow window;
    protected final ClientLaptop laptop;

    public ClientWidget(int id, ClientWindow window, int x, int y, int width, int height, ClientLaptop laptop) {
        this.id = id;
        this.window = window;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.laptop = laptop;
    }

    public void extract(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        }
    }
}
