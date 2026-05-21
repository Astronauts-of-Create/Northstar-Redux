package com.lightning.northstar.mixin;

import com.lightning.northstar.accessor.NorthstarMutableComponent;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MutableComponent.class)
public class MutableComponentMixin implements NorthstarMutableComponent {
}
