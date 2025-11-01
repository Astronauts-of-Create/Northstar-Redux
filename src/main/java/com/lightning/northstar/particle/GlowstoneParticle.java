package com.lightning.northstar.particle;

import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;

public class GlowstoneParticle extends SimpleAnimatedParticle {

    protected GlowstoneParticle(SimpleParticleType data, ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprite) {
        super(world, x, y, z, sprite, world.random.nextFloat() * .5f);
        this.quadSize *= 0.75F;
        this.lifetime = 40;
        hasPhysics = false;
        setSprite(sprites.get(7, 8));
        Vec3 offset = VecHelper.offsetRandomly(Vec3.ZERO, world.random, .25f);
        this.setPos(x + offset.x, y + offset.y, z + offset.z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        setAlpha(.25f);
    }

    @Override
    public void move(double pX, double pY, double pZ) {
    }

    @Override
    public float getQuadSize(float pScaleFactor) {
        float f = ((float) this.age + pScaleFactor) / (float) this.lifetime;
        return this.quadSize * (1.0F - f * f * 0.5F);
    }

    @Override
    public int getLightColor(float partialTick) {
        return NorthstarParticles.getLight(super.getLightColor(partialTick), partialTick, age, lifetime);
    }

}
