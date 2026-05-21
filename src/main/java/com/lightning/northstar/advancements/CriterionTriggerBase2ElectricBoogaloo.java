package com.lightning.northstar.advancements;

import com.google.common.collect.Maps;
import com.lightning.northstar.Northstar;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class CriterionTriggerBase2ElectricBoogaloo<T extends CriterionTriggerBase2ElectricBoogaloo.Instance> implements CriterionTrigger<T> {

    private final ResourceLocation id;
    private final Map<PlayerAdvancements, Set<Listener<T>>> listeners = Maps.newHashMap();

    public CriterionTriggerBase2ElectricBoogaloo(String id) {
        this.id = Northstar.asResource(id);
    }

    @Override
    public void addPlayerListener(PlayerAdvancements playerAdvancementsIn, Listener<T> listener) {
        Set<Listener<T>> playerListeners = this.listeners.computeIfAbsent(playerAdvancementsIn, k -> new HashSet<>());

        playerListeners.add(listener);
    }

    @Override
    public void removePlayerListener(PlayerAdvancements playerAdvancementsIn, Listener<T> listener) {
        Set<Listener<T>> playerListeners = this.listeners.get(playerAdvancementsIn);
        if (playerListeners != null) {
            playerListeners.remove(listener);
            if (playerListeners.isEmpty()) {
                this.listeners.remove(playerAdvancementsIn);
            }
        }
    }

    @Override
    public void removePlayerListeners(PlayerAdvancements playerAdvancementsIn) {
        this.listeners.remove(playerAdvancementsIn);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    protected void trigger(ServerPlayer player, @Nullable List<Supplier<Object>> suppliers) {
        PlayerAdvancements playerAdvancements = player.getAdvancements();
        Set<Listener<T>> playerListeners = this.listeners.get(playerAdvancements);
        if (playerListeners != null) {
            List<Listener<T>> list = new LinkedList<>();

            for (Listener<T> listener : playerListeners) {
                if (listener.getTriggerInstance()
                        .test(suppliers)) {
                    list.add(listener);
                }
            }

            list.forEach(listener -> listener.run(playerAdvancements));

        }
    }

    public abstract static class Instance extends AbstractCriterionTriggerInstance {

        public Instance(ResourceLocation criterion, ContextAwarePredicate player) {
            super(criterion, player);
        }

        protected abstract boolean test(@Nullable List<Supplier<Object>> suppliers);
    }

}
