package com.ultreon.devices.api.app.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.devices.api.app.Component;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.api.app.listener.ChangeListener;
import com.ultreon.devices.api.app.renderer.ItemRenderer;
import com.ultreon.devices.api.app.renderer.ListItemRenderer;
import com.ultreon.devices.api.utils.RenderUtil;
import com.ultreon.devices.core.Laptop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.awt.*;

/**
 * Created by Casey on 06-Aug-17.
 */
@SuppressWarnings("unused")
public abstract class ComboBox<T> extends Component {
    protected T value;
    protected boolean hovered;
    protected int width = 80;
    protected int height = 14;
    protected Layout layout;
    protected ItemRenderer<T> itemRenderer;
    protected ChangeListener<T> changeListener;
    private boolean opened = false;

    public ComboBox(int left, int top) {
        super(left, top);
    }

    public ComboBox(int left, int top, int width) {
        super(left, top);
        this.width = width;
    }

    @Override
    public void handleTick() {
        super.handleTick();
        if (opened && !Laptop.getSystem().hasContext()) {
            opened = false;
        }
    }

    @Override
    public void init(Layout layout) {
        this.layout.setBackground((graphics, mc, x, y, width, height, mouseX, mouseY, windowActive) -> graphics.fill(x, y, x + width, y + height, Color.GRAY.getRGB()));
    }

    @Override
    public void render(GuiGraphics graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        if (this.visible) {
            RenderSystem.setShaderTexture(0, Component.COMPONENTS_GUI);

            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(770, 771, 1, 0);
            RenderSystem.blendFunc(770, 771);

            Color bgColor = new Color(getColorScheme().getBackgroundColor()).brighter().brighter();
            float[] hsb = Color.RGBtoHSB(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), null);
            bgColor = new Color(Color.HSBtoRGB(hsb[0], hsb[1], 1f));
            RenderSystem.setShaderColor(bgColor.getRed() / 255f, bgColor.getGreen() / 255f, bgColor.getBlue() / 255f, 1f);

            this.hovered = isInside(mouseX, mouseY) && windowActive;
            int i = this.getHoverState(this.hovered);
            int xOffset = width - height;

            /* Corners */
            RenderUtil.drawRectWithTexture(Component.COMPONENTS_GUI, graphics, xPosition + xOffset, yPosition, 96 + i * 5, 12, 2, 2, 2, 2);
            RenderUtil.drawRectWithTexture(Component.COMPONENTS_GUI, graphics, xPosition + height - 2 + xOffset, yPosition, 99 + i * 5, 12, 2, 2, 2, 2);
            RenderUtil.drawRectWithTexture(Component.COMPONENTS_GUI, graphics, xPosition + height - 2 + xOffset, yPosition + height - 2, 99 + i * 5, 15, 2, 2, 2, 2);
            RenderUtil.drawRectWithTexture(Component.COMPONENTS_GUI, graphics, xPosition + xOffset, yPosition + height - 2, 96 + i * 5, 15, 2, 2, 2, 2);

            /* Middles */
            RenderUtil.drawRectWithTexture(Component.COMPONENTS_GUI, graphics, xPosition + 2 + xOffset, yPosition, 98 + i * 5, 12, height - 4, 2, 1, 2);
            RenderUtil.drawRectWithTexture(Component.COMPONENTS_GUI, graphics, xPosition + height - 2 + xOffset, yPosition + 2, 99 + i * 5, 14, 2, height - 4, 2, 1);
            RenderUtil.drawRectWithTexture(Component.COMPONENTS_GUI, graphics, xPosition + 2 + xOffset, yPosition + height - 2, 98 + i * 5, 15, height - 4, 2, 1, 2);
            RenderUtil.drawRectWithTexture(Component.COMPONENTS_GUI, graphics, xPosition + xOffset, yPosition + 2, 96 + i * 5, 14, 2, height - 4, 2, 1);

            /* Center */
            RenderUtil.drawRectWithTexture(Component.COMPONENTS_GUI, graphics, xPosition + 2 + xOffset, yPosition + 2, 98 + i * 5, 14, height - 4, height - 4, 1, 1);

            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

            /* Icons */
            RenderUtil.drawRectWithTexture(Component.COMPONENTS_GUI, graphics, xPosition + xOffset + 3, yPosition + 5, 111, 12, 8, 5, 8, 5);

            Color boxColor = new Color(getColorScheme().getBackgroundColor());
            Color borderColor = boxColor.darker().darker();

            /* Box */
            drawHorizontalLine(graphics, xPosition, xPosition + xOffset, yPosition, borderColor.getRGB());
            drawHorizontalLine(graphics, xPosition, xPosition + xOffset, yPosition + height - 1, borderColor.getRGB());
            drawVerticalLine(graphics, xPosition, yPosition, yPosition + height - 1, borderColor.getRGB());
            graphics.fill(xPosition + 1, yPosition + 1, xPosition + xOffset, yPosition + height - 1, boxColor.getRGB());

            if (itemRenderer != null) {
                itemRenderer.render(graphics, value, mc, x + 1, y + 1, xOffset - 1, height - 2);
            } else if (value != null) {
                RenderUtil.drawStringClipped(graphics, value.toString(), xPosition + 3, yPosition + 3, width - 15, Color.WHITE.getRGB(), true);
            }

            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }
    }

    @Override
    public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (!this.visible || !this.enabled)
            return;

        if (this.hovered && !this.opened) {
            this.opened = true;
            Laptop.getSystem().openContext(this.layout, xPosition, yPosition + 13);
        }
    }

    @Nullable
    public T getValue() {
        return value;
    }

    protected void updateValue(T newValue) {
        if (newValue != null && value != newValue) {
            if (value != null && changeListener != null) {
                changeListener.onChange(value, newValue);
            }
            value = newValue;
        }
    }

    protected boolean isInside(int mouseX, int mouseY) {
        return mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
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

    public void setItemRenderer(ItemRenderer<T> itemRenderer) {
        this.itemRenderer = itemRenderer;
    }

    public void setChangeListener(ChangeListener<T> changeListener) {
        this.changeListener = changeListener;
    }

    public void closeContext() {
        Laptop.getSystem().closeContext();
    }

    public static class List<T> extends ComboBox<T> {
        private final ItemList<T> list;
        private T selected;

        public List(int left, int top, T[] items) {
            super(left, top);
            this.list = new ItemList<>(0, 0, width, 6, false);
            this.layout = new Layout(width, getListHeight(list));
            this.setItems(items);
        }

        public List(int left, int top, int width, T[] items) {
            super(left, top, width);
            this.list = new ItemList<>(0, 0, width, 6, false);
            this.layout = new Layout(width, getListHeight(list));
            this.setItems(items);
        }

        public List(int left, int top, int comboBoxWidth, int listWidth, T[] items) {
            super(left, top, comboBoxWidth);
            this.list = new ItemList<>(0, 0, listWidth, 6, false);
            this.layout = new Layout(listWidth, getListHeight(list));
            this.setItems(items);
        }

        private static int getListHeight(ItemList<?> list) {
            int size = Math.max(1, Math.min(list.visibleItems, list.getItems().size()));
            return (list.renderer != null ? list.renderer.getHeight() : 13) * size + size + 1;
        }

        private static int getListWidth(ItemList<?> list) {
            return list.width + 15;
        }

        @Override
        public void init(Layout layout) {
            super.init(layout);
            list.setItemClickListener((t, index, mouseButton) ->
            {
                if (mouseButton == 0) {
                    selected = t;
                    updateValue(t);
                    Laptop.getSystem().closeContext();
                }
            });
            this.layout.addComponent(list);
        }

        public void setItems(T[] items) {
            if (items == null)
                throw new IllegalArgumentException("Cannot set null items");

            list.removeAll();
            for (T t : items) {
                list.addItem(t);
            }
            if (items.length > 0) {
                selected = list.getItem(0);
                updateValue(selected);
            }
            layout.height = getListHeight(list);
        }

        public T getSelectedItem() {
            return selected;
        }

        public void setSelectedItem(int index) {
            T t = list.getItem(index);
            if (t != null) {
                selected = t;
                updateValue(t);
            }
        }

        public void setSelectedItem(T t) {
            if (list.getItems().stream().anyMatch(i -> i == t)) {
                list.setSelectedIndex(list.getItems().indexOf(t));
                selected = t;
                updateValue(t);
            }
        }

        public void setListItemRenderer(ListItemRenderer<T> renderer) {
            list.setListItemRenderer(renderer);
            layout.height = getListHeight(list);
        }
    }

    public static class Custom<T> extends ComboBox<T> {
        public Custom(int left, int top, int width, int contextWidth, int contextHeight) {
            super(left, top);
            this.width = width;
            this.layout = new Layout.Context(contextWidth, contextHeight);
        }

        public Layout.Context getLayout() {
            return (Layout.Context) layout;
        }

        public void setValue(@NotNull T newVal) {
            updateValue(newVal);
        }
    }
}
