package com.lightning.northstar.planet.data.func;

import com.lightning.northstar.Northstar;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.createmod.catnip.lang.Lang;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record LightFunction(Mode mode, boolean normalize) implements LevelFunction {

    public static final ResourceLocation TYPE = Northstar.asResource("light");
    public static final Codec<LightFunction> CODEC = RecordCodecBuilder.create(i -> i.group(
            Mode.CODEC.fieldOf("mode").forGetter(LightFunction::mode),
            Codec.BOOL.optionalFieldOf("normalize", false).forGetter(LightFunction::normalize)
    ).apply(i, LightFunction::new));

    @Override
    public ResourceLocation type() {
        return TYPE;
    }

    @Override
    public float get(Level level, BlockPos pos) {
        float value = get(level, pos, mode);
        return normalize ? value / 15f : value;
    }

    public static int get(Level level, BlockPos pos, Mode mode) {
        return switch (mode) {
            case BLOCK -> level.getBrightness(LightLayer.BLOCK, pos);
            case SKY -> level.getBrightness(LightLayer.SKY, pos);
            case DARKEN -> level.getSkyDarken();
            case SKY_DARKEN -> Math.max(0, level.getBrightness(LightLayer.SKY, pos) - level.getSkyDarken());
            case MIN -> Math.min(level.getBrightness(LightLayer.SKY, pos), level.getBrightness(LightLayer.BLOCK, pos));
            case MAX -> Math.max(level.getBrightness(LightLayer.SKY, pos), level.getBrightness(LightLayer.BLOCK, pos));
        };
    }

    public enum Mode implements StringRepresentable {
        /** {@link LightLayer#SKY} */
        SKY,
        /** {@link LightLayer#BLOCK} */
        BLOCK,
        /** darken level, affected by weather and day-night cycle. {@link Level#getSkyDarken()} */
        DARKEN,
        /** {@link #SKY sky} - {@link #DARKEN darken}. Never less than zero. */
        SKY_DARKEN,
        /** Minimum of {@link #SKY sky} and {@link #BLOCK block} */
        MIN,
        /** Maximum of {@link #SKY sky} and {@link #BLOCK block} */
        MAX;

        public static final Codec<Mode> CODEC = StringRepresentable.fromEnumWithMapping(Mode::values, String::toLowerCase);

        @Override
        public String getSerializedName() {
            return Lang.asId(name());
        }
    }

}
