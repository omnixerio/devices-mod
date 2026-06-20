package com.ultreon.devices.init;

import com.ultreon.devices.Reference;
import com.ultreon.devices.crafting.MotherboardComponentRecipe;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.ArmorDyeRecipe;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

import java.util.function.Supplier;

public class ModRecipeSerializers {
    private static final DeferredRegister<RecipeSerializer<?>> REGISTER = DeferredRegister.create(Reference.MOD_ID, Registries.RECIPE_SERIALIZER);

    public static final RegistrySupplier<RecipeSerializer<MotherboardComponentRecipe>> MOTHERBOARD_COMPONENT = register("crafting_special_motherboard_component", () -> new SimpleCraftingRecipeSerializer<>(MotherboardComponentRecipe::new));

    private static <T extends RecipeSerializer<?>> RegistrySupplier<T> register(String id, Supplier<T> supplier) {
        return REGISTER.register(id, supplier);
    }

    public static void register() {
        REGISTER.register();
    }
}