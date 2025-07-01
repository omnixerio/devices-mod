package com.jab125.classloader.impl;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class ClassScanner extends ClassVisitor {
    protected ClassScanner(int api) {
        super(api);
    }
    private boolean h;
    private String a;
    private String b;
    protected ClassScanner(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    public boolean isH() {
        return h;
    }

    public String getA() {
        return a;
    }

    public String getB() {
        return b;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if (descriptor.equals("Lcom/jab125/classloader/api/Execution;")) h = true;
        return super.visitAnnotation(descriptor, visible);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        var method = super.visitMethod(access, name, descriptor, signature, exceptions);
        return method;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);

    }
}
