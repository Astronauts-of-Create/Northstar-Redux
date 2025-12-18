package com.lightning.northstar.mixin.compat.kubejs;

import com.lightning.northstar.api.WhenModLoaded;
import com.lightning.northstar.data.ModCompat;
import dev.latvian.mods.kubejs.script.data.DataPackEventJS;
import dev.latvian.mods.kubejs.script.data.VirtualKubeJSDataPack;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@WhenModLoaded(ModCompat.KJS)
@Mixin(DataPackEventJS.class)
public interface DataPackEventJsAccessor {

    @Accessor(value = "virtualDataPack", remap = false)
    VirtualKubeJSDataPack northstar$getVirtualDataPack();

    @Accessor(value = "wrappedManager", remap = false)
    MultiPackResourceManager northstar$getWrappedManager();

}
