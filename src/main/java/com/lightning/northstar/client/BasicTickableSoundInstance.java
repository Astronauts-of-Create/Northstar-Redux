package com.lightning.northstar.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BasicTickableSoundInstance extends AbstractTickableSoundInstance {

    private BlockEntity entity;

    public BasicTickableSoundInstance(SoundEvent soundEvent, SoundSource source, RandomSource random, BlockEntity entity) {
        super(soundEvent, source, random);
        this.entity = entity;
        setPos(entity.getBlockPos());
    }

    public static BasicTickableSoundInstance playLoopingSound(BlockEntity be, BasicTickableSoundInstance sound, boolean active, SoundEvent soundEvent) {
        if (active) {
            if (sound == null || sound.isStopped()) {
                sound = new BasicTickableSoundInstance(soundEvent, SoundSource.BLOCKS, SoundInstance.createUnseededRandom(), be);
                sound.setLooping(true);
                Minecraft.getInstance().getSoundManager().play(sound);
            }
        } else if (sound != null) {
            sound.cancel();
            sound = null;
        }
        return sound;
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
