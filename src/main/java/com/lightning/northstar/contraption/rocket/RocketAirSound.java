package com.lightning.northstar.contraption.rocket;

import com.lightning.northstar.config.NorthstarConfigs;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RocketAirSound extends AbstractTickableSoundInstance {

    private final RocketContraptionEntity entity;

    public RocketAirSound(SoundEvent sound, RocketContraptionEntity entity) {
        super(sound, SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
        this.entity = entity;
        this.looping = true;
        this.relative = true;
        this.delay = 0;
    }

    @Override
    public void tick() {
        x = entity.getX();
        y = entity.getY();
        z = entity.getZ();

        float velocity = Math.abs(entity.getVelocity());
        volume = Mth.clamp(velocity / RocketContraptionEntity.AIR_SOUND_SPEED, 0f, 1f) * (1 - NorthstarConfigs.server().calculateAtmosphereBlend(entity.level(), y));
        pitch = Mth.clampedMap(velocity, 0f, RocketContraptionEntity.MAX_SPEED, 0.5f, 1.5f);

        if (!shouldPlayFor(entity) || entity.isRemoved()) {
            stop();
        }
    }

    public static boolean shouldPlayFor(RocketContraptionEntity entity) {
        return Math.abs(entity.getVelocity()) / RocketContraptionEntity.AIR_SOUND_SPEED >= Mth.EPSILON &&
               entity.level().northstar$dimension().hasAtmosphere();
    }

}
