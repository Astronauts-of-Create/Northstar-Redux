package com.lightning.northstar.content.world;

import com.lightning.northstar.Northstar;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.TrapezoidFloat;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.*;
import net.minecraft.world.level.levelgen.heightproviders.*;

public class NorthstarConfiguredCarvers {

    public static final ResourceKey<ConfiguredWorldCarver<?>>
            MARS_DRY_RIVER = key("mars_dry_river");

    private static ResourceKey<ConfiguredWorldCarver<?>> key(String path) {
        return ResourceKey.create(Registries.CONFIGURED_CARVER, Northstar.asResource(path));
    }

    public static void bootstrap(BootstapContext<ConfiguredWorldCarver<?>> context) {
        HolderGetter<Block> blocks = context.lookup(Registries.BLOCK);

        HolderSet<Block> replaceable = blocks.getOrThrow(
                TagKey.create(Registries.BLOCK, Northstar.asResource("natural_mars_blocks"))
        );

        context.register(
                MARS_DRY_RIVER,
                new ConfiguredWorldCarver<>(
                        WorldCarver.CANYON,
                        new CanyonCarverConfiguration(
                                0.045F,
                                UniformHeight.of(
                                        VerticalAnchor.absolute(10),
                                        VerticalAnchor.absolute(72)
                                ),
                                UniformFloat.of(0.3F, 0.6F),
                                VerticalAnchor.aboveBottom(8),
                                CarverDebugSettings.of(
                                        false,
                                        Blocks.AIR.defaultBlockState(),
                                        Blocks.WATER.defaultBlockState(),
                                        Blocks.LAVA.defaultBlockState(),
                                        Blocks.BARRIER.defaultBlockState()
                                ),
                                replaceable,
                                UniformFloat.of(-0.05F, 0.05F),
                                new CanyonCarverConfiguration.CanyonShapeConfiguration(
                                        UniformFloat.of(0.75F, 1.0F),
                                        TrapezoidFloat.of(2.0F, 4.0F, 1.0F),
                                        8,
                                        UniformFloat.of(2.5F, 4.0F),
                                        0.3F,
                                        0.0F
                                )
                        )
                )
        );
    }

}
