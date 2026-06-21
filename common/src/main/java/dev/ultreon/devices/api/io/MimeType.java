package dev.ultreon.devices.api.io;

import net.minecraft.nbt.CompoundTag;

public record MimeType(String type, String subType) {
    public static final MimeType TEXT_PLAIN = new MimeType("text", "plain");
    public static final MimeType APPLICATION_JSON = new MimeType("application", "json");
    public static final MimeType APPLICATION_XML = new MimeType("application", "xml");
    public static final MimeType APPLICATION_OCTET_STREAM = new MimeType("application", "octet-stream");
    public static final MimeType TEXT_NOTE_STASH = new MimeType("text", "note-stash");
    public static final MimeType IMAGE_MC_IMG = new MimeType("image", "mc-img");

    public static MimeType of(CompoundTag mimeType) {
        return new MimeType(mimeType.getString("type"), mimeType.getString("subType"));
    }

    public CompoundTag toNbt() {
        CompoundTag mimeType = new CompoundTag();
        mimeType.putString("type", type);
        mimeType.putString("subType", subType);
        return mimeType;
    }
}
