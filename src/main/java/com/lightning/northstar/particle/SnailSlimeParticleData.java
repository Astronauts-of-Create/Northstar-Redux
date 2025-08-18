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

public class SnailSlimeParticleData implements ParticleOptions, ICustomParticleDataWithSprite<SnailSlimeParticleData> {

    public static final StreamCodec<ByteBuf, SnailSlimeParticleData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, p -> p.posX,
            ByteBufCodecs.VAR_INT, p -> p.posY,
            ByteBufCodecs.VAR_INT, p -> p.posZ,
            SnailSlimeParticleData::new
    );

    public static final MapCodec<SnailSlimeParticleData> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.optionalFieldOf("x", 0).forGetter(p -> p.posX),
            Codec.INT.optionalFieldOf("y", 0).forGetter(p -> p.posY),
            Codec.INT.optionalFieldOf("z", 0).forGetter(p -> p.posZ)
    ).apply(i, SnailSlimeParticleData::new));


    final int posX;
    final int posY;
    final int posZ;

    public SnailSlimeParticleData() {
        this(0, 0, 0);
    }

    public SnailSlimeParticleData(Vec3i pos) {
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    public SnailSlimeParticleData(int posX, int posY, int posZ) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    @Override
    public ParticleType<?> getType() {
        return NorthstarParticles.SNAIL_SLIME.get();
    }

    @Override
    public MapCodec<SnailSlimeParticleData> getCodec(ParticleType<SnailSlimeParticleData> type) {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, SnailSlimeParticleData> getStreamCodec() {
        return STREAM_CODEC;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public SpriteParticleRegistration<SnailSlimeParticleData> getMetaFactory() {
        return SnailSlimeParticle.Factory::new;
    }

}
