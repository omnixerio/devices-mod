package com.ultreon.devices.api.app.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.devices.api.app.Component;
import com.ultreon.devices.api.app.IIcon;
import com.ultreon.devices.api.app.listener.ClickListener;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.util.GuiHelper;
import com.ultreon.devices.util.StringUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

import java.awt.*;
import java.util.Arrays;

@SuppressWarnings("unused")
public class Button extends Component {
    protected static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation("textures/gui/widgets.png");

    protected static final int TOOLTIP_DELAY = 20;

    protected String text;
    protected String toolTip, toolTipTitle;
    protected int toolTipTick;
    protected boolean hovered;

    protected int padding = 5;
    protected int width, height;
    protected boolean explicitSize = false;

    protected ResourceLocation iconResource;
    protected int iconU, iconV;
    protected int iconWidth, iconHeight;
    protected int iconSourceWidth;
    protected int iconSourceHeight;
    protected int iconUHeight = iconHeight;
    protected int iconVWidth = iconWidth;

    public void setIconU(int u, int v) {
        this.iconVWidth = u;
        this.iconUHeight = v;
    }

    protected ClickListener clickListener = null;

    /**
     * Alternate button constructor
     *
     * @param left how many pixels from the left
     * @param top  how many pixels from the top
     * @param text text to be displayed in the button
     */
    public Button(int left, int top, String text) {
        super(left, top);
        this.width = getTextWidth(text) + padding * 2;
        this.height = 16;
        this.text = text;
    }

    /**
     * Alternate button constructor
     *
     * @param left how many pixels from the left
     * @param top  how many pixels from the top
     * @param text text to be displayed in the button
     */
    public Button(int left, int top, int buttonWidth, int buttonHeight, String text) {
        super(left, top);
        this.explicitSize = true;
        this.width = buttonWidth;
        this.height = buttonHeight;
        this.text = text;
    }

    /**
     * Alternate button constructor
     *
     * @param left how many pixels from the left
     *             I	 * @param top how many pixels from the top
     * @param icon icon to be displayed in the button
     */
    public Button(int left, int top, IIcon icon) {
        super(left, top);
        this.padding = 3;
        this.width = icon.getIconSize() + padding * 2;
        this.height = icon.getIconSize() + padding * 2;
        this.setIcon(icon);
    }

    /**
     * Alternate button constructor
     *
     * @param left how many pixels from the left
     * @param top  how many pixels from the top
     * @param icon icon to be displayed in the button
     */
    public Button(int left, int top, int buttonWidth, int buttonHeight, IIcon icon) {
        super(left, top);
        this.explicitSize = true;
        this.width = buttonWidth;
        this.height = buttonHeight;
        this.setIcon(icon);
    }

    public void setIconSource(int iconSourceWidth, int iconSourceHeight) {
        this.iconSourceWidth = iconSourceWidth;
        this.iconSourceHeight = iconSourceHeight;
    }

    /**
     * Alternate button constructor
     *
     * @param left how many pixels from the left
     * @param top  how many pixels from the top
     * @param icon icon to be displayed in the button
     */
    public Button(int left, int top, String text, IIcon icon) {
        this(left, top, text);
        this.setIcon(icon);
    }

    /**
     * Alternate button constructor
     *
     * @param left how many pixels from the left
     * @param top  how many pixels from the top
     * @param icon icon to be displayed in the button
     */
    public Button(int left, int top, int buttonWidth, int buttonHeight, String text, IIcon icon) {
        super(left, top);
        this.text = text;
        this.explicitSize = true;
        this.width = buttonWidth;
        this.height = buttonHeight;
        this.setIcon(icon);
    }

    /**
     * Alternate button constructor
     *
     * @param left how many pixels from the left
     * @param top  how many pixels from the top
     */
    public Button(int left, int top, ResourceLocation iconResource, int iconU, int iconV, int iconWidth, int iconHeight) {
        super(left, top);
        this.padding = 3;
        this.setIcon(iconResource, iconU, iconV, iconWidth, iconHeight);
    }

    /**
     * Alternate button constructor
     *
     * @param left how many pixels from the left
     * @param top  how many pixels from the top
     */
    public Button(int left, int top, int buttonWidth, int buttonHeight, ResourceLocation iconResource, int iconU, int iconV, int iconWidth, int iconHeight) {
        super(left, top);
        this.explicitSize = true;
        this.width = buttonWidth;
        this.height = buttonHeight;
        this.setIcon(iconResource, iconU, iconV, iconWidth, iconHeight);
    }

    /**
     * Alternate button constructor
     *
     * @param left how many pixels from the left
     * @param top  how many pixels from the top
     */
    public Button(int left, int top, String text, ResourceLocation iconResource, int iconU, int iconV, int iconWidth, int iconHeight) {
        super(left, top);
        this.text = text;
        this.setIcon(iconResource, iconU, iconV, iconWidth, iconHeight);
    }

    /**
     * Alternate button constructor
     *
     * @param left how many pixels from the left
     * @param top  how many pixels from the top
     */
    public Button(int left, int top, int buttonWidth, int buttonHeight, String text, ResourceLocation iconResource, int iconU, int iconV, int iconWidth, int iconHeight) {
        super(left, top);
        this.text = text;
        this.explicitSize = true;
        this.width = buttonWidth;
        this.height = buttonHeight;
        this.setIcon(iconResource, iconU, iconV, iconWidth, iconHeight);
    }

    public static int getTextWidth(String text) {
        boolean flag = Minecraft.getInstance().options.forceUnicodeFont().get();
        Minecraft.getInstance().options.forceUnicodeFont().set(false);
        Font fontRenderer = Minecraft.getInstance().font;
        int width = fontRenderer.width(text);
        Minecraft.getInstance().options.forceUnicodeFont().set(flag);
        return width;
    }

    @Override
    protected void handleTick() {
        toolTipTick = hovered ? ++toolTipTick : 0;
    }

    @Override
    public void render(GuiGraphics graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        if (this.visible) {
            RenderSystem.setShaderTexture(0, Component.COMPONENTS_GUI);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

            Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getButtonColor());
            RenderSystem.setShaderColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1);

            this.hovered = GuiHelper.isMouseWithin(mouseX, mouseY, x, y, width, height) && windowActive;
            int i = this.getHoverState(this.hovered);

            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(770, 771, 1, 0);
            RenderSystem.blendFunc(770, 771);

            /* Corners */
            graphics.blit(Component.COMPONENTS_GUI, x, y, 2, 2, 96 + i * 5, 12, 2, 2, 256, 256);
            graphics.blit(Component.COMPONENTS_GUI, x + width - 2, y, 2, 2, 99 + i * 5, 12, 2, 2, 256, 256);
            graphics.blit(Component.COMPONENTS_GUI, x + width - 2, y + height - 2, 2, 2, 99 + i * 5, 15, 2, 2, 256, 256);
            graphics.blit(Component.COMPONENTS_GUI, x, y + height - 2, 2, 2, 96 + i * 5, 15, 2, 2, 256, 256);

            /* Middles */
            graphics.blit(Component.COMPONENTS_GUI, x + 2, y, width - 4, 2, 98 + i * 5, 12, 1, 2, 256, 256);
            graphics.blit(Component.COMPONENTS_GUI, x + width - 2, y + 2, 2, height - 4, 99 + i * 5, 14, 2, 1, 256, 256);
            graphics.blit(Component.COMPONENTS_GUI, x + 2, y + height - 2, width - 4, 2, 98 + i * 5, 15, 1, 2, 256, 256);
            graphics.blit(Component.COMPONENTS_GUI, x, y + 2, 2, height - 4, 96 + i * 5, 14, 2, 1, 256, 256);

            /* Center */
            graphics.blit(Component.COMPONENTS_GUI, x + 2, y + 2, width - 4, height - 4, 98 + i * 5, 14, 1, 1, 256, 256);

            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

            if (this.hovered) {
                graphics.renderOutline(x, y, width, height, Laptop.getSystem().getSettings().getColorScheme().getButtonOutlineColor());
            }

            int contentWidth = (iconResource != null ? iconWidth : 0) + getTextWidth(text);
            if (iconResource != null && !StringUtils.isNotNullOrEmpty(text)) contentWidth += 3;
            int contentX = (int) Math.ceil((width - contentWidth) / 2d);

            if (iconResource != null) {
                int iconY = (height - iconHeight) / 2;
                RenderSystem.setShaderTexture(0, iconResource);
                graphics.blit(iconResource, x + contentX, y + iconY, iconWidth, iconHeight, iconU, iconV, iconVWidth, iconUHeight, iconSourceWidth, iconSourceHeight);
            }

            if (!StringUtils.isNullOrEmpty(text)) {
                int textY = (height - mc.font.lineHeight) / 2 + 1;
                int textOffsetX = iconResource != null ? iconWidth + 3 : 0;
                int textColor = !Button.this.enabled ? 0xa0a0a0 : 0xe0e0e0;
                graphics.drawString(mc.font, text, x + contentX + textOffsetX, y + textY, textColor);
            }
        }
    }

    @Override
    public void renderOverlay(GuiGraphics graphics, Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive) {
        if (this.hovered && this.toolTip != null && toolTipTick >= TOOLTIP_DELAY) {
            laptop.renderComponentTooltip(graphics, Arrays.asList(net.minecraft.network.chat.Component.literal(this.toolTipTitle).withStyle(ChatFormatting.GOLD), net.minecraft.network.chat.Component.literal(this.toolTip)), mouseX, mouseY);
        }
    }

    public void forceClick(int mouseX, int mouseY, int mouseButton) {
        if (clickListener != null) {
            clickListener.onClick(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (!this.visible || !this.enabled)
            return;

        if (this.hovered) {
            if (clickListener != null) {
                clickListener.onClick(mouseX, mouseY, mouseButton);
            }
            playClickSound(Minecraft.getInstance().getSoundManager());
        }
    }

    /**
     * Sets the click listener. Use this to handle custom actions
     * when you press the button.
     *
     * @param clickListener the click listener
     */
    public final void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    protected int getHoverState(boolean mouseOver) {
        int i = 1;

        if (!this.enabled) {
            i = 0;
        } else if (mouseOver) {
            i = 2;
        }

        return i;
    }

    protected void playClickSound(SoundManager manager) {
        manager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
    }

    protected boolean isInside(int mouseX, int mouseY) {
        return mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
    }

    public void setSize(int width, int height) {
        this.explicitSize = true;
        this.width = width;
        this.height = height;
    }

    public void setPadding(int padding) {
        this.padding = padding;
        updateSize();
    }

    /**
     * Gets the text currently displayed in the button
     *
     * @return the button text
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text to display in the button
     *
     * @param text the text
     */
    public void setText(String text) {
        this.text = text;
        updateSize();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setIcon(ResourceLocation iconResource, int iconU, int iconV, int iconWidth, int iconHeight) {
        this.iconU = iconU;
        this.iconV = iconV;
        this.iconResource = iconResource;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
        this.iconSourceWidth = 256;
        this.iconSourceHeight = 256;
        this.iconUHeight = iconHeight;
        this.iconVWidth= iconWidth;
        updateSize();
    }

    public void setIcon(IIcon icon) {
        this.iconU = icon.getU();
        this.iconV = icon.getV();
        this.iconResource = icon.getIconAsset();
        this.iconWidth = icon.getIconSize();
        this.iconHeight = icon.getIconSize();
        this.iconSourceWidth = icon.getGridWidth() * icon.getIconSize();
        this.iconSourceHeight = icon.getGridHeight() * icon.getIconSize();
        this.iconUHeight = iconHeight;
        this.iconVWidth= iconWidth;
        updateSize();
    }

    public void removeIcon() {
        this.iconResource = null;
        updateSize();
    }

    private void updateSize() {
        if (explicitSize) return;
        int height = padding * 2;
        int width = padding * 2;

        if (iconResource != null) {
            width += iconWidth;
            height += iconHeight;
        }

        if (text != null) {
            width += getTextWidth(text);
            height = 16;
        }

        if (iconResource != null && text != null) {
            width += 3;
            height = iconHeight + padding * 2;
        }

        this.width = width;
        this.height = height;
    }

    /**
     * Displays a message when hovering the button.
     *
     * @param toolTipTitle title of the tool tip
     * @param toolTip      description of the tool tip
     */
    public void setToolTip(String toolTipTitle, String toolTip) {
        this.toolTipTitle = toolTipTitle;
        this.toolTip = toolTip;
    }

    //TODO add button text Color and button Color
}
