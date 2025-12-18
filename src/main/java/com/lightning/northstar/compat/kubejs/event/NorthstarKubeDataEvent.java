package com.lightning.northstar.compat.kubejs.event;

import com.google.gson.JsonElement;
import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarRegistries;
import com.lightning.northstar.contraption.FuelType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.generator.KubeDataGenerator;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.StringUtilsWrapper;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class NorthstarKubeDataEvent implements KubeEvent {

    private final KubeDataGenerator generator;
    private final List<Runnable> actions = new ArrayList<>();

    public NorthstarKubeDataEvent(KubeDataGenerator generator) {
        this.generator = generator;
    }

    public FuelType.Builder fuel() {
        return fuel((ResourceLocation) null);
    }

    @Info(value = "Defines a fuel type", params = {
            @Param(name = "path", value = "The name of the fuel type")
    })
    public FuelType.Builder fuel(@Nullable ResourceLocation path) {
        FuelType.Builder builder = FuelType.builder();
        actions.add(() -> fuel(builder.build(), path));
        return builder;
    }

    public void fuel(FuelType.Builder fuel) {
        fuel(fuel, null);
    }

    @Info(value = "Defines a fuel type", params = {
            @Param(name = "fuel", value = "The fuel properties"),
            @Param(name = "path", value = "The name of the fuel type")
    })
    public void fuel(FuelType.Builder fuel, @Nullable ResourceLocation path) {
        fuel(fuel.build(), path);
    }

    public void fuel(FuelType fuel) {
        fuel(fuel, null);
    }

    @Info(value = "Defines a fuel type", params = {
            @Param(name = "fuel", value = "The fuel properties"),
            @Param(name = "path", value = "The name of the fuel type")
    })
    public void fuel(FuelType fuel, @Nullable ResourceLocation path) {
        add(path, fuel, FuelType.CODEC, NorthstarRegistries.FUEL);
    }

    private <T> void add(@Nullable ResourceLocation path, T value, Codec<T> codec, ResourceKey<? extends Registry<T>> registry) {
        JsonElement encoded = codec.encodeStart(JsonOps.INSTANCE, value).getOrThrow();
        if (path == null) {
            path = ResourceLocation.fromNamespaceAndPath(Northstar.MOD_ID + "_kjs", StringUtilsWrapper.getUniqueId(encoded));
        }
        generator.json(location(registry, path), encoded);
    }

    private static ResourceLocation location(ResourceKey<? extends Registry<?>> registry, ResourceLocation path) {
        ResourceLocation reg = registry.location();
        String prefix = reg.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE) ? "" : reg.getNamespace() + "/";
        return ResourceLocation.fromNamespaceAndPath(path.getNamespace(), prefix + reg.getPath() + "/" + path.getPath());
    }

    @HideFromJS
    public void postProcess() {
        actions.forEach(Runnable::run);
    }

}
