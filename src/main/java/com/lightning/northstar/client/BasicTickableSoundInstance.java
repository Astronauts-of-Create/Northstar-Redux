package com.lightning.northstar.client;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BasicTickableSoundInstance extends AbstractTickableSoundInstance {

    private static final float FADE_DISTANCE = 16.0f;

    private BlockEntity entity;

    public BasicTickableSoundInstance(SoundEvent soundEvent, SoundSource source, RandomSource random, BlockEntity entity) {
        super(soundEvent, source, random);
        this.entity = entity;
        this.relative = false;
        this.looping = false;
        setPos(entity.getBlockPos());
    }

    @Override
    public float getVolume() {
        var camera = net.minecraft.client.Minecraft.getInstance().cameraEntity;
        if (camera == null)
            return super.getVolume();
        double dist = camera.position().distanceTo(new Vec3(x, y, z));
        float multiplier = (float) net.minecraft.util.Mth.clampedMap(dist, 1.0, FADE_DISTANCE, 1.0, 0.0);
        return super.getVolume() * multiplier;
    }

    @Override
    public void tick() {
        if (entity.isRemoved()) {
            stop();
        }
    }

    public void cancel() {
        stop();
    }

    public void setPos(BlockPos pos) {
        x = pos.getX() + 0.5;
        y = pos.getY() + 0.5;
        z = pos.getZ() + 0.5;
    }

    public void setPos(Vec3 pos) {
        x = pos.x;
        y = pos.y;
        z = pos.z;
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
    }

}
