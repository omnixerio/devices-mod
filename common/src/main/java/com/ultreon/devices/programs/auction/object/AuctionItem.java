package com.ultreon.devices.programs.auction.object;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class AuctionItem {
    private final UUID id;
    private final ItemStack stack;
    private final int price;
    private long timeLeft;
    private final UUID sellerId;

    public AuctionItem(ItemStack stack, int price, long timeLeft, UUID sellerId) {
        this.id = UUID.randomUUID();
        this.stack = stack;
        this.price = price;
        this.timeLeft = timeLeft;
        this.sellerId = sellerId;
    }

    public AuctionItem(UUID id, ItemStack stack, int price, long timeLeft, UUID sellerId) {
        this.id = id;
        this.stack = stack;
        this.price = price;
        this.timeLeft = timeLeft;
        this.sellerId = sellerId;
    }

    public static AuctionItem readFromNBT(CompoundTag tag) {
        UUID id = UUID.fromString(tag.getString("id"));
        CompoundTag item = tag.getCompound("item");
        ItemStack stack = ItemStack.of(item);
        int price = tag.getInt("price");
        long timeLeft = tag.getLong("time");
        UUID sellerId = UUID.fromString(tag.getString("seller"));
        return new AuctionItem(id, stack, price, timeLeft, sellerId);
    }

    public UUID getId() {
        return id;
    }

    public ItemStack getStack() {
        return stack;
    }

    public int getPrice() {
        return price;
    }

    public UUID getSellerId() {
        return sellerId;
    }

    public boolean isValid() {
        return timeLeft > 0;
    }

    public void decrementTime() {
        if (timeLeft > 0) {
            timeLeft--;
        }
    }

    public long getTimeLeft() {
        return timeLeft;
    }

    public void setSold() {
        this.timeLeft = 0;
    }

    public void writeToNBT(CompoundTag tag) {
        tag.putString("id", id.toString());
        CompoundTag item = new CompoundTag();
        item = stack.save(item);
        tag.put("item", item);
        tag.putInt("price", price);
        tag.putLong("time", timeLeft);
        tag.putString("seller", sellerId.toString());
    }

    @Override
    public String toString() {
        return "{ " + id + ", " + stack + ", " + price + ", " + timeLeft + ", " + sellerId + " }";
    }
}
