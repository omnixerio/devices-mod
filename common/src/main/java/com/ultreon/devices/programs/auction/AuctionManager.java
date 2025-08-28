package com.ultreon.devices.programs.auction;

import com.ultreon.devices.programs.auction.object.AuctionItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AuctionManager {
    public static final AuctionManager INSTANCE = new AuctionManager();

    private final List<AuctionItem> items;

    private AuctionManager() {
        items = new ArrayList<AuctionItem>();
    }

    public void addItem(AuctionItem item) {
        if (!containsItem(item.getId())) {
            items.add(item);
        }
    }

    public boolean containsItem(UUID id) {
        for (AuctionItem item : items) {
            if (item.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public AuctionItem getItem(UUID uuid) {
        for (AuctionItem item : items) {
            if (item.getId().equals(uuid)) {
                return item;
            }
        }
        return null;
    }

    public void removeItem(UUID uuid) {
        for (AuctionItem item : items) {
            if (item.getId().equals(uuid)) {
                items.remove(item);
                return;
            }
        }
    }

    public List<AuctionItem> getItems() {
        return items;
    }

    public void tick() {
        for (AuctionItem item : items) {
            item.decrementTime();
        }
    }

    public void writeToNBT(CompoundTag tag) {
        ListTag tagList = new ListTag();
        items.stream().filter(i -> i.isValid()).forEach(i -> {
            CompoundTag itemTag = new CompoundTag();
            i.writeToNBT(itemTag);
            tagList.add(itemTag);
        });
        tag.put("auctionItems", tagList);
    }

    public void readFromNBT(CompoundTag tag) {
        items.clear();

        ListTag tagList = (ListTag) tag.get("auctionItems");
        for (int i = 0; i < tagList.size(); i++) {
            CompoundTag itemTag = tagList.getCompound(i);
            AuctionItem item = AuctionItem.readFromNBT(itemTag);
            items.add(item);
        }
    }

    public List<AuctionItem> getItemsForSeller(UUID seller) {
        return items.stream().filter(i -> i.getSellerId().equals(seller)).collect(Collectors.toList());
    }
}
