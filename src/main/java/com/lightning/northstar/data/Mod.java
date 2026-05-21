package com.lightning.northstar.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.ModList;

import java.util.Optional;
import java.util.function.Supplier;

public interface Mod {

    String getModId();

    default ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(getModId(), path);
    }

    default boolean isLoaded() {
        return ModList.get().isLoaded(getModId());
    }

    default <T> Optional<T> runIfLoaded(Supplier<Supplier<T>> action) {
        return isLoaded() ? Optional.ofNullable(action.get().get()) : Optional.empty();
    }

    default void executeIfLoaded(Supplier<Runnable> action) {
        if (isLoaded()) {
            action.get().run();
        }
    }

    default Block getBlock(String id) {
        return BuiltInRegistries.BLOCK.get(loc(id));
    }

    default Item getItem(String id) {
        return BuiltInRegistries.ITEM.get(loc(id));
    }

}
