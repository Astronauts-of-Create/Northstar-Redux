package com.lightning.northstar.data;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.content.NorthstarPotatoProjectileEntityHitActions;
import com.simibubi.create.content.equipment.potatoCannon.PotatoCannonProjectileType;

public class NorthstarPotatoCannonProjectiles {

    public static void register() {
        new PotatoCannonProjectileType.Builder(Northstar.asResource("ice_cream"))
                .damage(5)
                .reloadTicks(10)
                .knockback(0.2f)
                .velocity(1.2f)
                .renderTowardMotion(220, 1)
                .onEntityHit(new NorthstarPotatoProjectileEntityHitActions.Freezing())
                .addItems(NorthstarItems.CHOCOLATE_ICE_CREAM, NorthstarItems.STRAWBERRY_ICE_CREAM, NorthstarItems.VANILLA_ICE_CREAM)
                .register();
    }

}
