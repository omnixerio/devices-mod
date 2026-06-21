package dev.ultreon.devices.util;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class InventoryUtil {
    public static int getItemAmount(Player player, Item item) {
        int amount = 0;
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && stack.getItem() == item) {
                amount += stack.getCount();
            }
        }
        return amount;
    }

    public static boolean hasItemAndAmount(Player player, Item item, int amount) {
        int count = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack != null && stack.getItem() == item) {
                count += stack.getCount();
            }
        }
        return amount <= count;
    }

    public static boolean removeItemWithAmount(Player player, Item item, int amount) {
        if (hasItemAndAmount(player, item, amount)) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (!stack.isEmpty() && stack.getItem() == item) {
                    if (amount - stack.getCount() < 0) {
                        stack.shrink(amount);
                        return true;
                    } else {
                        amount -= stack.getCount();
                        player.getInventory().items.set(i, ItemStack.EMPTY);
                        if (amount == 0) return true;
                    }
                }
            }
        }
        return false;
    }
}
