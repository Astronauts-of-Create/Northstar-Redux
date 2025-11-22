package com.lightning.northstar.compat.kubejs.component;

import com.google.gson.JsonObject;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.StringUtilsWrapper;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public interface KubeCreateOutput {
    @Contract("_ -> new")
    static @NotNull ProcessingOutput of(ItemStack stack) {
        return new ProcessingOutput(stack, 1F);
    }

    @Contract("_, _ -> new")
    static @NotNull ProcessingOutput of(ItemStack stack, double c) {
        var chance = (float) Mth.clamp(c, 0.0, 1.0);
        return new ProcessingOutput(stack, chance);
    }

    private static @NotNull ProcessingOutput fromMapLike(Context cx, Object from, @NotNull Function<String, Object> getter, boolean nested) {
        var chance = (float) Mth.clamp(StringUtilsWrapper.parseDouble(getter.apply("chance"), 1.0), 0.0, 1.0);
        if (nested) {
            var output = ItemWrapper.wrap(cx, getter.apply("output"));
            return new ProcessingOutput(output, chance);
        } else {
            return new ProcessingOutput(ItemWrapper.wrap(cx, from), chance);
        }
    }

    @HideFromJS
    static ProcessingOutput wrapProcessingOutput(Context cx, @Nullable Object from) {
        return switch (from) {
            case null -> ProcessingOutput.EMPTY;
            case ProcessingOutput id -> id;
            case ItemStack s -> s.isEmpty() ? ProcessingOutput.EMPTY : new ProcessingOutput(s, 1F);
            case ItemLike i when i.asItem() == Items.AIR -> ProcessingOutput.EMPTY;
            case ItemLike i -> new ProcessingOutput(i.asItem(), 1, 1F);
            case JsonObject json when json.has("chance") -> fromMapLike(cx, json, json::get, json.has("output"));
            case Map<?, ?> map when map.containsKey("chance") -> fromMapLike(cx, map, map::get, map.containsKey("output"));
            default -> new ProcessingOutput(ItemWrapper.wrap(cx, from), 1F);
        };
    }
}
