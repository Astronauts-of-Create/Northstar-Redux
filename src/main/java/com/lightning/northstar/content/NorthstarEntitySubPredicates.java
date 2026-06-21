package com.lightning.northstar.content;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.advancements.predicate.OnGroundEntitySubPredicate;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NorthstarEntitySubPredicates {

    public static final DeferredRegister<MapCodec<? extends EntitySubPredicate>> REGISTER = DeferredRegister.create(Registries.ENTITY_SUB_PREDICATE_TYPE, Northstar.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends EntitySubPredicate>, MapCodec<OnGroundEntitySubPredicate>> ON_GROUND = REGISTER.register("on_ground", () -> OnGroundEntitySubPredicate.CODEC);

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

}
