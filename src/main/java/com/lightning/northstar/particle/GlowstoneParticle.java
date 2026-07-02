package com.lightning.northstar.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class GlowstoneParticle extends SimpleAnimatedParticle {

    protected GlowstoneParticle(SimpleParticleType data, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprite) {
        super(level, x, y, z, sprite, level.random.nextFloat() * 0.25f + 0.25f);
        quadSize *= 0.75f;
        lifetime = 40;
        setSpriteFromAge(sprite);
    }

}
