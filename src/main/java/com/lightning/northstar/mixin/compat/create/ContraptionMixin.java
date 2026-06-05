package com.lightning.northstar.mixin.compat.create;

import com.lightning.northstar.accessor.NorthstarContraption;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Contraption.class)
public class ContraptionMixin implements NorthstarContraption {

    @Shadow
    protected MountedStorageManager storage;

    @Override
    public MountedStorageManager northstar$getStorage() {
        return storage;
    }

}
