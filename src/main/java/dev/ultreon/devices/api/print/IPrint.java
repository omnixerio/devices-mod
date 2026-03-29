package dev.ultreon.devices.api.print;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ultreon.devices.init.DeviceBlocks;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.level.storage.ValueInput;
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
    static IPrint load(ValueInput tag) {
        IPrint print = PrintingManager.getPrint(tag.getStringOr("type", "default"));
        if (print != null) {
            print.fromTag(tag.read("data", ExtraCodecs.NBT).orElse(new CompoundTag()).asCompound().orElse(new CompoundTag()));
            return print;
        }
        return null;
    }

    static ItemStack generateItem(IPrint print) {
        CompoundTag blockEntityTag = new CompoundTag();
        blockEntityTag.put("print", save(print));

        CompoundTag itemTag = new CompoundTag();
        itemTag.put("BlockEntityTag", blockEntityTag);

        ItemStack stack = new ItemStack(DeviceBlocks.PAPER.get());
//        stack.setTag(itemTag);
//
//        if (print.getName() != null && !print.getName().isEmpty()) {
//            stack.setHoverName(Component.literal(print.getName()));
//        }
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
     * Converts print into an NBT tag compound. Used for the renderer.
     *
     * @return nbt form of print
     */
    CompoundTag toTag();

    void fromTag(CompoundTag tag);

    Class<? extends Renderer> getRenderer();

    interface Renderer {
        boolean render(GuiGraphicsExtractor graphics, PoseStack pose, CompoundTag data);
    }
}
