package com.jab125.classloader.api;

import org.spongepowered.asm.mixin.injection.ModifyConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Execution {
    String value(); // the method to execute MUST BE ()V
}
