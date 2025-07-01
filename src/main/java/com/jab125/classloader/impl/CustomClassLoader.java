package com.jab125.classloader.impl;

import net.fabricmc.loader.impl.FabricLoaderImpl;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class CustomClassLoader {
    private final ArrayList<String> executionClasses = new ArrayList<>();
    public ByteClassLoader cl;
    public class ByteClassLoader extends URLClassLoader {
        private final Map<String, byte[]> extraClassDefs;

        public ByteClassLoader(ClassLoader parent, Map<String, byte[]> extraClassDefs) {
            super(new URL[0], parent);
            this.extraClassDefs = new HashMap<>(extraClassDefs);
        }

        public ArrayList<String> getExecutionClasses() {
            return executionClasses;
        }

        @Override
        protected Class<?> findClass(final String name) throws ClassNotFoundException {
            byte[] classBytes = this.extraClassDefs.remove(name);
            if (classBytes != null) {
                return defineClass(name, classBytes, 0, classBytes.length);
            }
            return super.findClass(name);
        }

    }
    public void load(JarFile jar) throws IOException {
        getF(jar);
    }

    private void getF(JarFile jar) throws IOException {
        var hm = new HashMap<String, byte[]>();
        for (JarEntry jarEntry : jar.stream().toList()) {
            if (!jarEntry.isDirectory() && jarEntry.getRealName().endsWith(".class")) {
              //  DebugLog.log(jarEntry.getRealName());
                var d = jar.getInputStream(jarEntry);
                var bytes = d.readAllBytes();
                var asmVer = FabricLoaderImpl.ASM_VERSION;
                var classReader = new ClassReader(bytes);
                var classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                ClassScanner cs;
                classReader.accept(cs = new ClassScanner(asmVer, classWriter), ClassReader.EXPAND_FRAMES);
                var cn = new ClassNode(asmVer);
                classReader.accept(cn, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                hm.put(classReader.getClassName().replaceAll("/", "."), classWriter.toByteArray());
                if (cs.isH()) {
                    executionClasses.add(classReader.getClassName().replaceAll("/", "."));
                }
            }
        }

        this.cl = new ByteClassLoader(this.getClass().getClassLoader(), hm);
    }
}
