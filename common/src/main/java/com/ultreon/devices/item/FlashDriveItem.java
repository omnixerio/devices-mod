package com.ultreon.devices.item;

import com.ultreon.devices.Devices;
import com.ultreon.devices.IDeviceType;
import com.ultreon.devices.ModDeviceTypes;
import com.ultreon.devices.Reference;
import com.ultreon.devices.util.Colored;
import dev.architectury.registry.registries.RegistrarManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class FlashDriveItem extends Item implements Colored, SubItems, IDeviceType {

    private final DyeColor color;

    public FlashDriveItem(DyeColor color) {
        super(new Properties().arch$tab(Devices.TAB_DEVICE).rarity(Rarity.UNCOMMON).stacksTo(1));
        this.color = color;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltip, @NotNull TooltipFlag isAdvanced) {
        TextColor textColor = TextColor.fromRgb(this.color == DyeColor.BLACK ? 0xffffff : this.color.getTextColor());

        MutableComponent colorComponent = Component.literal(WordUtils.capitalize(this.color.getName().replace("_", " ")))
                .withStyle(style -> style.withBold(true).withColor(textColor));
        tooltip.add(Component.literal("Color: ").withStyle(ChatFormatting.GRAY).append(colorComponent));
    }

    @Override
    public NonNullList<ResourceLocation> getModels() {
        NonNullList<ResourceLocation> modelLocations = NonNullList.create();
        for (DyeColor color : DyeColor.values())
            modelLocations.add(new ResourceLocation(Reference.MOD_ID, Objects.requireNonNull(RegistrarManager.getId(this, Registries.ITEM)).getPath().substring(5) + "/" + color.getName()));
        return modelLocations;
    }

    @Override
    public DyeColor getColor() {
        return color;
    }

    @Override
    public ModDeviceTypes getDeviceType() {
        return ModDeviceTypes.FLASH_DRIVE;
    }
}
