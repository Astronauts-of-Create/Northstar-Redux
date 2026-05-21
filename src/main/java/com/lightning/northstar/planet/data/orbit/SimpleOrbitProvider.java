package com.lightning.northstar.planet.data.orbit;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.util.NorthstarCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3d;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @param durationDays       how long does it take for the orbit a full circle around the central body
 * @param radius             the orbit radius in AU
 * @param inclination        the orbit inclination in radians
 * @param ascendingNode      the ascending node angle in radians
 * @param initialMeanAnomaly the offset angle at the start of the world (base planets use J2000) in radians
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record SimpleOrbitProvider(
        double durationDays,
        double radius,
        double inclination,
        double ascendingNode,
        double initialMeanAnomaly
) implements OrbitProvider {

    public static final ResourceLocation TYPE = Northstar.asResource("simple");
    public static final Codec<SimpleOrbitProvider> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.DOUBLE.fieldOf("duration_days").forGetter(SimpleOrbitProvider::durationDays),
            Codec.DOUBLE.fieldOf("radius").forGetter(SimpleOrbitProvider::radius),
            NorthstarCodecs.DOUBLE_DEG_RAD.optionalFieldOf("inclination_deg", 0.0).forGetter(SimpleOrbitProvider::inclination),
            NorthstarCodecs.DOUBLE_DEG_RAD.optionalFieldOf("ascending_node_deg", 0.0).forGetter(SimpleOrbitProvider::ascendingNode),
            NorthstarCodecs.DOUBLE_DEG_RAD.optionalFieldOf("initial_mean_anomaly_deg", 0.0).forGetter(SimpleOrbitProvider::initialMeanAnomaly)
    ).apply(i, SimpleOrbitProvider::new));

    public static SimpleOrbitProvider create(double durationDays, double radius, double inclinationDeg, double ascendingNodeDeg, double initialMeanAnomalyDeg) {
        return new SimpleOrbitProvider(durationDays, radius, Math.toRadians(inclinationDeg), Math.toRadians(ascendingNodeDeg), Math.toRadians(initialMeanAnomalyDeg));
    }

    @Override
    public ResourceLocation type() {
        return TYPE;
    }

    @Override
    public double approximateRadius() {
        return radius;
    }

    @Override
    public double getVisualAngle(double deltaDays) {
        return Math.PI * 2 * deltaDays / (durationDays == 0 ? 1 : durationDays) + initialMeanAnomaly;
    }

    @Override
    public Vector3d getRotationAxis(Vector3d dest) {
        return dest.set(0, 1, 0)
                .rotateX(inclination)
                .rotateY(ascendingNode);
    }

    @Override
    public Vector3d calculatePosition(double deltaDays, Vector3d dest) {
        return dest.set(radius, 0, 0)
                .rotateY(getVisualAngle(deltaDays) - ascendingNode)
                .rotateX(inclination)
                .rotateY(ascendingNode);
    }

}
