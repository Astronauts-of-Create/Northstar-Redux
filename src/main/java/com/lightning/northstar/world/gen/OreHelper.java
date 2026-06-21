package com.lightning.northstar.world.gen;

import com.lightning.northstar.data.TagHelper;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record OreHelper(BootstrapContext<PlacedFeature> context, RuleTest baseStone, RuleTest deepStone) {
    public OreHelper(BootstrapContext<PlacedFeature> context, TagHelper.Tag<Block> baseStone, TagHelper.Tag<Block> deepStone) {
        this(context, baseStone.tag(), deepStone.tag());
    }

    public OreHelper(BootstrapContext<PlacedFeature> context, TagKey<Block> baseStone, TagKey<Block> deepStone) {
        this(context, new TagMatchTest(baseStone), new TagMatchTest(deepStone));
    }

    public Builder builder(ResourceKey<PlacedFeature> key) {
        return new Builder(key);
    }

    public class Builder {
        private final ResourceKey<PlacedFeature> key;
        private int size = -1;
        private float discardChanceOnAir = 0.0f;
        private Block baseOre;
        private Block deepOre;
        private List<PlacementModifier> placement;

        public Builder(ResourceKey<PlacedFeature> key) {
            this.key = key;
        }

        public Builder sized(int size, float discardChanceOnAir) {
            this.size = size;
            this.discardChanceOnAir = discardChanceOnAir;
            return this;
        }

        public Builder blobSize() {
            return sized(64, 0);
        }

        public Builder ores(@Nullable Block base, @Nullable Block deep) {
            this.baseOre = base;
            this.deepOre = deep;
            return this;
        }

        public Builder ores(@Nullable BlockEntry<?> base, @Nullable BlockEntry<?> deep) {
            return ores(base == null ? null : base.get(), deep == null ? null : deep.get());
        }

        public Builder uniformPlacement(int count, VerticalAnchor minY, VerticalAnchor maxY) {
            return placement(
                    CountPlacement.of(count),
                    InSquarePlacement.spread(),
                    HeightRangePlacement.uniform(minY, maxY),
                    BiomeFilter.biome()
            );
        }

        public Builder trianglePlacement(int count, VerticalAnchor minY, VerticalAnchor maxY) {
            return placement(
                    CountPlacement.of(count),
                    InSquarePlacement.spread(),
                    HeightRangePlacement.triangle(minY, maxY),
                    BiomeFilter.biome()
            );
        }

        public Builder placement(PlacementModifier... placement) {
            return placement(List.of(placement));
        }

        public Builder placement(List<PlacementModifier> placement) {
            this.placement = placement;
            return this;
        }

        public void register() {
            if (size == -1) throw new IllegalStateException("No size defined");
            if (baseOre == null && deepOre == null) throw new IllegalStateException("No ore defined");
            if (placement == null) throw new IllegalStateException("No placement defined");

            List<OreConfiguration.TargetBlockState> targets = new ArrayList<>(2);
            if (baseOre != null) targets.add(OreConfiguration.target(baseStone, baseOre.defaultBlockState()));
            if (deepOre != null) targets.add(OreConfiguration.target(deepStone, deepOre.defaultBlockState()));
            ConfiguredFeature<?, ?> feature = new ConfiguredFeature<>(
                    Feature.ORE,
                    new OreConfiguration(targets, size, discardChanceOnAir)
            );

            context.register(key, new PlacedFeature(
                    Holder.direct(feature),
                    placement
            ));
        }
    }
}