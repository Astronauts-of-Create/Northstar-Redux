package com.lightning.northstar.planet.data.orbit;

import com.lightning.northstar.Northstar;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3d;
import org.joml.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record FixedOrbitProvider(Vector3f position) implements OrbitProvider {

    public static final ResourceLocation TYPE = Northstar.asResource("fixed");
    public static final Codec<FixedOrbitProvider> CODEC = RecordCodecBuilder.create(i -> i.group(
            ExtraCodecs.VECTOR3F.fieldOf("position").forGetter(FixedOrbitProvider::position)
    ).apply(i, FixedOrbitProvider::new));

    @Override
    public ResourceLocation type() {
        return TYPE;
    }

    @Override
    public double getVisualAngle(double deltaDays) {
        return 0;
    }

    @Override
    public Vector3d getRotationAxis(Vector3d dest) {
        return dest.set(0, 1, 0);
    }

    @Override
    public double approximateRadius() {
        return position.length();
    }

    @Override
    public Vector3d calculatePosition(double deltaDays, Vector3d dest) {
        return position.get(dest);
    }

}
