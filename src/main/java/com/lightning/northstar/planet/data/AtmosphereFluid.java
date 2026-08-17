package com.lightning.northstar.planet.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;

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
        DataComponentPatch fluidData,
        float collectionRate,
        boolean breathable
) {

    public static final float DEFAULT_COLLECTION_RATE = 10;

    public AtmosphereFluid {
        fluid = FluidHelper.convertToStill(fluid);
    }

    public AtmosphereFluid(Fluid fluid) {
        this(fluid, DataComponentPatch.EMPTY, DEFAULT_COLLECTION_RATE, true);
    }

    public AtmosphereFluid(Fluid fluid, DataComponentPatch fluidData) {
        this(fluid, fluidData, DEFAULT_COLLECTION_RATE, true);
    }

    public AtmosphereFluid(Fluid fluid, float collectionRate, boolean breathable) {
        this(fluid, DataComponentPatch.EMPTY, collectionRate, breathable);
    }

    public AtmosphereFluid(RegistryEntry<Fluid, ? extends Fluid> fluid) {
        this(fluid.get());
    }

    public AtmosphereFluid(RegistryEntry<Fluid, ? extends Fluid> fluid, DataComponentPatch fluidData) {
        this(fluid.get(), fluidData);
    }

    public AtmosphereFluid(RegistryEntry<Fluid, ? extends Fluid> fluid, float collectionRate, boolean breathable) {
        this(fluid.get(), collectionRate, breathable);
    }

    public AtmosphereFluid(RegistryEntry<Fluid, ? extends Fluid> fluid, DataComponentPatch fluidData, float collectionRate, boolean breathable) {
        this(fluid.get(), fluidData, collectionRate, breathable);
    }

    public static final Codec<AtmosphereFluid> CODEC = RecordCodecBuilder.create(i -> i.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(AtmosphereFluid::fluid),
            DataComponentPatch.CODEC.optionalFieldOf("fluid_data", DataComponentPatch.EMPTY).forGetter(AtmosphereFluid::fluidData),
            Codec.FLOAT.optionalFieldOf("collection_rate", DEFAULT_COLLECTION_RATE).forGetter(AtmosphereFluid::collectionRate),
            Codec.BOOL.fieldOf("breathable").forGetter(AtmosphereFluid::breathable)
    ).apply(i, AtmosphereFluid::new));

    public FluidStack asFluidStack(int amount) {
        return new FluidStack(fluid.builtInRegistryHolder(), amount, fluidData);
    }

}
