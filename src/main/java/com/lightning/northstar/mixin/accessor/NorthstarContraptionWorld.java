package com.lightning.northstar.mixin.accessor;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ContraptionWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ContraptionWorld.class)
public interface NorthstarContraptionWorld {

    @Accessor(value = "contraption", remap = false)
    Contraption northstar$getContraption();

}
