package com.lightning.northstar.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.particle.ParticleEngine.SpriteParticleRegistration;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class RocketFlameLandingParticleData implements ParticleOptions, ICustomParticleDataWithSprite<RocketFlameLandingParticleData> {

    public static final StreamCodec<ByteBuf, RocketFlameLandingParticleData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, p -> p.posX,
            ByteBufCodecs.VAR_INT, p -> p.posY,
            ByteBufCodecs.VAR_INT, p -> p.posZ,
            RocketFlameLandingParticleData::new
    );

    public static final MapCodec<RocketFlameLandingParticleData> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.optionalFieldOf("x", 0).forGetter(p -> p.posX),
            Codec.INT.optionalFieldOf("y", 0).forGetter(p -> p.posY),
            Codec.INT.optionalFieldOf("z", 0).forGetter(p -> p.posZ)
    ).apply(i, RocketFlameLandingParticleData::new));

    public final int posX;
    public final int posY;
    public final int posZ;

    public RocketFlameLandingParticleData() {
        this(0, 0, 0);
    }

    public RocketFlameLandingParticleData(Vec3i pos) {
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    public RocketFlameLandingParticleData(int posX, int posY, int posZ) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    @Override
    public ParticleType<?> getType() {
        return NorthstarParticles.ROCKET_FLAME_LANDING.get();
    }

    @Override
    public MapCodec<RocketFlameLandingParticleData> getCodec(ParticleType<RocketFlameLandingParticleData> type) {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, RocketFlameLandingParticleData> getStreamCodec() {
        return STREAM_CODEC;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public SpriteParticleRegistration<RocketFlameLandingParticleData> getMetaFactory() {
        return RocketFlameLandingParticle.Factory::new;
    }

}
