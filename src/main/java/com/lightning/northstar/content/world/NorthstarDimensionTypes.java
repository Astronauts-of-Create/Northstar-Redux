package com.lightning.northstar.content.world;

import com.lightning.northstar.Northstar;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.OptionalLong;

public class NorthstarDimensionTypes {

    public static final ResourceKey<DimensionType>
            ORBIT = key("orbit"),
            MARS = key("mars"),
            MERCURY = key("mercury"),
            THE_MOON = key("moon"),
            VENUS = key("venus");

    public static void bootstrap(BootstrapContext<DimensionType> context) {
        context.register(ORBIT, new DimensionType(
                OptionalLong.empty(),
                true,
                false,
                false,
                false,
                1,
                true,
                true,
                -64,
                384,
                384,
                BlockTags.INFINIBURN_OVERWORLD,
                NorthstarDimensionEffects.ORBIT,
                0f,
                new DimensionType.MonsterSettings(
                        false,
                        false,
                        ConstantInt.of(0),
                        0
                )
        ));

        context.register(MARS, create(
                NorthstarDimensionEffects.MARS,
                false,
                384
        ));

        context.register(MERCURY, create(
                NorthstarDimensionEffects.SPACE,
                true,
                512
        ));

        context.register(THE_MOON, create(
                NorthstarDimensionEffects.SPACE,
                false,
                384
        ));

        context.register(VENUS, create(
                NorthstarDimensionEffects.VENUS,
                true,
                512
        ));
    }

    private static DimensionType create(ResourceLocation effects, boolean ultrawarm, int height) {
        return new DimensionType(
                OptionalLong.empty(),
                true,
                false,
                ultrawarm,
                true,
                1,
                true,
                false,
                -64,
                height,
                384,
                BlockTags.INFINIBURN_OVERWORLD,
                effects,
                0,
                new DimensionType.MonsterSettings(
                        false,
                        false,
                        UniformInt.of(0, 7),
                        0
                )
        );
    }

    private static ResourceKey<DimensionType> key(String path) {
        return ResourceKey.create(Registries.DIMENSION_TYPE, Northstar.asResource(path));
    }

}
