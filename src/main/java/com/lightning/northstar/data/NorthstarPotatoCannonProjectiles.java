package com.lightning.northstar.data;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.content.NorthstarPotatoProjectileEntityHitActions;
import com.simibubi.create.api.equipment.potatoCannon.PotatoCannonProjectileType;
import com.simibubi.create.api.registry.CreateRegistries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

public class NorthstarPotatoCannonProjectiles {

    public static void boostrap(BootstrapContext<PotatoCannonProjectileType> context) {
        register(context, "ice_cream", new PotatoCannonProjectileType.Builder()
                .damage(5)
                .reloadTicks(10)
                .knockback(0.2f)
                .velocity(1.2f)
                .renderTowardMotion(220, 1)
                .onEntityHit(NorthstarPotatoProjectileEntityHitActions.Freezing.INSTANCE)
                .addItems(NorthstarItems.CHOCOLATE_ICE_CREAM, NorthstarItems.STRAWBERRY_ICE_CREAM, NorthstarItems.VANILLA_ICE_CREAM)
                .build());
    }

    private static void register(BootstrapContext<PotatoCannonProjectileType> context, String name, PotatoCannonProjectileType type) {
        context.register(ResourceKey.create(CreateRegistries.POTATO_PROJECTILE_TYPE, Northstar.asResource(name)), type);
    }

}
