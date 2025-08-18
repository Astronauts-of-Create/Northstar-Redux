package com.lightning.northstar.particle;

import com.simibubi.create.content.equipment.bell.BasicParticleData;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class DustCloudParticle extends SimpleAnimatedParticle {

    protected DustCloudParticle(ClientLevel world, double x, double y, double z, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet sprite) {
        super(world, x, y, z, sprite, 0.25f);
        this.quadSize *= 0.75F;
        this.lifetime = 60;
        this.scale(15);
        this.setSize(6F, 6F);
        this.setBoundingBox(new AABB(0.1, 0.1, 0.1, -0.1, -0.1, -0.1));
        this.xd += 0.25;
        this.yd += 0.02;
        this.zd -= 0.25;
        hasPhysics = true;
        selectSprite(7);
        Vec3 offset = VecHelper.offsetRandomly(Vec3.ZERO, world.random, .00f);
        this.setPos(x + offset.x, y + offset.y, z + offset.z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        setAlpha(0.2f);
    }

    @Override
    public void tick() {
        super.tick();
        x += 0.25;
        z -= 0.25;
        this.setAlpha(0.2f);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float partialTick) {
        BlockPos blockpos = BlockPos.containing(x, y, z);
        return this.level.isLoaded(blockpos) ? LevelRenderer.getLightColor(level, blockpos) : 0;
    }

    private void selectSprite(int index) {
        setSprite(sprites.get(index, 8));
    }

    @Override
    public float getQuadSize(float pScaleFactor) {
        float f = ((float) this.age + pScaleFactor) / (float) this.lifetime;
        return this.quadSize * (1.0F - f * f * 0.5F);
    }

    public static class Factory implements ParticleProvider<DustCloudParticleData> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet animatedSprite) {
            this.spriteSet = animatedSprite;
        }

        @Override
        public Particle createParticle(DustCloudParticleData data, ClientLevel worldIn, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new DustCloudParticle(worldIn, x, y, z, zSpeed, zSpeed, zSpeed, this.spriteSet);
        }
    }

    public static class Data extends BasicParticleData<DustCloudParticle> {
        @Override
        public IBasicParticleFactory<DustCloudParticle> getBasicFactory() {
            return DustCloudParticle::new;
        }

        @Override
        public ParticleType<?> getType() {
            return NorthstarParticles.DUST_CLOUD.get();
        }
    }

}

