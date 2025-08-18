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

public class RocketSmokeLandingParticleData implements ParticleOptions, ICustomParticleDataWithSprite<RocketSmokeLandingParticleData> {

    public static final StreamCodec<ByteBuf, RocketSmokeLandingParticleData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, p -> p.posX,
            ByteBufCodecs.VAR_INT, p -> p.posY,
            ByteBufCodecs.VAR_INT, p -> p.posZ,
            RocketSmokeLandingParticleData::new
    );

    public static final MapCodec<RocketSmokeLandingParticleData> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.optionalFieldOf("x", 0).forGetter(p -> p.posX),
            Codec.INT.optionalFieldOf("y", 0).forGetter(p -> p.posY),
            Codec.INT.optionalFieldOf("z", 0).forGetter(p -> p.posZ)
    ).apply(i, RocketSmokeLandingParticleData::new));

    public final int posX;
    public final int posY;
    public final int posZ;


    public RocketSmokeLandingParticleData(Vec3i pos) {
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    public RocketSmokeLandingParticleData(int posX, int posY, int posZ) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    public RocketSmokeLandingParticleData() {
        this(0, 0, 0);
    }

    @Override
    public ParticleType<?> getType() {
        return NorthstarParticles.ROCKET_SMOKE_LANDING.get();
    }

    @Override
    public MapCodec<RocketSmokeLandingParticleData> getCodec(ParticleType<RocketSmokeLandingParticleData> type) {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, RocketSmokeLandingParticleData> getStreamCodec() {
        return STREAM_CODEC;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public SpriteParticleRegistration<RocketSmokeLandingParticleData> getMetaFactory() {
        return RocketSmokeLandingParticle.Factory::new;
    }

}
