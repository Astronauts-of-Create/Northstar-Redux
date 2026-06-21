package com.lightning.northstar.mixin.entity;

import com.lightning.northstar.accessor.NorthstarEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public class EntityMixin implements NorthstarEntity {

    @Override
    public void northstar$onResourceReload() {
    }

}
