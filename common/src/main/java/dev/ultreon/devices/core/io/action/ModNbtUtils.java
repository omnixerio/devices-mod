package dev.ultreon.devices.core.io.action;

import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;

import java.io.*;

public class ModNbtUtils {
    public static byte[] toBytes(Tag tag) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutput output = new DataOutputStream(bos);
        NbtIo.writeAnyTag(tag, output);
        return bos.toByteArray();
    }

    public static Tag fromBytes(byte[] bytes) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        DataInput input = new DataInputStream(bis);
        return NbtIo.readAnyTag(input, NbtAccounter.create(1048576));
    }

    public static Tag fromStream(InputStream stream) throws IOException {
        DataInput input = new DataInputStream(stream);
        return NbtIo.readAnyTag(input, NbtAccounter.create(1048576));
    }

    public static void toStream(Tag tag, OutputStream stream) throws IOException {
        DataOutput output = new DataOutputStream(stream);
        NbtIo.writeAnyTag(tag, output);
    }
}
