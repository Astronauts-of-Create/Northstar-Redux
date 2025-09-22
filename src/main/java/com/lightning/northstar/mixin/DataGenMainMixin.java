package com.lightning.northstar.mixin;

import net.minecraft.data.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public class DataGenMainMixin {

    @Inject(method = "main",
            at = @At("RETURN"),
            require = 0, // don't fail if it's not here
            remap = false) // main method isn't obfuscated
    private static void northstar$onExit(String[] strings, CallbackInfo ci) {
        // something creates a thread pool that isn't closed and prevents the data generator from exiting
        // while this isn't the best solution it definitely works
        System.exit(0);
    }

}
