package com.ultreon.devices.core;

import com.ultreon.devices.Devices;
import org.apache.commons.io.input.NullInputStream;
import org.apache.commons.io.output.NullOutputStream;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.io.IOAccess;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.function.IntConsumer;

public class PyProcess extends Thread {
    private int pid;
    private final Laptop.LaptopFileSystem fs;
    private final Value modules;
    private final String init;
    private final String path;
    private final String[] args;
    private final Map<String, String> env;
    private IntConsumer onExit;
    private Context processContext;
    private final ProcessApi api = ProcessApi.of(this);

    public PyProcess(Laptop.LaptopFileSystem fs, int pid, Value modules, String init, String path, String[] args, Map<String, String> env) throws IOException {
        this.fs = fs;
        this.pid = pid;
        this.modules = modules;
        this.init = init;
        this.path = path;
        this.args = args;
        this.env = env;
    }

    public void setOnExit(IntConsumer handler) {
        this.onExit = handler;
    }

    public int getPid() {
        return pid;
    }

    public void kill(int code) {
        this.processContext.close(true);
        try {
            this.join();
        } catch (InterruptedException e) {
            this.onExit.accept(code);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        try (Context context = Context.newBuilder("python")
                .environment(env)
                .allowCreateProcess(false)
                .allowCreateThread(false)
                .allowIO(IOAccess.newBuilder().fileSystem(fs).build())
                .allowValueSharing(true)
                .allowNativeAccess(false)
                .allowPolyglotAccess(PolyglotAccess.NONE)
                .in(NullInputStream.INSTANCE)
                .out(NullOutputStream.INSTANCE)
                .err(NullOutputStream.INSTANCE)
                .allowHostClassLoading(false)
                .useSystemExit(false)
                .build()) {

            context.enter();

            Value bindings = context.getBindings("python");
            bindings.putMember("_moduleImport", this.modules);

            context.eval(Source.newBuilder("python", init, "<<process-init>>").internal(true).build());

            this.processContext = context;

            try (SeekableByteChannel seekableByteChannel = fs.newByteChannel(Path.of(path), Collections.emptySet())) {
                long size = seekableByteChannel.size();
                ByteBuffer buffer = ByteBuffer.allocate((int) size);
                seekableByteChannel.read(buffer);
                buffer.flip();
                Value python = context.eval(Source.newBuilder("python", new String(buffer.array(), StandardCharsets.UTF_8), path).encoding(StandardCharsets.UTF_8).build());
                Value execute = python.getMember("main").execute("main", args);
                int anInt = execute.asInt();
                if (anInt != 0) {
                    onExit.accept(anInt);
                }
            } catch (IOException e) {
                onExit.accept(1);
            }

            context.leave();
        } catch (IOException e) {
            Devices.LOGGER.error("ERROR:", e);
        }
    }

    public ProcessApi api() {
        return api;
    }
}
