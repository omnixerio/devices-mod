package dev.ultreon.devices.programs.auction.task;

import dev.ultreon.devices.UltreonDevices;
import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.programs.auction.AuctionManager;
import dev.ultreon.devices.programs.auction.object.AuctionItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TaskAddAuction extends Task {
    private int slot;
    private int amount;
    private int price;
    private int duration;

    private AuctionItem item;

    public TaskAddAuction() {
        super();
    }

    public TaskAddAuction(int slot, int amount, int price, int duration) {
        this();
        this.slot = slot;
        this.amount = amount;
        this.price = price;
        this.duration = duration;
    }

    @Override
    public void prepareRequest(HolderLookup.Provider provider, CompoundTag nbt) {
        nbt.putInt("slot", slot);
        nbt.putInt("amount", amount);
        nbt.putInt("price", price);
        nbt.putInt("duration", duration);
    }

    @Override
    public void processRequest(HolderLookup.Provider provider, CompoundTag nbt, Level level, Player player) {
        int slot = nbt.getInt("slot");
        int amount = nbt.getInt("amount");
        int price = nbt.getInt("price");
        int duration = nbt.getInt("duration");

        if (slot >= 0 && price >= 0 && slot < player.getInventory().getContainerSize()) {
            ItemStack real = player.getInventory().getItem(slot);
            if (!real.isEmpty()) {
                ItemStack stack = real.copy();
                stack.setCount(amount);
                real.shrink(amount);
                //TODO Test this

                item = new AuctionItem(stack, price, duration, player.getUUID());

                AuctionManager.INSTANCE.addItem(item);

                this.setSuccessful();
            }
        }
    }

    @Override
    public void prepareResponse(HolderLookup.Provider provider, CompoundTag nbt) {
        if (isSuccessful()) {
            item.writeToNBT(nbt, UltreonDevices.getServer().registryAccess());
        }
    }

    @Override
    public void processResponse(HolderLookup.Provider provider, CompoundTag nbt) {
        if (isSuccessful()) {
            AuctionManager.INSTANCE.addItem(AuctionItem.readFromNBT(provider, nbt));
        }
    }
}
