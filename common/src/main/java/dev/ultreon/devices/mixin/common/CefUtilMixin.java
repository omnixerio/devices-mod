package dev.ultreon.devices.mixin.common;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.cef.CefApp;
import org.cef.callback.CefSchemeRegistrar;
import org.cef.handler.CefAppHandlerAdapter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(targets = "com.cinemamod.mcef.CefUtil", remap = false)
public class CefUtilMixin {
    @WrapOperation(method = "init", at = @At(value = "INVOKE", target = "Lorg/cef/CefApp;startup([Ljava/lang/String;)Z", remap = false), remap = false)
    private static boolean devices$init(String[] args, Operation<Boolean> operation) {
        CefApp.addAppHandler(new CefAppHandlerAdapter(new String[]{}) {
            @Override
            public void onRegisterCustomSchemes(CefSchemeRegistrar registrar) {
                super.onRegisterCustomSchemes(registrar);
                registrar.addCustomScheme("devices-vefi", true, false, false, true, false, false, true);
            }
        });

        ArrayList<String> strings = Lists.newArrayList(args);
        strings.add("--allow-file-access-from-files");
        strings.add("--enable-dom-storage");
        strings.add("--enable-features=NetworkService,StorageService");
        return operation.call((Object) strings.toArray(new String[0]));
    }
}
