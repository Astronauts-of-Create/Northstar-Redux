package com.lightning.northstar.particle;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RocketPlumeParticle extends TextureSheetParticle {

    private final SpriteSet sprites;
    private boolean smoke;

    public RocketPlumeParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double vx, double vy, double vz, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.xd = vx;
        this.yd = vy;
        this.zd = vz;
        this.lifetime = 80;
        this.quadSize = 1;

        setSpriteFromAge(sprites);
    }

    // ????
    @Override
    public void tick() {
        super.tick();

        if (age >= lifetime * 3 / 13) {
            yd *= 0.9f;
            quadSize += onGround ? 0.05f : 0.1f;
        }

        if ((onGround || age >= lifetime * 3 / 13) && !smoke) {
            smoke = true;

            if (age < lifetime * 3 / 13) {
                age = lifetime * 3 / 13;
            }

            double sidewaysVelocity = onGround ? 0.2 : 0;

            if (onGround) {
                yd = 0;
            }

            float dir = random.nextFloat() * Mth.TWO_PI;
            xd = Mth.cos(dir) * sidewaysVelocity;
            zd = Mth.sin(dir) * sidewaysVelocity;
        }

        setSpriteFromAge(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

}
