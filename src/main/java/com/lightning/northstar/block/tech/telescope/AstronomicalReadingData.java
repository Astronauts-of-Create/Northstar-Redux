package com.lightning.northstar.block.tech.telescope;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record AstronomicalReadingData(
        ResourceLocation originPlanet,
        ResourceLocation targetPlanet,
        float science,
        int day
) {

    public static final Codec<AstronomicalReadingData> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.fieldOf("origin").forGetter(AstronomicalReadingData::originPlanet),
            ResourceLocation.CODEC.fieldOf("target").forGetter(AstronomicalReadingData::targetPlanet),
            Codec.FLOAT.fieldOf("science").forGetter(AstronomicalReadingData::science),
            Codec.INT.fieldOf("day").forGetter(AstronomicalReadingData::day)
    ).apply(i, AstronomicalReadingData::new));

}
