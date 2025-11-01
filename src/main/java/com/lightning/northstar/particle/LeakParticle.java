package com.lightning.northstar.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class LeakParticle extends SimpleAnimatedParticle {

    public static final int LIFETIME = 10;

    protected LeakParticle(SimpleParticleType data, ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprite) {
        super(world, x, y, z, sprite, 0);
        friction = 1;
        lifetime = LIFETIME;
        hasPhysics = false;
        xd = xSpeed / LIFETIME;
        yd = ySpeed / LIFETIME;
        zd = zSpeed / LIFETIME;

        setSpriteFromAge(sprite);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

}
