package com.lightning.northstar.content;

import com.lightning.northstar.Northstar;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileEntityHitAction;
import com.simibubi.create.api.registry.CreateRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NorthstarPotatoProjectileEntityHitActions {

    public static final DeferredRegister<MapCodec<? extends PotatoProjectileEntityHitAction>> REGISTER =
            DeferredRegister.create(CreateRegistries.POTATO_PROJECTILE_ENTITY_HIT_ACTION, Northstar.MOD_ID);

    public static class Freezing implements PotatoProjectileEntityHitAction {
        public static final Freezing INSTANCE = new Freezing();
        public static final MapCodec<Freezing> CODEC = MapCodec.unit(INSTANCE);

        @Override
        public boolean execute(ItemStack projectile, EntityHitResult ray, Type type) {
            Entity entity = ray.getEntity();
            entity.setTicksFrozen(Math.max(entity.getTicksFrozen(), entity.getTicksRequiredToFreeze()) + 200);
            return false;
        }

        @Override
        public MapCodec<? extends PotatoProjectileEntityHitAction> codec() {
            return CODEC;
        }
    }

    public static void register(IEventBus eventBus) {
        REGISTER.register("freezing", () -> Freezing.CODEC);

        REGISTER.register(eventBus);
    }

}
