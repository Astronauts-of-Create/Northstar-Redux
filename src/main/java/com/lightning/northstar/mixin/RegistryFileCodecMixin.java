package com.lightning.northstar.mixin;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.NorthstarContentRemapper;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;
import java.util.function.Supplier;

// TODO: Is this required now that registries support addAlias
@Mixin(RegistryFileCodec.class)
public class RegistryFileCodecMixin<E> {

    @ModifyReceiver(
            method = "decode",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Optional;orElseGet(Ljava/util/function/Supplier;)Ljava/lang/Object;"
            )
    )
    private Optional<DataResult<Holder.Reference<E>>> northstar$remap(
            Optional<DataResult<Holder.Reference<E>>> instance,
            Supplier<DataResult<Holder.Reference<E>>> supplier,
            @Local HolderGetter<E> getter,
            @Local ResourceKey<E> key) {
        if (instance.isPresent()) {
            return instance;
        }
        ResourceKey<E> remapped = NorthstarContentRemapper.remap(key);
        if (remapped != key) {
            Optional<Holder.Reference<E>> ref = getter.get(remapped);
            if (ref.isPresent()) {
                Northstar.LOGGER.warn("Remapping '{}' to '{}' for registry '{}'", key.location(), remapped.location(), key.registry());
                return Optional.of(DataResult.success(ref.get()));
            }
        }
        return instance;
    }

}
