package com.ultreon.devices.datagen;

import com.ultreon.devices.OmnixerioDevicesMod;
import com.ultreon.devices.init.ModItems;
import com.ultreon.devices.init.tags.ModItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class DevicesAdvancementsProvider extends FabricAdvancementProvider {
    protected DevicesAdvancementsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {
        AdvancementHolder root = Advancement.Builder.advancement()
                .display(
                        ModItems.COMPONENT_CIRCUIT_BOARD.get(), // The display icon
                        Component.literal("Devices Mod"), // The title
                        Component.literal("Install Devices Mod"), // The description
                        ResourceLocation.withDefaultNamespace("textures/block/copper_block"), // Background image for the tab in the advancements page, if this is a root advancement (has no parent)
                        AdvancementType.TASK, // TASK, CHALLENGE, or GOAL
                        false, // Show the toast when completing it
                        false, // Announce it to chat
                        false // Hide it in the advancement tab until it's achieved
                )
                .addCriterion("has_crafting_table", InventoryChangeTrigger.TriggerInstance.hasItems(Items.CRAFTING_TABLE))
                // Give the advancement an id
                .save(consumer, OmnixerioDevicesMod.MOD_ID + "/root");

        Advancement.Builder.advancement()
                .display(
                        ModItems.COMPONENT_CIRCUIT_BOARD.get(), // The display icon
                        Component.literal("It Was All His Fault"), // The title
                        Component.literal("Create a circuit board"), // The description
                        ResourceLocation.withDefaultNamespace("textures/block/copper_block"), // Background image for the tab in the advancements page, if this is a root advancement (has no parent)
                        AdvancementType.TASK, // TASK, CHALLENGE, or GOAL
                        true, // Show the toast when completing it
                        true, // Announce it to chat
                        false // Hide it in the advancement tab until it's achieved
                )
                // "got_dirt" is the name referenced by other advancements when they want to have "requirements."
                .addCriterion("got_circuit_board", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.COMPONENT_CIRCUIT_BOARD.get()))
                .parent(root)
                // Give the advancement an id
                .save(consumer, OmnixerioDevicesMod.MOD_ID + "/get_circuit_board");

        AdvancementHolder flash = Advancement.Builder.advancement()
                .display(
                        ModItems.COMPONENT_FLASH_CHIP.get(), // The display icon
                        Component.literal("Flashy Chips"),
                        Component.literal("Create your first flash chip"), // The description
                        ResourceLocation.withDefaultNamespace("textures/block/copper_block"), // Background image for the tab in the advancements page, if this is a root advancement (has no parent)
                        AdvancementType.TASK, // TASK, CHALLENGE, or GOAL
                        true, // Show the toast when completing it
                        true, // Announce it to chat
                        false // Hide it in the advancement tab until it's achieved
                )
                // "got_dirt" is the name referenced by other advancements when they want to have "requirements."
                .addCriterion("got_flash_chip", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.COMPONENT_FLASH_CHIP.get()))
                .parent(root)
                // Give the advancement an id
                .save(consumer, OmnixerioDevicesMod.MOD_ID + "/get_flash_chip");

        AdvancementHolder motherboard = Advancement.Builder.advancement()
                .display(
                        ModItems.COMPONENT_MOTHERBOARD_FULL.get(),
                        Component.literal("Motherboard"),
                        Component.literal("Create your first motherboard"),
                        ResourceLocation.withDefaultNamespace("textures/block/copper_block"),
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("got_motherboard", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.COMPONENT_MOTHERBOARD_FULL.get()))
                .parent(root)
                .save(consumer, OmnixerioDevicesMod.MOD_ID + "/get_motherboard");

        AdvancementHolder screen = Advancement.Builder.advancement()
                .display(
                        ModItems.COMPONENT_MOTHERBOARD_FULL.get(),
                        Component.literal("Motherboard"),
                        Component.literal("Create your first motherboard"),
                        ResourceLocation.withDefaultNamespace("textures/block/copper_block"),
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("got_screen", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.COMPONENT_SCREEN.get()))
                .parent(motherboard)
                .save(consumer, OmnixerioDevicesMod.MOD_ID + "/get_motherboard");

        Advancement.Builder.advancement()
                .display(
                        ModItems.LAPTOPS.of(DyeColor.RED).get(),
                        Component.literal("Portable Computing"),
                        Component.literal("Build your first laptop"),
                        ResourceLocation.withDefaultNamespace("textures/block/copper_block"),
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("got_laptop", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(ModItemTags.LAPTOPS).build()))
                .parent(screen)
                .save(consumer, OmnixerioDevicesMod.MOD_ID + "/get_laptop");

        Advancement.Builder.advancement()
                .display(
                        ModItems.LAPTOPS.of(DyeColor.RED).get(),
                        Component.literal("Mac Max X What?"),
                        Component.literal("Build your first Mac Max X"),
                        ResourceLocation.withDefaultNamespace("textures/block/copper_block"),
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("got_mac_max", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(ModItems.MAC_MAX_X.get()).build()))
                .parent(screen)
                .save(consumer, OmnixerioDevicesMod.MOD_ID + "/get_mac_max");

        Advancement.Builder.advancement()
                .display(
                        ModItems.ROUTERS.of(DyeColor.RED).get(),
                        Component.literal("The World Wide Web"),
                        Component.literal("Build your first router"),
                        ResourceLocation.withDefaultNamespace("textures/block/copper_block"),
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("got_router", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(ModItemTags.ROUTERS).build()))
                .parent(root)
                .save(consumer, OmnixerioDevicesMod.MOD_ID + "/get_router");

        Advancement.Builder.advancement()
                .display(
                        ModItems.FLASH_DRIVE.of(DyeColor.RED).get(),
                        Component.literal("Portable Storage"),
                        Component.literal("Build your first flash drive"),
                        ResourceLocation.withDefaultNamespace("textures/block/copper_block"),
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("got_flash_drive", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(ModItemTags.FLASH_DRIVES).build()))
                .parent(flash)
                .save(consumer, OmnixerioDevicesMod.MOD_ID + "/get_flash_drive");
    }
}
