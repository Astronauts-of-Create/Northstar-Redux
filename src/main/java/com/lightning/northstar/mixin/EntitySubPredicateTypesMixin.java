package com.lightning.northstar.mixin;

import com.google.common.collect.ImmutableBiMap;
import com.lightning.northstar.Northstar;
import com.lightning.northstar.advancements.predicate.GroupedEntitySubPredicate;
import com.lightning.northstar.advancements.predicate.OnGroundEntitySubPredicate;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntitySubPredicate.Types.class)
public class EntitySubPredicateTypesMixin {

    @ModifyExpressionValue(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/ImmutableBiMap;builder()Lcom/google/common/collect/ImmutableBiMap$Builder;"
            ),
            remap = false
    )
    private static ImmutableBiMap.Builder<String, EntitySubPredicate.Type> northstar$registerTypes(ImmutableBiMap.Builder<String, EntitySubPredicate.Type> original) {
        return original
                .put(Northstar.asResource("on_ground").toString(), OnGroundEntitySubPredicate.TYPE)
                .put(Northstar.asResource("group").toString(), GroupedEntitySubPredicate.TYPE);
    }

}
