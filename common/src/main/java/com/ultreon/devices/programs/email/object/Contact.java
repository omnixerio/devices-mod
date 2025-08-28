package com.ultreon.devices.programs.email.object;

import net.minecraft.nbt.CompoundTag;

/**
 * @author MrCrayfish
 */
public class Contact {
    private final String nickname;
    private final String email;

    public Contact(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return nickname;
    }

    public void save(CompoundTag contactTag) {
        contactTag.putString("nickname", nickname);
        contactTag.putString("email", email);
    }
}
