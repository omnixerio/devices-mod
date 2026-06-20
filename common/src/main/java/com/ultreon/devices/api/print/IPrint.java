package com.ultreon.devices.api.print;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.devices.init.ModBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

//printing somethings takes makes ink cartridge take damage. cartridge can only stack to one

/**
 * @author MrCrayfish
 */
public interface IPrint {
    static CompoundTag save(IPrint print) {
        CompoundTag tag = new CompoundTag();
        tag.putString("type", PrintingManager.getPrintIdentifier(print));
        tag.put("data", print.toTag());
        return tag;
    }

    @Nullable
    static IPrint load(CompoundTag tag) {
        IPrint print = PrintingManager.getPrint(tag.getString("type"));
        if (print != null) {
            print.fromTag(tag.getCompound("data"));
            return print;
        }
        return null;
    }

    static ItemStack generateItem(IPrint print) {
        CompoundTag blockEntityTag = new CompoundTag();
        blockEntityTag.put("print", save(print));

        ItemStack stack = new ItemStack(ModBlocks.PAPER.get());
        stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(blockEntityTag));

        if (print.getName() != null && !print.getName().isEmpty()) {
            stack.set(DataComponents.CUSTOM_NAME, Component.literal(print.getName()));
        }
        return stack;
    }

    String getName();

    /**
     * Gets the speed of the print. The higher the value, the longer it will take to print.
     *
     * @return the speed of this print
     */
    int speed();

    /**
     * Gets whether or not this print requires colored ink.
     *
     * @return if print requires ink
     */
    boolean requiresColor();

    /**
     * Converts print into an NBT requestData compound. Used for the renderer.
     *
     * @return nbt form of print
     */
    CompoundTag toTag();

    void fromTag(CompoundTag tag);

    @Environment(EnvType.CLIENT)
    Class<? extends Renderer> getRenderer();

    interface Renderer {
        boolean render(PoseStack pose, CompoundTag data);
    }
}
