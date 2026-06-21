package com.lightning.northstar.planet.data.orbit;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.util.NorthstarCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3d;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record PhysicalOrbitProvider(
        double gravitationalParameter,
        double semiMajorAxis,
        double eccentricity,
        double inclination,
        double ascendingNode,
        double argumentOfPeriapsis,
        double meanAnomaly
) implements OrbitProvider {

    public static final ResourceLocation TYPE = Northstar.asResource("physical");
    public static final MapCodec<PhysicalOrbitProvider> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.DOUBLE.fieldOf("gravitational_parameter").forGetter(PhysicalOrbitProvider::gravitationalParameter),
            Codec.DOUBLE.fieldOf("semi_major_axis").forGetter(PhysicalOrbitProvider::semiMajorAxis),
            Codec.DOUBLE.fieldOf("eccentricity").forGetter(PhysicalOrbitProvider::eccentricity),
            NorthstarCodecs.DOUBLE_DEG_RAD.fieldOf("inclination_deg").forGetter(PhysicalOrbitProvider::inclination),
            NorthstarCodecs.DOUBLE_DEG_RAD.fieldOf("ascending_node_deg").forGetter(PhysicalOrbitProvider::ascendingNode),
            NorthstarCodecs.DOUBLE_DEG_RAD.fieldOf("argument_of_periapsis_deg").forGetter(PhysicalOrbitProvider::argumentOfPeriapsis),
            NorthstarCodecs.DOUBLE_DEG_RAD.fieldOf("mean_anomaly_deg").forGetter(PhysicalOrbitProvider::meanAnomaly)
    ).apply(i, PhysicalOrbitProvider::new));

    public static PhysicalOrbitProvider create(double gravitationalParameter, double semiMajorAxis, double eccentricity,
                                               double inclinationDeg, double longitudeOfAscendingNodeDeg, double argumentOfPeriapsisDeg,
                                               double meanAnomalyDeg) {
        return new PhysicalOrbitProvider(gravitationalParameter, semiMajorAxis, eccentricity, Math.toRadians(inclinationDeg),
                Math.toRadians(longitudeOfAscendingNodeDeg), Math.toRadians(argumentOfPeriapsisDeg), Math.toRadians(meanAnomalyDeg));
    }

    @Override
    public ResourceLocation type() {
        return TYPE;
    }

    @Override
    public double approximateRadius() {
        return semiMajorAxis;
    }

    @Override
    public double getVisualAngle(double deltaDays) {
        double meanMotion = Math.sqrt(gravitationalParameter / Math.pow(semiMajorAxis, 3));
        return (this.meanAnomaly + meanMotion * deltaDays) % (Math.PI * 2);
    }

    @Override
    public Vector3d getRotationAxis(Vector3d dest) {
        return dest.set(0, 1, 0)
                .rotateX(inclination)
                .rotateY(ascendingNode);
    }

    @Override
    public Vector3d calculatePosition(double deltaDays, Vector3d dest) {
        if (semiMajorAxis <= 1.0E-08) {
            return dest.set(0);
        }

        double meanMotion = Math.sqrt(gravitationalParameter / Math.pow(semiMajorAxis, 3));

        double meanAnomaly = (this.meanAnomaly + meanMotion * deltaDays) % (Math.PI * 2);
        if (meanAnomaly < 0) meanAnomaly += Math.PI * 2;

        double eccentricAnomaly = kepler(meanAnomaly, eccentricity);

        double trueAnomaly = 2 * Math.atan2(
                Math.sqrt(1 + eccentricity) * Math.sin(eccentricAnomaly / 2),
                Math.sqrt(1 - eccentricity) * Math.cos(eccentricAnomaly / 2)
        );
        double distance = semiMajorAxis * (1 - eccentricity * Math.cos(eccentricAnomaly));

        double cosOmega = Math.cos(ascendingNode);
        double sinOmega = Math.sin(ascendingNode);
        double cosPeriapsisAnomaly = Math.cos(argumentOfPeriapsis + trueAnomaly);
        double sinPeriapsisAnomaly = Math.sin(argumentOfPeriapsis + trueAnomaly);
        double cosIncl = Math.cos(inclination);

        return dest.set(
                distance * (cosOmega * cosPeriapsisAnomaly - sinOmega * sinPeriapsisAnomaly * cosIncl),
                distance * (sinPeriapsisAnomaly * Math.sin(inclination)),
                distance * (sinOmega * cosPeriapsisAnomaly + cosOmega * sinPeriapsisAnomaly * cosIncl)
        );
    }

    /** @see <a href="https://en.wikipedia.org/wiki/Kepler%27s_equation#Equation">Kepler's equation</a> */
    private double kepler(double M, double e) {
        double E = e < 0.8 ? M : Math.PI;
        for (int i = 0; i < 32; i++) {
            double delta = (E - e * Math.sin(E) - M) / (1 - e * Math.cos(E));
            E -= delta;
            if (Math.abs(delta) < 0.00001) {
                break;
            }
        }
        return E;
    }

}
