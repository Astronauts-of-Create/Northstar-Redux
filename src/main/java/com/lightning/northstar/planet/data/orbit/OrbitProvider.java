package com.lightning.northstar.planet.data.orbit;

import com.lightning.northstar.util.SimpleRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.joml.Vector3d;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface OrbitProvider {

    SimpleRegistry<ResourceLocation, Codec<? extends OrbitProvider>> REGISTRY = new SimpleRegistry<>();

    Codec<OrbitProvider> CODEC = ResourceLocation.CODEC.dispatch(OrbitProvider::type, REGISTRY.lookup("orbit codec"));

    @ApiStatus.Internal
    static void register() {
        OrbitProvider.REGISTRY.register(FixedOrbitProvider.TYPE, FixedOrbitProvider.CODEC);
        OrbitProvider.REGISTRY.register(PhysicalOrbitProvider.TYPE, PhysicalOrbitProvider.CODEC);
        OrbitProvider.REGISTRY.register(SimpleOrbitProvider.TYPE, SimpleOrbitProvider.CODEC);
    }

    ResourceLocation type();

    /** Approximate average radius of the orbit, in AU */
    double approximateRadius();

    /** Calculates the visualFog angle in radians at the given time. Used for rendering in the orrery. */
    double getVisualAngle(double deltaDays);

    @Contract(value = "_, -> param1", mutates = "param1")
    Vector3d getRotationAxis(Vector3d dest);

    @Contract(value = "_, _ -> param2", mutates = "param2")
    Vector3d calculatePosition(double deltaDays, Vector3d dest);

}
