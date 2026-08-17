package com.lightning.northstar.planet.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

/**
 *
 * @param fluid          the fluid
 * @param fluidData      the extra data associated with the fluid
 * @param collectionRate the collection rate in mB/t for an atmospheric concentrator running at 256 RPM
 * @param breathable     if this fluid is considered dense enough to be breathed by entities
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record AtmosphereFluid(
        Fluid fluid,
        @Nullable CompoundTag fluidData,
        float collectionRate,
        boolean breathable
) {

    public static final float DEFAULT_COLLECTION_RATE = 10;

    public AtmosphereFluid {
        fluid = FluidHelper.convertToStill(fluid);
    }

    public AtmosphereFluid(Fluid fluid) {
        this(fluid, null, DEFAULT_COLLECTION_RATE, true);
    }

    public AtmosphereFluid(Fluid fluid, CompoundTag fluidData) {
        this(fluid, fluidData, DEFAULT_COLLECTION_RATE, true);
    }

    public AtmosphereFluid(Fluid fluid, float collectionRate, boolean breathable) {
        this(fluid, null, collectionRate, breathable);
    }

    public AtmosphereFluid(RegistryEntry<? extends Fluid> fluid) {
        this(fluid.get());
    }

    public AtmosphereFluid(RegistryEntry<? extends Fluid> fluid, CompoundTag fluidData) {
        this(fluid.get(), fluidData);
    }

    public AtmosphereFluid(RegistryEntry<? extends Fluid> fluid, float collectionRate, boolean breathable) {
        this(fluid.get(), collectionRate, breathable);
    }

    public AtmosphereFluid(RegistryEntry<? extends Fluid> fluid, CompoundTag fluidData, float collectionRate, boolean breathable) {
        this(fluid.get(), fluidData, collectionRate, breathable);
    }

    public static final Codec<AtmosphereFluid> CODEC = RecordCodecBuilder.create(i -> i.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(AtmosphereFluid::fluid),
            CompoundTag.CODEC.optionalFieldOf("fluid_data").forGetter(f -> Optional.ofNullable(f.fluidData())),
            Codec.FLOAT.optionalFieldOf("collection_rate", DEFAULT_COLLECTION_RATE).forGetter(AtmosphereFluid::collectionRate),
            Codec.BOOL.fieldOf("breathable").forGetter(AtmosphereFluid::breathable)
    ).apply(i, (a, b, c, d) -> new AtmosphereFluid(a, b.orElse(null), c, d)));

    public FluidStack asFluidStack(int amount) {
        return new FluidStack(fluid, amount, fluidData);
    }

}
