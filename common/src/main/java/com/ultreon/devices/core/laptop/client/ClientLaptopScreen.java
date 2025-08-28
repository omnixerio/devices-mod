package com.ultreon.devices.core.laptop.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.devices.Reference;
import com.ultreon.devices.debug.DebugLog;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;


public class ClientLaptopScreen extends Screen {
    static final ResourceLocation LAPTOP_GUI = new ResourceLocation(Reference.MOD_ID, "textures/gui/laptop.png");
    private static final int BORDER = 10;
    private final ClientLaptop laptop;


    public ClientLaptopScreen(ClientLaptop laptop) {
        super(Component.translatable(laptop.toString()));
        this.laptop = laptop;
    }

    public void renderBezels(final @NotNull GuiGraphics graphics, final int mouseX, final int mouseY, float partialTicks) {
        this.renderBackground(graphics);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, LAPTOP_GUI);

        //*************************//
        //     Physical Screen     //
        //*************************//
        int posX = (width - ClientLaptop.DEVICE_WIDTH) / 2;
        int posY = (height - ClientLaptop.DEVICE_HEIGHT) / 2;

        // Corners
        graphics.blit(LAPTOP_GUI, posX, posY, 0, 0, BORDER, BORDER); // TOP-LEFT
        graphics.blit(LAPTOP_GUI, posX + ClientLaptop.DEVICE_WIDTH - BORDER, posY, 11, 0, BORDER, BORDER); // TOP-RIGHT
        graphics.blit(LAPTOP_GUI, posX + ClientLaptop.DEVICE_WIDTH - BORDER, posY + ClientLaptop.DEVICE_HEIGHT - BORDER, 11, 11, BORDER, BORDER); // BOTTOM-RIGHT
        graphics.blit(LAPTOP_GUI, posX, posY + ClientLaptop.DEVICE_HEIGHT - BORDER, 0, 11, BORDER, BORDER); // BOTTOM-LEFT

        // Edges
        graphics.blit(LAPTOP_GUI, posX + BORDER, posY, ClientLaptop.SCREEN_WIDTH, BORDER, 10, 0, 1, BORDER, 256, 256); // TOP
        graphics.blit(LAPTOP_GUI, posX + ClientLaptop.DEVICE_WIDTH - BORDER, posY + BORDER, BORDER, ClientLaptop.SCREEN_HEIGHT, 11, 10, BORDER, 1, 256, 256); // RIGHT
        graphics.blit(LAPTOP_GUI, posX + BORDER, posY + ClientLaptop.DEVICE_HEIGHT - BORDER, ClientLaptop.SCREEN_WIDTH, BORDER, 10, 11, 1, BORDER, 256, 256); // BOTTOM
        graphics.blit(LAPTOP_GUI, posX, posY + BORDER, BORDER, ClientLaptop.SCREEN_HEIGHT, 0, 11, BORDER, 1, 256, 256); // LEFT

        // Center
        graphics.blit(LAPTOP_GUI, posX + BORDER, posY + BORDER, ClientLaptop.SCREEN_WIDTH, ClientLaptop.SCREEN_HEIGHT, 10, 10, 1, 1, 256, 256);

    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int posX = (width - ClientLaptop.DEVICE_WIDTH) / 2 + BORDER;
        int posY = (height - ClientLaptop.DEVICE_HEIGHT) / 2 + BORDER;
        super.render(graphics, mouseX, mouseY, partialTick);
        renderBezels(graphics, mouseX, mouseY, partialTick);
        graphics.pose().translate(posX, posY, 0);
        laptop.render(graphics, mouseX-posX, mouseY-posY, partialTick);
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
