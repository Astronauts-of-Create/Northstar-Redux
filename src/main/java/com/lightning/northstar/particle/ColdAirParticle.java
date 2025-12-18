package com.lightning.northstar.particle;

import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;

public class ColdAirParticle extends SimpleAnimatedParticle {

    protected ColdAirParticle(SimpleParticleType data, ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprite) {
        super(world, x, y, z, sprite, world.random.nextFloat() * .1f);
        this.quadSize *= 0.75F;
        this.lifetime = 70;
        this.scale(3F);
        this.setSize(0.25F, 0.25F);
        double x_off = random.nextInt(2) * (random.nextBoolean() ? -1 : 1) * 0.01;
        double z_off = random.nextInt(2) * (random.nextBoolean() ? -1 : 1) * 0.01;
        this.xd += x_off;
        this.yd += 0.02;
        this.zd += z_off;
        hasPhysics = true;
        setSprite(sprites.get(7, 8));
        Vec3 offset = VecHelper.offsetRandomly(Vec3.ZERO, world.random, .15f);
        this.setPos(x + offset.x, y + offset.y - 0.3, z + offset.z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        setAlpha(0.6f);
    }

    @Override
    public float getQuadSize(float pScaleFactor) {
        float f = ((float) this.age + pScaleFactor) / (float) this.lifetime;
        return this.quadSize * (1.0F - f * f * 0.5F);
    }

    @Override
    public int getLightColor(float partialTick) {
        BlockPos blockpos = BlockPos.containing(x, y, z);
        return this.level.isLoaded(blockpos) ? LevelRenderer.getLightColor(level, blockpos) : 0;
    }

}
