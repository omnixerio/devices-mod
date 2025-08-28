package com.ultreon.devices.programs.gitweb.component.container;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

/**
 * @author MrCrayfish
 */
public class CraftingBox extends ContainerBox {
    public static final int HEIGHT = 68;

    public CraftingBox(ItemStack[] ingredients, ItemStack result) {
        super(0, 0, 0, 0, HEIGHT, new ItemStack(Blocks.CRAFTING_TABLE), "Crafting Table");
        this.setIngredients(ingredients);
        this.slots.add(new Slot(99, 26, result));
    }

    private void setIngredients(ItemStack[] ingredients) {
        for (int i = 0; i < ingredients.length; i++) {
            int posX = (i % 3) * 18 + 8;
            int posY = (i / 3) * 18 + 8;
            slots.add(new Slot(posX, posY, ingredients[i]));
        }
    }
}
