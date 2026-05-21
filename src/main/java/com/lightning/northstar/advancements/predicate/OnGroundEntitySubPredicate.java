package com.lightning.northstar.advancements.predicate;

import com.google.gson.JsonObject;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

// Wish it was a thing in EntityFlagsPredicate... but since it ain't this hacky solution is needed
// Other options would include injecting it into EntityFlagsPredicate or using a fully custom trigger.
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class OnGroundEntitySubPredicate implements EntitySubPredicate {

    public static final OnGroundEntitySubPredicate INSTANCE = new OnGroundEntitySubPredicate();
    // Registered in EntitySubPredicateTypesMixin
    public static final Type TYPE = json -> INSTANCE;

    private OnGroundEntitySubPredicate() {
    }

    @Override
    public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position) {
        return entity.onGround();
    }

    @Override
    public JsonObject serializeCustomData() {
        return new JsonObject();
    }

    @Override
    public Type type() {
        return TYPE;
    }

}
