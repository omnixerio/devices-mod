package dev.ultreon.devices.block.computer;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

class FakeBlockPlaceContext extends BlockPlaceContext {
    protected FakeBlockPlaceContext(Level level, InteractionHand interactionHand, ItemStack itemStack, BlockHitResult blockHitResult) {
        super(level, null, interactionHand, itemStack, blockHitResult);
    }
}
