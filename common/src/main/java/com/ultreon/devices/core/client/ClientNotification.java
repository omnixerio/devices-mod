package com.ultreon.devices.core.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.ultreon.devices.api.app.IIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * @author MrCrayfish
 */
public class ClientNotification implements Toast {
    private static final Identifier TEXTURE_TOASTS = Identifier.parse("devices:textures/gui/toast.png");

    private IIcon icon;
    private String title;
    private String subTitle;

    private ClientNotification() {
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, Font font, long fullyVisibleForMs) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE_TOASTS, 0, 0, 0, 0, 160, 32, 256, 256);

        if (subTitle == null) {
            graphics.text(font, font.plainSubstrByWidth(I18n.get(title), 118), 38, 12, -1);
        } else {
            graphics.text(font, font.plainSubstrByWidth(I18n.get(title), 118), 38, 7, -1);
            graphics.text(font, font.plainSubstrByWidth(I18n.get(subTitle), 118), 38, 18, -1, false);
        }

        graphics.blit(RenderPipelines.GUI_TEXTURED, icon.getIconAsset(), 6, 6, icon.getGridWidth(), icon.getGridHeight(), icon.getU(), icon.getV(), icon.getIconSize(), icon.getIconSize(), icon.getSourceWidth(), icon.getSourceHeight());
    }

    public static ClientNotification loadFromTag(CompoundTag tag) {
        ClientNotification notification = new ClientNotification();

        int ordinal = tag.getCompoundOrEmpty("icon").getIntOr("ordinal", 0);
        String className = tag.getCompoundOrEmpty("icon").getStringOr("className", "");

        try {
            notification.icon = (IIcon) Class.forName(className).getEnumConstants()[ordinal];
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        notification.title = tag.getStringOr("title", "Title");
        if (tag.contains("subTitle", Tag.TAG_STRING)) {
            notification.subTitle = tag.getStringOr("subTitle", "Subtitle");
        }

        return notification;
    }

    public void push() {
        Minecraft.getInstance().getToastManager().addToast(this);
    }
}
