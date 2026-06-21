package dev.ultreon.devices.crafting;

import dev.ultreon.devices.init.ModDataComponents;
import dev.ultreon.devices.init.ModItems;
import dev.ultreon.devices.init.ModRecipeSerializers;
import dev.ultreon.devices.item.ComponentItem;
import dev.ultreon.devices.item.MotherboardItem;
import dev.ultreon.devices.item.data.MotherboardComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MotherboardComponentRecipe extends CustomRecipe {
    public MotherboardComponentRecipe(CraftingBookCategory craftingBookCategory) {
        super(craftingBookCategory);
    }

    @Override
    public boolean matches(CraftingInput recipeInput, Level level) {
        boolean matches = false;
        MotherboardComponents motherboardComponents = null;
        boolean hasCpu = false;
        boolean hasRam = false;
        boolean hasGpu = false;
        boolean hasWifi = false;

        for (ItemStack item : recipeInput.items()) {
            if (item.getItem() instanceof MotherboardItem) {
                motherboardComponents = item.get(ModDataComponents.MOTHERBOARD_COMPONENTS.get());
                if (motherboardComponents == null) {
                    motherboardComponents = new MotherboardComponents(false, false, false, false);
                    item.set(ModDataComponents.MOTHERBOARD_COMPONENTS.get(), motherboardComponents);
                }
                matches = true;
            } else if (ComponentItem.isComponent(item)) {
                if (item.is(ModItems.COMPONENT_CPU.get())) {
                    hasCpu = true;
                } else if (item.is(ModItems.COMPONENT_RAM.get())) {
                    hasRam = true;
                } else if (item.is(ModItems.COMPONENT_GPU.get())) {
                    hasGpu = true;
                } else if (item.is(ModItems.COMPONENT_WIFI.get())) {
                    hasWifi = true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        if (!matches) return false;

        return (!hasCpu || !motherboardComponents.hasCpu()) &&
                (!hasRam || !motherboardComponents.hasRam()) &&
                (!hasGpu || !motherboardComponents.hasGpu()) &&
                (!hasWifi || !motherboardComponents.hasWifi());
    }

    @Override
    public @NotNull ItemStack assemble(CraftingInput recipeInput, HolderLookup.Provider provider) {
        boolean matches = false;
        MotherboardComponents motherboardComponents = null;
        boolean hasCpu = false;
        boolean hasRam = false;
        boolean hasGpu = false;
        boolean hasWifi = false;

        for (ItemStack item : recipeInput.items()) {
            if (item.getItem() instanceof MotherboardItem) {
                motherboardComponents = item.get(ModDataComponents.MOTHERBOARD_COMPONENTS.get());
                if (motherboardComponents == null) {
                    motherboardComponents = new MotherboardComponents(false, false, false, false);
                    item.set(ModDataComponents.MOTHERBOARD_COMPONENTS.get(), motherboardComponents);
                }
                matches = true;
            } else if (ComponentItem.isComponent(item)) {
                if (item.is(ModItems.COMPONENT_CPU.get())) {
                    hasCpu = true;
                } else if (item.is(ModItems.COMPONENT_RAM.get())) {
                    hasRam = true;
                } else if (item.is(ModItems.COMPONENT_GPU.get())) {
                    hasGpu = true;
                } else if (item.is(ModItems.COMPONENT_WIFI.get())) {
                    hasWifi = true;
                }
            }
        }

        if (motherboardComponents.hasCpu()) hasCpu = true;
        if (motherboardComponents.hasRam()) hasRam = true;
        if (motherboardComponents.hasGpu()) hasGpu = true;
        if (motherboardComponents.hasWifi()) hasWifi = true;

        MotherboardComponents newComponents = new MotherboardComponents(hasCpu, hasRam, hasGpu, hasWifi);
        if (hasCpu && hasRam && hasGpu && hasWifi)
            return new ItemStack(ModItems.COMPONENT_MOTHERBOARD_FULL.get());

        ItemStack itemStack = new ItemStack(ModItems.COMPONENT_MOTHERBOARD.get(), 1);
        itemStack.set(ModDataComponents.MOTHERBOARD_COMPONENTS.get(), newComponents);


        return itemStack;
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return i * j >= 2;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.MOTHERBOARD_COMPONENT.get();
    }
}
