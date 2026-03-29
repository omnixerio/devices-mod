package dev.ultreon.devices.core.laptop.client;

import dev.ultreon.devices.Reference;
import dev.ultreon.devices.debug.DebugLog;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;


public class ClientLaptopScreen extends Screen {
    static final Identifier LAPTOP_GUI = Identifier.fromNamespaceAndPath(Reference.MOD_ID, "textures/gui/laptop.png");
    private static final int BORDER = 10;
    private final ClientLaptop laptop;


    public ClientLaptopScreen(ClientLaptop laptop) {
        super(Component.translatable(laptop.toString()));
        this.laptop = laptop;
    }

    public void renderBezels(final @NotNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, float partialTicks) {
        this.extractBackground(graphics, mouseX, mouseY, partialTicks);

        //*************************//
        //     Physical Screen     //
        //*************************//
        int posX = (width - ClientLaptop.DEVICE_WIDTH) / 2;
        int posY = (height - ClientLaptop.DEVICE_HEIGHT) / 2;

        // Corners
        graphics.blit(RenderPipelines.GUI_TEXTURED, LAPTOP_GUI, posX, posY, 0, 0, BORDER, BORDER, 256, 256); // TOP-LEFT
        graphics.blit(RenderPipelines.GUI_TEXTURED, LAPTOP_GUI, posX + ClientLaptop.DEVICE_WIDTH - BORDER, posY, 11, 0, BORDER, BORDER, 256, 256); // TOP-RIGHT
        graphics.blit(RenderPipelines.GUI_TEXTURED, LAPTOP_GUI, posX + ClientLaptop.DEVICE_WIDTH - BORDER, posY + ClientLaptop.DEVICE_HEIGHT - BORDER, 11, 11, BORDER, BORDER, 256, 256); // BOTTOM-RIGHT
        graphics.blit(RenderPipelines.GUI_TEXTURED, LAPTOP_GUI, posX, posY + ClientLaptop.DEVICE_HEIGHT - BORDER, 0, 11, BORDER, BORDER, 256, 256); // BOTTOM-LEFT

        // Edges
        graphics.blit(RenderPipelines.GUI_TEXTURED, LAPTOP_GUI, posX + BORDER, posY, ClientLaptop.SCREEN_WIDTH, BORDER, 10, 0, 1, BORDER, 256, 256); // TOP
        graphics.blit(RenderPipelines.GUI_TEXTURED, LAPTOP_GUI, posX + ClientLaptop.DEVICE_WIDTH - BORDER, posY + BORDER, BORDER, ClientLaptop.SCREEN_HEIGHT, 11, 10, BORDER, 1, 256, 256); // RIGHT
        graphics.blit(RenderPipelines.GUI_TEXTURED, LAPTOP_GUI, posX + BORDER, posY + ClientLaptop.DEVICE_HEIGHT - BORDER, ClientLaptop.SCREEN_WIDTH, BORDER, 10, 11, 1, BORDER, 256, 256); // BOTTOM
        graphics.blit(RenderPipelines.GUI_TEXTURED, LAPTOP_GUI, posX, posY + BORDER, BORDER, ClientLaptop.SCREEN_HEIGHT, 0, 11, BORDER, 1, 256, 256); // LEFT

        // Center
        graphics.blit(RenderPipelines.GUI_TEXTURED, LAPTOP_GUI, posX + BORDER, posY + BORDER, ClientLaptop.SCREEN_WIDTH, ClientLaptop.SCREEN_HEIGHT, 10, 10, 1, 1, 256, 256);

    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int posX = (width - ClientLaptop.DEVICE_WIDTH) / 2 + BORDER;
        int posY = (height - ClientLaptop.DEVICE_HEIGHT) / 2 + BORDER;
        super.extractRenderState(graphics, mouseX, mouseY, a);
        renderBezels(graphics, mouseX, mouseY, a);
        graphics.pose().translate(posX, posY);
        laptop.render(graphics, mouseX-posX, mouseY-posY, a);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        int posX = (width - ClientLaptop.DEVICE_WIDTH) / 2 + BORDER;
        int posY = (height - ClientLaptop.DEVICE_HEIGHT) / 2 + BORDER;
        super.mouseMoved(mouseX, mouseY);
        laptop.mouseMoved(mouseX-posX, mouseY-posY);
        DebugLog.log(Arrays.toString(laptop.square));
    }
}
