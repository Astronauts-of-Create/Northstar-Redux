package com.lightning.northstar.advancements.predicate;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class GroupedEntitySubPredicate implements EntitySubPredicate {

    // Registered in EntitySubPredicateTypesMixin
    public static final EntitySubPredicate.Type TYPE = json -> {
        boolean any = json.get("any").getAsBoolean();
        JsonArray jsonArray = json.getAsJsonArray("predicates");
        EntitySubPredicate[] predicates = new EntitySubPredicate[jsonArray.size()];
        for (int i = 0; i < predicates.length; i++) {
            predicates[i] = EntitySubPredicate.fromJson(jsonArray.get(i));
        }
        return new GroupedEntitySubPredicate(any, predicates);
    };

    public static EntitySubPredicate anyOf(EntitySubPredicate... predicates) {
        return new GroupedEntitySubPredicate(true, predicates);
    }

    public static EntitySubPredicate allOf(EntitySubPredicate... predicates) {
        return new GroupedEntitySubPredicate(false, predicates);
    }

    private final boolean any;
    private final EntitySubPredicate[] predicates;

    private GroupedEntitySubPredicate(boolean any, EntitySubPredicate[] predicates) {
        this.any = any;
        this.predicates = predicates;
    }

    @Override
    public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position) {
        if (any) {
            for (EntitySubPredicate predicate : predicates) {
                if (predicate.matches(entity, level, position)) {
                    return true;
                }
            }
            return false;
        }

        for (EntitySubPredicate predicate : predicates) {
            if (!predicate.matches(entity, level, position)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public JsonObject serializeCustomData() {
        JsonArray jsonArray = new JsonArray();
        for (EntitySubPredicate predicate : predicates) {
            jsonArray.add(predicate.serialize());
        }
        JsonObject json = new JsonObject();
        json.addProperty("any", any);
        json.add("predicates", jsonArray);
        return json;
    }

    @Override
    public EntitySubPredicate.Type type() {
        return TYPE;
    }
}
