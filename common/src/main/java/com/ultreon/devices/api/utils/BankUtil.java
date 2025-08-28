package com.ultreon.devices.api.utils;

import com.ultreon.devices.api.WorldSavedData;
import com.ultreon.devices.api.task.Callback;
import com.ultreon.devices.api.task.TaskManager;
import com.ultreon.devices.programs.system.object.Account;
import com.ultreon.devices.programs.system.task.TaskAdd;
import com.ultreon.devices.programs.system.task.TaskGetBalance;
import com.ultreon.devices.programs.system.task.TaskPay;
import com.ultreon.devices.programs.system.task.TaskRemove;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <p>The Bank is a built in currency system that you can use in your application.
 * You should definitely use this instead of writing your own as this would allow
 * people to use the same currency across applications, not just your own.</p>
 *
 * <p>Please keep in mind that 1 unit equals 1 emerald. Players can withdraw currency
 * from their account into emeralds. Be nice, and don't abuse this system. Check out
 * the example applications to learn how you use this currency system.</p>
 *
 * @author MrCrayfish
 */
public class BankUtil implements WorldSavedData {
    public static final BankUtil INSTANCE = new BankUtil();

    private final Map<UUID, Account> uuidToAccount = new HashMap<UUID, Account>();

    private BankUtil() {
    }

    /**
     * Sends a request to get the balance of this user's account. To actually get
     * the balance, you need to implement a {@link Callback}
     * and get the integer with the key "balance" from the NBT parameter.
     *
     * @param callback he callback object to processing the response
     */
    public static void getBalance(Callback<CompoundTag> callback) {
        TaskManager.sendTask(new TaskGetBalance().setCallback(callback));
    }

    /**
     * <p>Sends a request for the user to pay x amount from their account. Use the callback
     * to check if payment was successful. You will also get returned their new balance. Use
     * the key "balance" to an integer from the NBT parameter in callback.</p>
     *
     * @param uuid     the UUID of the player you want to pay
     * @param amount   the amount to pay
     * @param callback the callback object to processing the response
     */
    public static void pay(String uuid, int amount, Callback<CompoundTag> callback) {
        TaskManager.sendTask(new TaskPay().setCallback(callback));
    }

    /**
     * <p>Sends a request to add x amount to the user's account. Use the callback
     * to check if addition was successful. You will also get returned their new balance. Use
     * the key "balance" to an integer from the NBT parameter in callback.</p>
     *
     * @param callback he callback object to processing the response
     */
    public static void add(int amount, Callback<CompoundTag> callback) {
        TaskManager.sendTask(new TaskAdd(amount).setCallback(callback));
    }

    /**
     * <p>Sends a request to remove x amount to the user's account. Use the callback
     * to check if removal was successful. You will also get returned their new balance. Use
     * the key "balance" to an integer from the NBT parameter in callback.</p>
     *
     * @param callback he callback object to processing the response
     */
    public static void remove(int amount, Callback<CompoundTag> callback) {
        TaskManager.sendTask(new TaskRemove(amount).setCallback(callback));
    }

    //TODO: Make private. Only the bank application should have access to these.

    public Account getAccount(Player player) {
        if (!uuidToAccount.containsKey(player.getUUID())) {
            uuidToAccount.put(player.getUUID(), new Account(0));
        }
        return uuidToAccount.get(player.getUUID());
    }

    public Account getAccount(UUID uuid) {
        return uuidToAccount.get(uuid);
    }

    public void save(CompoundTag tag) {
        ListTag accountList = new ListTag();
        for (UUID uuid : uuidToAccount.keySet()) {
            CompoundTag accountTag = new CompoundTag();
            Account account = uuidToAccount.get(uuid);
            accountTag.putString("uuid", uuid.toString());
            accountTag.putInt("balance", account.getBalance());
            accountList.add(accountTag);
        }
        tag.put("accounts", accountList);
    }

    public void load(CompoundTag tag) {
        ListTag accountList = (ListTag) tag.get("accounts");
        for (int i = 0; i < accountList.size(); i++) {
            CompoundTag accountTag = accountList.getCompound(i);
            UUID uuid = UUID.fromString(accountTag.getString("uuid"));
            Account account = new Account(accountTag.getInt("balance"));
            uuidToAccount.put(uuid, account);
        }
    }
}