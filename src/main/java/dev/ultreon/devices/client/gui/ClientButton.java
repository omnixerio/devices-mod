package dev.ultreon.devices.client.gui;

import dev.ultreon.devices.client.ClientLaptop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.nbt.CompoundTag;

public class ClientButton extends ClientWidget {
    private final String text;

    public ClientButton(int id, ClientWindow window, CompoundTag nbt, ClientLaptop laptop) {
        super(id, window, nbt.getIntOr("X", 0), nbt.getIntOr("Y", 0), nbt.getIntOr("Width", 20), nbt.getIntOr("Height", 20), laptop);
        this.text = nbt.getStringOr("Text", "");
    }

    public void onClick() {
        CompoundTag data = new CompoundTag();
        data.putInt("id", window.id());
        data.putInt("widget", id);
        laptop.sendPacket("buttonClicked", data);
    }

    @Override
    public void extract(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;

        super.extract(graphics, mouseX, mouseY, partialTicks);

        if (this.enabled) {
            graphics.fill(x, y, x + width, y + height, this.isHovered ? 0xFF666666 : 0xFF444444);
        } else {
            graphics.fill(x, y, x + width, y + height, 0xFF111111);
        }
        graphics.text(Minecraft.getInstance().font, text, x + 2, y + 2, 0xFFFFFFFF, false);
    }
}
