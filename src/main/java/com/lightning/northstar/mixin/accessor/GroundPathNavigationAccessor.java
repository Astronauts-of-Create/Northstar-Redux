package com.lightning.northstar.mixin.accessor;

import com.lightning.northstar.accessor.NorthstarGroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GroundPathNavigation.class)
public class GroundPathNavigationAccessor implements NorthstarGroundPathNavigation {

    @Shadow
    private boolean avoidSun;

    @Override
    public boolean northstar$isAvoidSun() {
        return avoidSun;
    }

}
