package com.lightning.northstar.advancements.predicate;

import com.mojang.serialization.MapCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class OnGroundEntitySubPredicate implements EntitySubPredicate {

    public static final OnGroundEntitySubPredicate INSTANCE = new OnGroundEntitySubPredicate();
    public static final MapCodec<OnGroundEntitySubPredicate> CODEC = MapCodec.unit(INSTANCE);

    private OnGroundEntitySubPredicate() {
    }

    @Override
    public MapCodec<? extends EntitySubPredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position) {
        return entity.onGround();
    }

}
