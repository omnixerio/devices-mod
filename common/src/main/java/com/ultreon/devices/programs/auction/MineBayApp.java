package com.ultreon.devices.programs.auction;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.devices.api.app.Application;
import com.ultreon.devices.api.app.Dialog;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.api.app.component.Button;
import com.ultreon.devices.api.app.component.Label;
import com.ultreon.devices.api.app.component.*;
import com.ultreon.devices.api.app.renderer.ListItemRenderer;
import com.ultreon.devices.api.task.TaskManager;
import com.ultreon.devices.api.utils.BankUtil;
import com.ultreon.devices.api.utils.RenderUtil;
import com.ultreon.devices.programs.auction.object.AuctionItem;
import com.ultreon.devices.programs.auction.task.TaskAddAuction;
import com.ultreon.devices.programs.auction.task.TaskBuyItem;
import com.ultreon.devices.programs.auction.task.TaskGetAuctions;
import com.ultreon.devices.programs.system.layout.StandardLayout;
import com.ultreon.devices.util.TimeUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import org.jetbrains.annotations.Nullable;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class MineBayApp extends Application {
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    private static final ResourceLocation MINEBAY_ASSETS = new ResourceLocation("devices:textures/gui/minebay.png");

    private static final ItemStack EMERALD = new ItemStack(Items.EMERALD);

    private final String[] categories = {"Building", "Combat", "Tools", "Food", "Materials", "Redstone", "Alchemy", "Rare", "Misc"};

    private Layout layoutMyAuctions;
    private ItemList<AuctionItem> items;

    /* Add Item Layout */
    private Layout layoutSelectItem;
    private Inventory inventory;
    private Button buttonAddCancel;
    private Button buttonAddNext;

    /* Set Amount and Price Layout */
    private Layout layoutAmountAndPrice;
    private Label labelAmount;
    private NumberSelector selectorAmount;
    private Label labelPrice;
    private NumberSelector selectorPrice;
    private Button buttonAmountAndPriceBack;
    private Button buttonAmountAndPriceCancel;
    private Button buttonAmountAndPriceNext;

    /* Set Duration Layout */
    private Layout layoutDuration;
    private Label labelHours;
    private Label labelMinutes;
    private Label labelSeconds;
    private NumberSelector selectorHours;
    private NumberSelector selectorMinutes;
    private NumberSelector selectorSeconds;
    private Button buttonDurationBack;
    private Button buttonDurationCancel;
    private Button buttonDurationAdd;

    public MineBayApp() {
        //super(Reference.MOD_ID + "MineBay", "MineBay");
    }

    @Override
    public void onTick() {
        super.onTick();
        AuctionManager.INSTANCE.tick();
    }

    @Override
    public void init(@Nullable CompoundTag intent) {
        var layoutMain = new StandardLayout(ChatFormatting.BOLD + "Icons", 330, 153, this, null);
        setCurrentLayout(layoutMain);
        getCurrentLayout().setBackground((graphics, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
            graphics.fill(x, y, x + width, y + 25, Color.GRAY.getRGB());
            graphics.fill(x, y + 24, x + width, y + 25, Color.DARK_GRAY.getRGB());
            graphics.fill(x, y + 25, x + 95, y + height, Color.LIGHT_GRAY.getRGB());
            graphics.fill(x + 94, y + 25, x + 95, y + height, Color.GRAY.getRGB());

            RenderSystem.setShaderTexture(0, MINEBAY_ASSETS);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            RenderUtil.drawRectWithTexture(MINEBAY_ASSETS, graphics, x + 5, y + 6, 0, 0, 61, 11, 61, 12);
        });

        Button btnAddItem = new Button(70, 5, "Add Item");
        btnAddItem.setSize(60, 15);
        btnAddItem.setClickListener((mouseX, mouseY, mouseButton) -> setCurrentLayout(layoutSelectItem));
        layoutMain.addComponent(btnAddItem);

        Button btnViewItem = new Button(135, 5, "Your Auctions");
        btnViewItem.setSize(80, 15);
        btnViewItem.setClickListener((mouseX, mouseY, mouseButton) -> {
            assert Minecraft.getInstance().player != null;
            TaskGetAuctions task = new TaskGetAuctions(Minecraft.getInstance().player.getUUID());
            task.setCallback((nbt, success) -> {
                items.removeAll();
                for (AuctionItem item : AuctionManager.INSTANCE.getItems()) {
                    items.addItem(item);
                }
            });
            TaskManager.sendTask(task);
        });
        layoutMain.addComponent(btnViewItem);

        Label labelBalance = new Label("Balance", 295, 3);
        labelBalance.setAlignment(Label.ALIGN_RIGHT);
        layoutMain.addComponent(labelBalance);

        final Label labelMoney = new Label("$0", 295, 13);
        labelMoney.setAlignment(Label.ALIGN_RIGHT);
        labelMoney.setScale(1);
        labelMoney.setShadow(false);
        layoutMain.addComponent(labelMoney);

        Label labelCategories = new Label("Categories", 5, 29);
        labelCategories.setShadow(false);
        layoutMain.addComponent(labelCategories);

        ItemList<String> categories = new ItemList<>(5, 40, 70, 7);
        for (String category : this.categories) {
            categories.addItem(category);
        }
        layoutMain.addComponent(categories);

        Label labelItems = new Label("Items", 100, 29);
        labelItems.setShadow(false);
        layoutMain.addComponent(labelItems);

        items = new ItemList<>(100, 40, 180, 4);
        items.setListItemRenderer(new ListItemRenderer<>(20) {
            @Override
            public void render(GuiGraphics graphics, AuctionItem e, Minecraft mc, int x, int y, int width, int height, boolean selected) {
                if (selected) {
                    graphics.fill(x, y, x + width, y + height, Color.DARK_GRAY.getRGB());
                } else {
                    graphics.fill(x, y, x + width, y + height, Color.GRAY.getRGB());
                }

                RenderUtil.renderItem(graphics, x + 2, y + 2, e.getStack(), true);

                graphics.pose().pushPose();
                {
                    graphics.pose().translate(x + 24, y + 4, 0);
                    graphics.pose().scale(0.666f, 0.666f, 0);
                    graphics.drawString(mc.font, e.getStack().getDisplayName(), 0, 0, Color.WHITE.getRGB(), false);
                    graphics.drawString(mc.font, TimeUtil.getTotalRealTime(e.getTimeLeft()), 0, 11, Color.LIGHT_GRAY.getRGB(), false);
                }
                graphics.pose().popPose();

                String price = "$" + e.getPrice();
                graphics.drawString(mc.font, price, x - mc.font.width(price) + width - 5, y + 6, Color.YELLOW.getRGB(), false);
            }
        });
        layoutMain.addComponent(items);

        Button btnBuy = new Button(100, 127, "Buy");
        btnBuy.setSize(50, 15);
        btnBuy.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            final Dialog.Confirmation dialog = new Dialog.Confirmation();
            dialog.setPositiveText("Buy");
            dialog.setPositiveListener((mouseX1, mouseY1, mouseButton1) -> {
                final int index = items.getSelectedIndex();
                if (index == -1) return;

                AuctionItem item = items.getItem(index);
                if (item != null) {
                    TaskBuyItem task = new TaskBuyItem(item.getId());
                    task.setCallback((nbt, success) ->
                    {
                        if (success) {
                            items.removeItem(index);
                        }
                    });
                    TaskManager.sendTask(task);
                }
            });
            dialog.setNegativeText("Cancel");
            dialog.setNegativeListener((mouseX1, mouseY1, mouseButton1) -> dialog.close());
            MineBayApp.this.openDialog(dialog);
        });
        layoutMain.addComponent(btnBuy);

        /* Select Item Layout */

        layoutSelectItem = new Layout(172, 87);
        layoutSelectItem.setTitle("Add Item");
        layoutSelectItem.setBackground((graphics, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
            graphics.fill(x, y, x + width, y + 22, Color.LIGHT_GRAY.getRGB());
            graphics.fill(x, y + 22, x + width, y + 23, Color.DARK_GRAY.getRGB());
            graphics.drawString(mc.font, "Select an Item...", x + 5, y + 7, Color.WHITE.getRGB());
        });

        inventory = new Inventory(5, 28);
        inventory.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if (inventory.getSelectedSlotIndex() != -1) {
                assert Minecraft.getInstance().player != null;
                ItemStack stack = Minecraft.getInstance().player.getInventory().getItem(inventory.getSelectedSlotIndex());
                if (!stack.isEmpty()) {
                    buttonAddNext.setEnabled(true);
                    selectorAmount.setMax(stack.getCount());
                    selectorAmount.setNumber(stack.getCount());
                } else {
                    buttonAddNext.setEnabled(false);
                }
            }
        });
        layoutSelectItem.addComponent(inventory);

        buttonAddCancel = new Button(138, 4, MINEBAY_ASSETS, 0, 12, 8, 8);
        buttonAddCancel.setToolTip("Cancel", "Go back to main page");
        buttonAddCancel.setClickListener((mouseX, mouseY, mouseButton) -> restoreDefaultLayout());
        layoutSelectItem.addComponent(buttonAddCancel);

        buttonAddNext = new Button(154, 4, MINEBAY_ASSETS, 16, 12, 8, 8);
        buttonAddNext.setToolTip("Next Page", "Set price and amount");
        buttonAddNext.setEnabled(false);
        buttonAddNext.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            selectorAmount.updateButtons();
            selectorPrice.updateButtons();
            setCurrentLayout(layoutAmountAndPrice);
        });
        layoutSelectItem.addComponent(buttonAddNext);


        /* Set Amount and Price */

        layoutAmountAndPrice = new Layout(172, 87);
        layoutAmountAndPrice.setTitle("Add Item");
        layoutAmountAndPrice.setBackground((graphics, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
            graphics.fill(x, y, x + width, y + 22, Color.LIGHT_GRAY.getRGB());
            graphics.fill(x, y + 22, x + width, y + 23, Color.DARK_GRAY.getRGB());
            graphics.drawString(mc.font, "Set amount and price...", x + 5, y + 7, Color.WHITE.getRGB());

            int offsetX = 14;
            int offsetY = 40;
            graphics.fill(x + offsetX, y + offsetY, x + offsetX + 38, y + offsetY + 38, Color.BLACK.getRGB());
            graphics.fill(x + offsetX + 1, y + offsetY + 1, x + offsetX + 37, y + offsetY + 37, Color.DARK_GRAY.getRGB());

            offsetX = 90;
            graphics.fill(x + offsetX, y + offsetY, x + offsetX + 38, y + offsetY + 38, Color.BLACK.getRGB());
            graphics.fill(x + offsetX + 1, y + offsetY + 1, x + offsetX + 37, y + offsetY + 37, Color.DARK_GRAY.getRGB());

            if (inventory.getSelectedSlotIndex() != -1) {
                assert mc.player != null;
                ItemStack stack = mc.player.getInventory().getItem(inventory.getSelectedSlotIndex());
                if (!stack.isEmpty()) {
                    graphics.pose().pushPose();
                    {
                        graphics.pose().translate(x + 17, y + 43, 0);
                        graphics.pose().scale(2, 2, 0);
                        RenderUtil.renderItem(graphics, 0, 0, stack, false);
                    }
                    graphics.pose().popPose();
                }
            }

            graphics.pose().pushPose();
            {
                graphics.pose().translate(x + 92, y + 43, 0);
                graphics.pose().scale(2, 2, 0);
                RenderUtil.renderItem(graphics, 0, 0, EMERALD, false);
            }
            graphics.pose().popPose();
        });

        buttonAmountAndPriceBack = new Button(122, 4, MINEBAY_ASSETS, 8, 12, 8, 8);
        buttonAmountAndPriceBack.setClickListener((mouseX, mouseY, mouseButton) -> setCurrentLayout(layoutSelectItem));
        layoutAmountAndPrice.addComponent(buttonAmountAndPriceBack);

        buttonAmountAndPriceCancel = new Button(138, 4, MINEBAY_ASSETS, 0, 12, 8, 8);
        buttonAmountAndPriceCancel.setClickListener((mouseX, mouseY, mouseButton) -> restoreDefaultLayout());
        layoutAmountAndPrice.addComponent(buttonAmountAndPriceCancel);

        buttonAmountAndPriceNext = new Button(154, 4, MINEBAY_ASSETS, 16, 12, 8, 8);
        buttonAmountAndPriceNext.setClickListener((mouseX, mouseY, mouseButton) -> setCurrentLayout(layoutDuration));
        layoutAmountAndPrice.addComponent(buttonAmountAndPriceNext);

        labelAmount = new Label("Amount", 16, 30);
        layoutAmountAndPrice.addComponent(labelAmount);

        selectorAmount = new NumberSelector(55, 42, 18);
        selectorAmount.setMax(64);
        layoutAmountAndPrice.addComponent(selectorAmount);

        labelPrice = new Label("Price", 96, 30);
        layoutAmountAndPrice.addComponent(labelPrice);

        selectorPrice = new NumberSelector(131, 42, 24);
        selectorPrice.setMax(999);
        layoutAmountAndPrice.addComponent(selectorPrice);


        /* Duration Layout */
        layoutDuration = new Layout(172, 87);
        layoutDuration.setTitle("Add Item");
        layoutDuration.setBackground((graphics, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
            graphics.fill(x, y, x + width, y + 22, Color.LIGHT_GRAY.getRGB());
            graphics.fill(x, y + 22, x + width, y + 23, Color.DARK_GRAY.getRGB());
            graphics.drawString(mc.font, "Set duration...", x + 5, y + 7, Color.WHITE.getRGB());
        });

        buttonDurationBack = new Button(122, 4, MINEBAY_ASSETS, 8, 12, 8, 8);
        buttonDurationBack.setClickListener((mouseX, mouseY, mouseButton) -> setCurrentLayout(layoutAmountAndPrice));
        layoutDuration.addComponent(buttonDurationBack);

        buttonDurationCancel = new Button(138, 4, MINEBAY_ASSETS, 0, 12, 8, 8);
        buttonDurationCancel.setClickListener((mouseX, mouseY, mouseButton) -> this.setCurrentLayout(layoutMain));
        layoutDuration.addComponent(buttonDurationCancel);

        buttonDurationAdd = new Button(154, 4, MINEBAY_ASSETS, 24, 12, 8, 8);
        buttonDurationAdd.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            final Dialog.Confirmation dialog = new Dialog.Confirmation();
            dialog.setMessageText("Are you sure you want to auction this item?");
            dialog.setPositiveText("Yes");
            dialog.setPositiveListener((mouseX1, mouseY1, mouseButton1) ->
            {
                int ticks = (int) TimeUtil.getRealTimeToTicks(selectorHours.getNumber(), selectorMinutes.getNumber(), selectorSeconds.getNumber());
                TaskAddAuction task = new TaskAddAuction(inventory.getSelectedSlotIndex(), selectorAmount.getNumber(), selectorPrice.getNumber(), ticks);
                task.setCallback((nbt, success) ->
                {
                    if (success) {
                        List<AuctionItem> auctionItems = AuctionManager.INSTANCE.getItems();
                        items.addItem(auctionItems.get(auctionItems.size() - 1));
                    }
                });
                TaskManager.sendTask(task);
                dialog.close();
                init(intent);
            });
            openDialog(dialog);
        });
        layoutDuration.addComponent(buttonDurationAdd);

        labelHours = new Label("Hrs", 45, 30);
        layoutDuration.addComponent(labelHours);

        labelMinutes = new Label("Mins", 76, 30);
        layoutDuration.addComponent(labelMinutes);

        labelSeconds = new Label("Secs", 105, 30);
        layoutDuration.addComponent(labelSeconds);

        DecimalFormat format = new DecimalFormat("00");

        selectorHours = new NumberSelector(45, 42, 20);
        selectorHours.setMax(23);
        selectorHours.setMin(0);
        selectorHours.setFormat(format);
        layoutDuration.addComponent(selectorHours);

        selectorMinutes = new NumberSelector(76, 42, 20);
        selectorMinutes.setMax(59);
        selectorMinutes.setMin(0);
        selectorMinutes.setFormat(format);
        layoutDuration.addComponent(selectorMinutes);

        selectorSeconds = new NumberSelector(107, 42, 20);
        selectorSeconds.setMax(59);
        selectorSeconds.setMin(1);
        selectorSeconds.setFormat(format);
        layoutDuration.addComponent(selectorSeconds);

        BankUtil.getBalance((nbt, success) ->
        {
            if (success) {
                labelMoney.setText("$" + Objects.requireNonNull(nbt, "Expected to get a tag from the get-balance task response.").getInt("balance"));
            }
        });

        TaskGetAuctions task = new TaskGetAuctions();
        task.setCallback((nbt, success) ->
        {
            items.removeAll();
            for (AuctionItem item : AuctionManager.INSTANCE.getItems()) {
                items.addItem(item);
            }
        });
        TaskManager.sendTask(task);
    }

    @Override
    public void load(CompoundTag tagCompound) {

    }

    @Override
    public void save(CompoundTag tagCompound) {

    }
}
