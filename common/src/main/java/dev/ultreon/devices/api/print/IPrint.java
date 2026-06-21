package dev.ultreon.devices.api.print;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ultreon.devices.OmnixerioDevicesMod;
import dev.ultreon.devices.init.ModBlockEntities;
import dev.ultreon.devices.init.ModBlocks;
import dev.ultreon.devices.init.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntityType;
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

        ResourceLocation resourceLocation = BlockEntityType.getKey(ModBlockEntities.PAPER.get());
        if (resourceLocation == null) {
            OmnixerioDevicesMod.LOGGER.error("Failed to generate item for print: {}", print.getName());
            return new ItemStack(ModItems.PAPER.get());
        }
        blockEntityTag.putString("id", resourceLocation.toString());

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
     * Converts print into an NBT tag compound. Used for the renderer.
     *
     * @return nbt form of print
     */
    CompoundTag toTag();

    void fromTag(CompoundTag tag);

    @Environment(EnvType.CLIENT)
    Class<? extends Renderer> getRenderer();

    interface Renderer {
        default boolean render(PoseStack pose, CompoundTag data) {
            return render(pose, data, 0, 0, Direction.NORTH);
        }

        boolean render(PoseStack pose, CompoundTag data, int packedLight, int packedOverlay, Direction direction);
    }
}
