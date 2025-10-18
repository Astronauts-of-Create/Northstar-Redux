package com.lightning.northstar.particle;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;

public class RocketSmokeLandingParticle extends SimpleAnimatedParticle {

    protected RocketSmokeLandingParticle(SimpleParticleType data, ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprite) {
        super(world, x, y, z, sprite, world.random.nextFloat() * 20f);
        this.quadSize *= 1.5F;
        this.lifetime = 20;
        this.scale(5F);
        this.setSize(1.2F, 1.2F);
        double x_off = random.nextInt(2) * (random.nextBoolean() ? -1 : 1) * 0.1;
        double z_off = random.nextInt(2) * (random.nextBoolean() ? -1 : 1) * 0.1;
        this.xd += x_off;
        this.yd += 0.02;
        this.zd += z_off;
        hasPhysics = true;
        setSprite(sprites.get(7, 8));
        Vec3 offset = VecHelper.offsetRandomly(Vec3.ZERO, world.random, .00f);
        this.setPos(x + offset.x, y + offset.y - 2, z + offset.z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        setAlpha(0.6f);
    }

    @Override
    public float getQuadSize(float pScaleFactor) {
        //float f = ((float)this.age + pScaleFactor) / (float)this.lifetime;
        return this.quadSize;// * (1.0F - f * f * 0.5F);
    }

    @Override
    public int getLightColor(float partialTick) {
        return NorthstarParticles.getLight(super.getLightColor(partialTick), partialTick, age, lifetime);
    }

}
