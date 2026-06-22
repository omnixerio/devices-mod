package dev.ultreon.devices.core;

import com.cinemamod.mcef.MCEF;
import com.cinemamod.mcef.MCEFBrowser;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.ultreon.devices.block.entity.ComputerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;
import org.lwjgl.opengl.GL11;

public class ModernLaptop extends Screen {
    private static final int BROWSER_DRAW_OFFSET = 20;
    private static ComputerBlockEntity laptop;

    private MCEFBrowser browser;

    public ModernLaptop(ComputerBlockEntity laptop) {
        super(Component.translatable("devices.modern_laptop"));
        ModernLaptop.laptop = laptop;
    }

    public static BlockPos getPos() {
        return laptop.getBlockPos();
    }

    @Override
    protected void init() {
        super.init();
        if (browser == null) {
            String url = "devices-vefi://devices/index.html";
            boolean transparent = true;
            browser = MCEF.createBrowser(url, transparent);


            browser.loadURL(url);
            resizeBrowser();
        }
    }

    private int mouseX(double x) {
        return (int) ((x - BROWSER_DRAW_OFFSET));
    }

    private int mouseY(double y) {
        return (int) ((y - BROWSER_DRAW_OFFSET));
    }

    private int scaleX(double x) {
        return (int) ((x - BROWSER_DRAW_OFFSET * 2));
    }

    private int scaleY(double y) {
        return (int) ((y - BROWSER_DRAW_OFFSET * 2));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void resizeBrowser() {
        if (width > 100 && height > 100) {
            browser.resize(width - BROWSER_DRAW_OFFSET * 2, height - BROWSER_DRAW_OFFSET * 2);
        }
    }

    @Override
    public void resize(Minecraft minecraft, int i, int j) {
        super.resize(minecraft, i, j);
        resizeBrowser();
    }

    @Override
    public void onClose() {
        browser.close();
        super.onClose();
    }

    @Override
    public void render(GuiGraphics graphics, int i, int j, float f) {
        super.render(graphics, i, j, f);
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        int textureID = browser.getRenderer().getTextureID();
        // Set nearest filtering
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        RenderSystem.setShaderTexture(0, textureID);
        Tesselator t = Tesselator.getInstance();
        BufferBuilder buffer = t.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.addVertex(BROWSER_DRAW_OFFSET, height - BROWSER_DRAW_OFFSET, 0).setUv(0.0f, 1.0f).setColor(255, 255, 255, 255);
        buffer.addVertex(width - BROWSER_DRAW_OFFSET, height - BROWSER_DRAW_OFFSET, 0).setUv(1.0f, 1.0f).setColor(255, 255, 255, 255);
        buffer.addVertex(width - BROWSER_DRAW_OFFSET, BROWSER_DRAW_OFFSET, 0).setUv(1.0f, 0.0f).setColor(255, 255, 255, 255);
        buffer.addVertex(BROWSER_DRAW_OFFSET, BROWSER_DRAW_OFFSET, 0).setUv(0.0f, 0.0f).setColor(255, 255, 255, 255);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.enableDepthTest();
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        browser.sendMousePress(mouseX(mouseX), mouseY(mouseY), button);
        browser.setFocus(true);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        browser.sendMouseRelease(mouseX(mouseX), mouseY(mouseY), button);
        browser.setFocus(true);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        browser.sendMouseMove(mouseX(mouseX), mouseY(mouseY));
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        browser.sendMouseWheel(mouseX(mouseX), mouseY(mouseY), deltaY, 0);
        return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        browser.sendKeyPress(keyCode, scanCode, modifiers);
        browser.setFocus(true);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        browser.sendKeyRelease(keyCode, scanCode, modifiers);
        browser.setFocus(true);
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (codePoint == (char) 0) return false;
        browser.sendKeyTyped(codePoint, modifiers);
        browser.setFocus(true);
        return super.charTyped(codePoint, modifiers);
    }
}
